package com.binaryLoyalty.server

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.mockito.stubbing.Answer
import java.time.LocalDate
import java.time.LocalTime

class GameServiceTest {

    private val gameRepo = mock<GameRepository>()
    private val timeService = mock<TimeService>()

    private val subject = GameService(gameRepo, timeService)

    private val fakeTime = LocalTime.now()

    private val fakeId = 5L

    @Before
    fun setup() {
        whenever(timeService.getCurrentTime()).thenReturn(fakeTime)
        whenever(gameRepo.save(any<Game>())).thenAnswer(
                Answer<Game> { i ->
                    val savedGame = i!!.getArgument<Game>(0)
                    if (savedGame.id == null) {
                        return@Answer savedGame.copy(id = fakeId)
                    }
                    return@Answer savedGame
                }
        )
    }


    @Test
    fun `can create new games`() {
        val actual = subject.createNewGame()
        verify(gameRepo).save(any<Game>())
        expect(actual.id).toBe(5)
        expect(actual.gameCode.length).toBe(5)
        expect(actual.lastModified).toBe(fakeTime)
        expect(actual.state).toBe(GameState.WAITING)
    }

    @Test
    fun `can retrieve games by code`() {
        val mockGame = Game("1")
        whenever(gameRepo.findByGameCode("ABC")).thenReturn(mockGame)
        expect(subject.findByGameCode("ABC")).toBe(mockGame)
    }

    @Test
    fun `can update game state`() {
        val mockGame = Game("1", lastModified = LocalTime.of(1, 1, 1), id = 1L)
        whenever(gameRepo.findByGameCode("ABC")).thenReturn(mockGame)

        val expectedGame = mockGame.copy(lastModified = fakeTime, state = GameState.GETTING_READY)

        val actual = subject.updateState("ABC", GameState.GETTING_READY)
        verify(gameRepo).save(expectedGame)
        expect(actual).toBe(expectedGame)
    }
}
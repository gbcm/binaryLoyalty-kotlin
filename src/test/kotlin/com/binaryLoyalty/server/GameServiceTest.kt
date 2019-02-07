package com.binaryLoyalty.server

import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.*
import org.junit.Before
import org.junit.Test
import org.mockito.ArgumentCaptor.forClass
import org.mockito.stubbing.Answer
import java.time.LocalTime

class GameServiceTest {

    private val gameRepo = mock<GameRepository>()
    private val timeService = mock<TimeService>()
    private val playerRepo = mock<PlayerRepository>()

    private val subject = GameService(gameRepo, timeService, playerRepo)

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

    @Test
    fun `can assign loyalties correctly for 6`() {
        //Assemble
        val gameCode = "12A"
        val game = Game(gameCode)
        val player1 = Player("1", game)
        val player2 = Player("2", game)
        val player3 = Player("3", game)
        val player4 = Player("4", game)
        val player5 = Player("5", game)
        val player6 = Player("6", game)
        whenever(playerRepo.findAllByGameGameCode(gameCode)).thenReturn(
                listOf(player1, player2, player3, player4, player5, player6)
        )
        //Act
        subject.assignLoyalties(gameCode)

        //Assert
        val captor = forClass<Player, Player>(Player::class.java)
        verify(playerRepo, times(6)).save<Player>(captor.capture())

        var betrayerCount = 0
        var loyalistCount = 0
        for (player in captor.allValues) {
            when(player.loyalty) {
                Loyalty.BETRAYER -> betrayerCount++
                Loyalty.LOYALIST -> loyalistCount++
                Loyalty.UNASSIGNED -> throw Exception()
            }
        }
        expect(betrayerCount).toBe(1)
        expect(loyalistCount).toBe(5)
    }

    @Test
    fun `can assign loyalties correctly for 7`() {
        //Assemble
        val gameCode = "12A"
        val game = Game(gameCode)
        val player1 = Player("1", game)
        val player2 = Player("2", game)
        val player3 = Player("3", game)
        val player4 = Player("4", game)
        val player5 = Player("5", game)
        val player6 = Player("6", game)
        val player7 = Player("7", game)
        whenever(playerRepo.findAllByGameGameCode(gameCode)).thenReturn(
                listOf(player1, player2, player3, player4, player5, player6, player7)
        )
        //Act
        subject.assignLoyalties(gameCode)

        //Assert
        val captor = forClass<Player, Player>(Player::class.java)
        verify(playerRepo, times(7)).save<Player>(captor.capture())

        var betrayerCount = 0
        var loyalistCount = 0
        for (player in captor.allValues) {
            when(player.loyalty) {
                Loyalty.BETRAYER -> betrayerCount++
                Loyalty.LOYALIST -> loyalistCount++
                Loyalty.UNASSIGNED -> throw Exception()
            }
        }
        expect(betrayerCount).toBe(2)
        expect(loyalistCount).toBe(5)
    }
}
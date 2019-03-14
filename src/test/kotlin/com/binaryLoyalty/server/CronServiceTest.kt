package com.binaryLoyalty.server

import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.verifyNoMoreInteractions
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import java.time.LocalDateTime

@RunWith(MockitoJUnitRunner.StrictStubs::class)
class CronServiceTest {

    private val gameService = mock<GameService>()
    private val playerRepo = mock<PlayerRepository>()

    val subject = CronService(gameService, playerRepo)

    @Test
    fun `starts games that are GETTING_READY for configurable seconds`() {
        //Assemble
        val now = LocalDateTime.now()
        val tenSecAgo = now.minusSeconds(10)
        val sixteenSecAgo = now.minusSeconds(16)

        val game1 = Game("1", GameState.STARTED, sixteenSecAgo)
        val game2 = Game("2", GameState.GETTING_READY, tenSecAgo)
        val game3 = Game("3", GameState.GETTING_READY, sixteenSecAgo)
        whenever(gameService.findAll()).thenReturn(listOf(game1, game2, game3))

        //Act
        subject.startReadyGames()

        //Assert
        verify(gameService).updateState("3",GameState.STARTED)
        verifyNoMoreInteractions(gameService)
    }

}


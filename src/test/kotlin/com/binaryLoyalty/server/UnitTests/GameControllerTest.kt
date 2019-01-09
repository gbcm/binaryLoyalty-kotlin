package com.binaryLoyalty.server.UnitTests

import com.binaryLoyalty.server.*
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.springframework.ui.Model
import org.springframework.ui.set
import java.util.*

class GameControllerTest {

    private val playerRepo = mock<PlayerRepository>()

    private val subject = GameController(playerRepo)

    private val model = mock<Model>()

    @Test
    fun `Joined players see other players in the same game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, 5)
        val playerId: Long = 2
        val player = Player("Bob", game, playerId)
        val playerList = listOf(
                player,
                Player("Cammy", game, 5),
                Player("Darren", game, 6)
        )
        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(playerRepo.findAllByGameGameCode(gameCode)).thenReturn(playerList)

        //Act
        val result = subject.getGame(model, playerId)

        //Assert
        expect(result).toBe("game")
        verify(model)["title"] = "Binary Loyalty"
        verify(model)["game"] = GamePresenter(gameCode, player, playerList)
    }
}
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
    private val gameRepo = mock<GameRepository>()

    private val subject = GameController(gameRepo, playerRepo)

    private val model = mock<Model>()

    @Test
    fun `Joined players see other players in the same game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, id = 5)
        val playerId: Long = 2
        val player = Player("Bob", game, id = playerId)
        val playerList = listOf(
                player,
                Player("Cammy", game, id = 5),
                Player("Darren", game, id = 6)
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

    @Test
    fun `Post to start game starts game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, id = 5)
        val readyGame = game.copy(state = GameState.GETTING_READY)
        val playerId: Long = 2
        val player = Player("Alice", game, id = playerId)
        val readyPlayer = Player("Alice", game, true, playerId)
        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameRepo.findByGameCode(gameCode)).thenReturn(game)

        //Act
        val result = subject.startGame(model, playerId)

        //Assert
        expect(result).toBe("redirect:/game?pid=$playerId")
        verify(gameRepo).save(readyGame)
        verify(playerRepo).save(readyPlayer)
    }

    @Test
    fun `Players see Readiness Prompt if game state is GETTING_READY but they are not ready`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, GameState.GETTING_READY, 5)
        val playerId: Long = 2
        val player = Player("Alice", game, false, playerId)

        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameRepo.findByGameCode(gameCode)).thenReturn(game)

        //Act
        val result = subject.getReadyPrompt(model, playerId)

        //Assert
        expect(result).toBe("getReady/prompt")
    }

    @Test
    fun `Players see Waiting Thing if game state is GETTING_READY and they are ready`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, GameState.GETTING_READY, 5)
        val playerId: Long = 2
        val player = Player("Alice", game, true, playerId)

        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameRepo.findByGameCode(gameCode)).thenReturn(game)

        //Act
        val result = subject.getReadyPrompt(model, playerId)

        //Assert
        expect(result).toBe("getReady/waiting")
    }

    @Test
    fun `Players see Start button if game state is WAITING`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, GameState.WAITING, 5)
        val playerId: Long = 2
        val player = Player(name = "Don't need this", game = game)

        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameRepo.findByGameCode(gameCode)).thenReturn(game)

        //Act
        val result = subject.getReadyPrompt(model, playerId)

        //Assert
        expect(result).toBe("getReady/start")
    }
}
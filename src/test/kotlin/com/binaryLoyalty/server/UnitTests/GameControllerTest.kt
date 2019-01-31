package com.binaryLoyalty.server.UnitTests

import com.binaryLoyalty.server.*
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Before
import org.junit.Test
import org.springframework.ui.Model
import org.springframework.ui.set
import java.time.LocalTime
import java.util.*


class GameControllerTest {

    private val playerRepo = mock<PlayerRepository>()
    private val gameService = mock<GameService>()

    private val timeService = mock<TimeService>()
    private val timeDiff = 10L
    private val fakeInitTime = LocalTime.now().minusSeconds(timeDiff)
    private val fakeTime = LocalTime.now()

    private val subject = GameController(gameService, playerRepo, timeService)
    private val model = mock<Model>()

    @Before
    fun setup() {
        whenever(timeService.getCurrentTime()).thenReturn(fakeTime)
    }

    @Test
    fun `Joined players see other players in the same game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, id = 5, lastModified = fakeInitTime)
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
        verify(model)["game"] = GamePresenter(gameCode, player, playerList, 15 - timeDiff)
    }

    @Test
    fun `Post to start game starts game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, id = 5, lastModified = fakeInitTime)
        val playerId: Long = 2
        val player = Player("Alice", game, id = playerId)
        val readyPlayer = Player("Alice", game, true, playerId)
        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameService.findByGameCode(gameCode)).thenReturn(game)

        //Act
        val result = subject.startGame(model, StartGame(playerId))

        //Assert
        expect(result).toBe("redirect:/game?pid=$playerId")
        verify(gameService).updateState(gameCode, GameState.GETTING_READY)
        verify(playerRepo).save(readyPlayer)
    }

    @Test
    fun `Players see Readiness Prompt if game state is GETTING_READY but they are not ready`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, GameState.GETTING_READY, id = 5, lastModified = fakeInitTime)
        val playerId: Long = 2
        val player = Player("Alice", game, false, playerId)
        val playerList = listOf(player)

        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameService.findByGameCode(gameCode)).thenReturn(game)
        whenever(playerRepo.findAllByGameGameCode(gameCode)).thenReturn(playerList)

        //Act
        val result = subject.getReadyPrompt(model, playerId)

        //Assert
        expect(result).toBe("getReady/prompt")
        verify(model)["title"] = "Binary Loyalty"
        verify(model)["game"] = GamePresenter(gameCode, player, playerList, 15 - timeDiff)
    }

    @Test
    fun `Players see Waiting Thing if game state is GETTING_READY and they are ready`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, GameState.GETTING_READY, id = 5, lastModified = fakeInitTime)
        val playerId: Long = 2
        val player = Player("Alice", game, true, playerId)
        val playerList = listOf(player)

        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameService.findByGameCode(gameCode)).thenReturn(game)
        whenever(playerRepo.findAllByGameGameCode(gameCode)).thenReturn(playerList)

        //Act
        val result = subject.getReadyPrompt(model, playerId)

        //Assert
        expect(result).toBe("getReady/waiting")
        verify(model)["title"] = "Binary Loyalty"
        verify(model)["game"] = GamePresenter(gameCode, player, playerList, 15 - timeDiff)
    }

    @Test
    fun `Players see Start button if game state is WAITING`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, GameState.WAITING, id = 5, lastModified = fakeInitTime)
        val playerId: Long = 2
        val player = Player(name = "Don't need this", game = game)
        val playerList = listOf(player)

        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameService.findByGameCode(gameCode)).thenReturn(game)
        whenever(playerRepo.findAllByGameGameCode(gameCode)).thenReturn(playerList)

        //Act
        val result = subject.getReadyPrompt(model, playerId)

        //Assert
        expect(result).toBe("getReady/start")
        verify(model)["title"] = "Binary Loyalty"
        verify(model)["game"] = GamePresenter(gameCode, player, playerList, 15 - timeDiff)
    }

    @Test
    fun `Players see a Started game thing, and game starts, if it has been more than 15 seconds`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, GameState.WAITING, id = 5, lastModified = fakeTime.minusSeconds(16))
        val playerId: Long = 2
        val player = Player(name = "Foo", game = game)
        val playerList = listOf(player)

        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(gameService.findByGameCode(gameCode)).thenReturn(game)
        whenever(gameService.updateState(gameCode, GameState.STARTED)).thenReturn(
                game.copy(state = GameState.STARTED))
        whenever(playerRepo.findAllByGameGameCode(gameCode)).thenReturn(playerList)

        //Act
        val result = subject.getReadyPrompt(model, playerId)

        //Assert
        expect(result).toBe("getReady/inProgress")
        verify(gameService).updateState(gameCode, GameState.STARTED)
        verify(model)["title"] = "Binary Loyalty"
        verify(model)["game"] = GamePresenter(gameCode, player, playerList, -1)
    }

    @Test
    fun `Unready player deletes player and redirects to lobby`() {
        //Assemble
        val playerId: Long = 2
        val player = Player(name = "Foo", game = Game(""), id= playerId)

        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))

        //Act
        val result = subject.unReadyPlayer(model, UnReadyPlayer(playerId))

        //Assert
        expect(result).toBe("redirect:/")
        verify(playerRepo).delete(player)
    }
}
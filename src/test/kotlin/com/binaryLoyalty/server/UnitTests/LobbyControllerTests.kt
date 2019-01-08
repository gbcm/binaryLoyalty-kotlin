package com.binaryLoyalty.server.UnitTests


import com.binaryLoyalty.server.*
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.junit.MockitoJUnitRunner
import org.springframework.ui.Model
import org.springframework.ui.set
import java.util.*

@RunWith(MockitoJUnitRunner::class)
class LobbyControllerTests {

    private val playerRepo = mock<PlayerRepository>()

    private val gameRepo = mock<GameRepository>()

    private val utils = mock<UtilService>()

    private val subject = LobbyController(playerRepo, gameRepo, utils)

    private val model = mock<Model>()

    @Test
    fun `Players can join games`() {
        //Assemble
        val game = Game(5, "ABC12")
        whenever(gameRepo.findByGameCode("ABC12")).thenReturn(game)
        val playerId: Long = 2
        whenever(playerRepo.save(Player(game = game))).thenReturn(Player(playerId, game))

        //Act
        val result = subject.postJoin(Game(gameCode = "ABC12"))

        //Assert
        expect(result).toBe("redirect:/?pid=$playerId")
        verify(playerRepo).save(Player(game = game))
    }

    @Test
    fun `Joined players see other players in the same game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(5, gameCode)
        val playerId: Long = 2
        val player = Player(playerId, game)
        val playerList = listOf(
                player,
                Player(5, game),
                Player(6, game)
        )
        whenever(playerRepo.findById(playerId)).thenReturn(Optional.of(player))
        whenever(playerRepo.findAllByGameGameCode(gameCode)).thenReturn(playerList)

        //Act
        val result = subject.getIndex(model, playerId)

        //Assert
        expect(result).toBe("lobby")
        verify(model)["title"] = "Binary Loyalty"
        verify(model)["game"] = GamePresenter(gameCode, playerList)
    }

    @Test
    fun `Start game creates a new player and game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(5, gameCode)
        val playerId: Long = 2
        whenever(utils.generateGameCode()).thenReturn(gameCode)
        whenever(gameRepo.save(Game(gameCode = gameCode))).thenReturn(game)
        whenever(playerRepo.save(Player(game = game))).thenReturn(Player(playerId, game))

        //Act
        val result = subject.postIndex()

        //Assert
        verify(gameRepo).save(Game(gameCode = gameCode))
        verify(playerRepo).save(Player(game = game))
        expect(result).toBe("redirect:/?pid=$playerId")
    }
}
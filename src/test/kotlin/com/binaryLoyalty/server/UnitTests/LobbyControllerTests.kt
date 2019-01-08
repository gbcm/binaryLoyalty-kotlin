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
    fun `Create game creates a new player and game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode,5)
        val playerId: Long = 2
        val name = "Alice"
        whenever(utils.generateGameCode()).thenReturn(gameCode)
        whenever(gameRepo.save(Game(gameCode))).thenReturn(game)
        whenever(playerRepo.save(Player(name, game))).thenReturn(Player(name, game, playerId))

        //Act
        val result = subject.postIndex(CreateGame(name))

        //Assert
        verify(gameRepo).save(Game(gameCode))
        verify(playerRepo).save(Player(name, game))
        expect(result).toBe("redirect:/?pid=$playerId")
    }

    @Test
    fun `Players can join games`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, 5)
        val playerId: Long = 2
        val name = "Alice"
        whenever(gameRepo.findByGameCode(gameCode)).thenReturn(game)
        whenever(playerRepo.save(Player(name, game))).thenReturn(Player(name, game, playerId))

        //Act
        val result = subject.postJoin(JoinGame(name, gameCode))

        //Assert
        expect(result).toBe("redirect:/?pid=$playerId")
        verify(playerRepo).save(Player(name, game))
    }

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
        val result = subject.getIndex(model, playerId)

        //Assert
        expect(result).toBe("lobby")
        verify(model)["title"] = "Binary Loyalty"
        verify(model)["game"] = GamePresenter(gameCode, player, playerList)
    }
}
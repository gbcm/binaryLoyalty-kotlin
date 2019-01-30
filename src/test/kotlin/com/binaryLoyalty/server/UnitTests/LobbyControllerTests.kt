package com.binaryLoyalty.server.UnitTests


import com.binaryLoyalty.server.*
import com.nhaarman.expect.expect
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import com.nhaarman.mockitokotlin2.whenever
import org.junit.Test
import org.springframework.ui.Model
import org.springframework.ui.set

class LobbyControllerTests {

    private val playerRepo = mock<PlayerRepository>()

    private val gameService = mock<GameService>()


    private val subject = LobbyController(playerRepo, gameService)

    private val model = mock<Model>()

    @Test
    fun `The index has a title`() {
        //Act
        subject.getIndex(model)

        //Assert
        verify(model)["title"] = "Binary Loyalty"
    }
    @Test
    fun `Create game creates a new player and game`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, id = 5)
        val playerId: Long = 2
        val name = "Alice"
        whenever(gameService.createNewGame()).thenReturn(game)
        whenever(playerRepo.save(Player(name, game))).thenReturn(Player(name, game, id=playerId))

        //Act
        val result = subject.postIndex(CreateGame(name))

        //Assert
        verify(gameService).createNewGame()
        verify(playerRepo).save(Player(name, game))
        expect(result).toBe("redirect:/game?pid=$playerId")
    }

    @Test
    fun `Players can join games`() {
        //Assemble
        val gameCode = "ABC12"
        val game = Game(gameCode, id = 5)
        val playerId: Long = 2
        val name = "Alice"
        whenever(gameService.findByGameCode(gameCode)).thenReturn(game)
        whenever(playerRepo.save(Player(name, game))).thenReturn(Player(name, game, id=playerId))

        //Act
        val result = subject.postJoin(JoinGame(name, gameCode))

        //Assert
        expect(result).toBe("redirect:/game?pid=$playerId")
        verify(playerRepo).save(Player(name, game))
    }


}
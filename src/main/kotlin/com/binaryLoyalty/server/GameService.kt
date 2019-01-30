package com.binaryLoyalty.server

import org.springframework.stereotype.Service
import java.util.*
import kotlin.streams.asSequence

@Service
class GameService(
        val gameRepo: GameRepository,
        val timeService: TimeService) {
    fun createNewGame(): Game {
        return gameRepo.save(Game(
                generateGameCode(),
                GameState.WAITING,
                timeService.getCurrentTime()))
    }

    fun updateState(gameCode: String, gameState: GameState): Game {
        val game = gameRepo.findByGameCode(gameCode)
        return gameRepo.save(game.copy(state = gameState, lastModified = timeService.getCurrentTime()))
     }

    fun findByGameCode(gameCode: String): Game {
        return gameRepo.findByGameCode(gameCode)
    }

    fun generateGameCode(): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return Random().ints(5, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
    }
}

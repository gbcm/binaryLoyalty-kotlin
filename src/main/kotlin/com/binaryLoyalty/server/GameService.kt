package com.binaryLoyalty.server

import org.springframework.stereotype.Service
import java.lang.Math.floor
import java.util.*
import kotlin.streams.asSequence

@Service
class GameService(
        val gameRepo: GameRepository,
        val timeService: TimeService,
        val playerRepo: PlayerRepository) {

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

    fun assignLoyalties(gameCode: String) {
        val players = playerRepo.findAllByGameGameCode(gameCode).shuffled()
        val numBetrayers = floor(.32 * players.size).toInt()
        for ((index, player) in players.withIndex()) {
            playerRepo.save(
                    player.copy(loyalty = when {
                        index < numBetrayers -> Loyalty.BETRAYER
                        else -> Loyalty.LOYALIST
                    }))
        }
    }

    fun generateGameCode(): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return Random().ints(5, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
    }

    fun findAll(): List<Game> {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }
}

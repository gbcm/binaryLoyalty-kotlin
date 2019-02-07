package com.binaryLoyalty.server

import org.springframework.stereotype.Service
import java.lang.Math.floor
import java.lang.Math.round
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
        val players = playerRepo.findAllByGameGameCode(gameCode)
        val loyaltyBag = mutableListOf<Loyalty>()
        val numBetrayers = round(floor(.32 * players.size))

        for (i in 1..players.size) {
            if (i <= numBetrayers) {
                loyaltyBag.add(Loyalty.BETRAYER)
            } else {
                loyaltyBag.add(Loyalty.LOYALIST)
            }
        }

        for (player in players) {
            val i = Random().nextInt(loyaltyBag.size)
            playerRepo.save(player.copy(loyalty = loyaltyBag[i]))
            loyaltyBag.removeAt(i)
        }
    }

    fun generateGameCode(): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return Random().ints(5, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
    }
}

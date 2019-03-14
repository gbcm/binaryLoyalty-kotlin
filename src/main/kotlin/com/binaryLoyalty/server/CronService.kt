package com.binaryLoyalty.server

import org.springframework.stereotype.Component
import org.springframework.scheduling.annotation.Scheduled
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit


@Component
class CronService(val gameService: GameService,
                  val playerRepo: PlayerRepository) {

//    @Scheduled(fixedRate = 5000)
    fun startReadyGames() {
        for (game in gameService.findAll()) {
            if (game.lastModified.until(LocalDateTime.now(), ChronoUnit.SECONDS) > 15 &&
                    game.state == GameState.GETTING_READY) {
                gameService.updateState(game.gameCode, GameState.STARTED)
            }
        }
    }
}
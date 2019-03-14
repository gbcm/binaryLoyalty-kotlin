package com.binaryLoyalty.server

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController


@RestController
class ApiController(
        val gameService: GameService,
        val playerRepo: PlayerRepository,
        val timeService: TimeService) {

    @GetMapping("/rando")
    fun rando(): Player {
        val game = gameService.createNewGame()
        val player = playerRepo.save(Player("Arglebarg", game))
        return player
    }
}
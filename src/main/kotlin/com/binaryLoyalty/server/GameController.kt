package com.binaryLoyalty.server

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class GameController(val playerRepo: PlayerRepository) {

    @GetMapping("/game")
    fun getGame(model: Model, @RequestParam pid: Long): String {
        val player = playerRepo.findById(pid).get()
        val gameCode = player.game.gameCode
        model["game"] = GamePresenter(
                gameCode,
                player,
                playerRepo.findAllByGameGameCode(gameCode))
        model["title"] = "Binary Loyalty"
        return "game"
    }
}


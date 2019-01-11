package com.binaryLoyalty.server

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class GameController(val gameRepo: GameRepository, val playerRepo: PlayerRepository) {

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

    @PostMapping("/game")
    fun startGame(model: Model, playerId: Long): String {
        val player = playerRepo.findById(playerId).get()
        playerRepo.save(player.copy(isReady = true))
        val game = gameRepo.findByGameCode(player.game.gameCode)
        gameRepo.save(game.copy(state = GameState.GETTING_READY))
        return "redirect:/game?pid=${player.id}"
    }

    fun getReadyPrompt(model: Model, playerId: Long): String {
        val player = playerRepo.findById(playerId).get()
        val game = gameRepo.findByGameCode(player.game.gameCode)
        return when {
            game.state == GameState.WAITING -> "getReady/start"
            game.state == GameState.GETTING_READY &&
                    player.isReady -> "getReady/waiting"
            else -> "getReady/prompt"
        }

    }


}


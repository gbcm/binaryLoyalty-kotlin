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
        updatePresenter(model, player.game.gameCode, player)
        return "game"
    }

    @PostMapping("/game")
    fun startGame(model: Model, form: StartGame): String {
        val player = playerRepo.findById(form.playerId).get()
        playerRepo.save(player.copy(isReady = true))
        val game = gameRepo.findByGameCode(player.game.gameCode)
        gameRepo.save(game.copy(state = GameState.GETTING_READY))
        return "redirect:/game?pid=${player.id}"
    }

    @GetMapping("/game/getReady")
    fun getReadyPrompt(model: Model, @RequestParam pid: Long): String {
        val player = playerRepo.findById(pid).get()
        val game = gameRepo.findByGameCode(player.game.gameCode)
        updatePresenter(model, player.game.gameCode, player)
        return when {
            game.state == GameState.WAITING -> "getReady/start"
            game.state == GameState.GETTING_READY &&
                    player.isReady -> "getReady/waiting"
            else -> "getReady/prompt"
        }

    }

    fun updatePresenter(model: Model, gameCode: String, player: Player) {
        model["game"] = GamePresenter(
                gameCode,
                player,
                playerRepo.findAllByGameGameCode(gameCode))
        model["title"] = "Binary Loyalty"
    }


}


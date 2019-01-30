package com.binaryLoyalty.server

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam
import java.time.temporal.ChronoUnit

@Controller
class GameController(
        val gameService: GameService,
        val playerRepo: PlayerRepository,
        val timeService: TimeService) {

    private val gameReadyTimeout = 15

    @GetMapping("/game")
    fun getGame(model: Model, @RequestParam pid: Long): String {
        val player = playerRepo.findById(pid).get()
        updatePresenter(model, player)
        return "game"
    }

    @PostMapping("/game")
    fun startGame(model: Model, form: StartGame): String {
        val player = playerRepo.findById(form.playerId).get()
        playerRepo.save(player.copy(isReady = true))
        gameService.updateState(player.game.gameCode, GameState.GETTING_READY)
        return "redirect:/game?pid=${player.id}"
    }

    @GetMapping("/game/getReady")
    fun getReadyPrompt(model: Model, @RequestParam pid: Long): String {
        val player = playerRepo.findById(pid).get()
        var game = gameService.findByGameCode(player.game.gameCode)
        if (game.lastModified.until(timeService.getCurrentTime(), ChronoUnit.SECONDS) > gameReadyTimeout) {
           game = gameService.updateState(game.gameCode, GameState.STARTED)
        }
        updatePresenter(model, player)
        return when {
            game.state == GameState.STARTED -> "getReady/inProgress"
            game.state == GameState.WAITING -> "getReady/start"
            game.state == GameState.GETTING_READY &&
                    player.isReady -> "getReady/waiting"
            else -> "getReady/prompt"
        }
    }

    fun updatePresenter(model: Model, player: Player) {
        model["game"] = GamePresenter(
                player.game.gameCode,
                player,
                playerRepo.findAllByGameGameCode(player.game.gameCode),
                gameReadyTimeout - player.game.lastModified.until(timeService.getCurrentTime(), ChronoUnit.SECONDS))
        model["title"] = "Binary Loyalty"
    }
}


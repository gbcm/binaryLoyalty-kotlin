package com.binaryLoyalty.server

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.*

@Controller
class LobbyController(
        val playerRepo: PlayerRepository,
        val gameRepo: GameRepository,
        val utils: UtilService) {


    @GetMapping("/")
    fun getIndex(model: Model, @RequestParam pid: Long?): String {
        if (pid != null) {
            val gameCode = playerRepo.findById(pid).get().game.gameCode
            model["game"] = GamePresenter(
                    gameCode,
                    playerRepo.findAllByGameGameCode(gameCode))
        }
        model["title"] = "Binary Loyalty"
        return "lobby"
    }

    @PostMapping("/")
    fun postIndex(): String {
        val game = gameRepo.save(Game(gameCode = utils.generateGameCode()))
        val player = playerRepo.save(Player(game = game))
        return "redirect:/?pid=${player.id}"
    }

    @PostMapping("/join")
    fun postJoin(@ModelAttribute postedGame: Game): String {
        val player = playerRepo.save(Player(game = gameRepo.findByGameCode(postedGame.gameCode)))
        return "redirect:/?pid=${player.id}"
    }
}


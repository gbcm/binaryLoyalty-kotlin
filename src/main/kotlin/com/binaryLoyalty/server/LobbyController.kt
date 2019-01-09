package com.binaryLoyalty.server

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestParam

@Controller
class LobbyController(
        val playerRepo: PlayerRepository,
        val gameRepo: GameRepository,
        val utils: UtilService) {

    @GetMapping("/")
    fun getIndex(model: Model): String {
        model["title"] = "Binary Loyalty"
        return "lobby"
    }

    @PostMapping("/")
    fun postIndex(@ModelAttribute form: CreateGame): String {
        val game = gameRepo.save(Game(utils.generateGameCode()))
        val player = playerRepo.save(Player(form.playerName, game))
        return "redirect:/game?pid=${player.id}"
    }

    @PostMapping("/join")
    fun postJoin(@ModelAttribute form: JoinGame): String {
        val player = playerRepo.save(Player(form.playerName, gameRepo.findByGameCode(form.gameCode)))
        return "redirect:/game?pid=${player.id}"
    }
}


package com.binaryLoyalty.server

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.ui.set
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import java.util.*
import kotlin.streams.asSequence

@Controller
class LobbyController {

    var gameCode = ""

    @GetMapping("/")
    fun getIndex(model: Model): String {
        model["title"] = "Binary Loyalty"
        model["gameCode"] = gameCode
        return "lobby"
    }

    @PostMapping("/")
    fun postIndex(): String {
        gameCode = generateGameCode()
        return "redirect:/"
    }

    @PostMapping("/join")
    fun postJoin(@ModelAttribute game: Game): String {
        gameCode = game.gameCode
        return "redirect:/"
    }

    private fun generateGameCode(): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return Random().ints(5, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
    }
}


package com.binaryLoyalty.server

import javax.persistence.GeneratedValue
import javax.persistence.Id

data class JoinGame(
        val playerName: String,
        val gameCode: String
)

data class CreateGame(
        val playerName: String
)

data class StartGame(
        val playerId: Long
)
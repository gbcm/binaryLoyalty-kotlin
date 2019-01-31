package com.binaryLoyalty.server

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

data class UnReadyPlayer(
    val playerId: Long
)
package com.binaryLoyalty.server

data class GamePresenter(
        val gameCode: String,
        val currentPlayer: Player,
        val players: List<Player>,
        val seconds: Long
)
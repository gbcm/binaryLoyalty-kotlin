package com.binaryLoyalty.server

import javax.persistence.GeneratedValue
import javax.persistence.Id

data class GamePresenter(
        val gameCode: String,
        val players: List<Player>
)
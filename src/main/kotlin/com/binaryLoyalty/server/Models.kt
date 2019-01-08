package com.binaryLoyalty.server

import org.springframework.data.repository.CrudRepository
import javax.persistence.*

interface GameRepository : CrudRepository<Game, Long> {
    fun findByGameCode(gameCode: String): Game
}

@Entity
data class Game(
        @Id @GeneratedValue val id: Long? = null,
        val gameCode: String
)

interface PlayerRepository : CrudRepository<Player, Long> {
    fun findAllByGameGameCode(gameCode: String): List<Player>
}

@Entity
data class Player(
        @Id @GeneratedValue val id: Long? = null,
        @ManyToOne @JoinColumn val game: Game
)

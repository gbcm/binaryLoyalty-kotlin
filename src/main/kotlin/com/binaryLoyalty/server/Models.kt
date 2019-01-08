package com.binaryLoyalty.server

import org.springframework.data.repository.CrudRepository
import javax.persistence.*

interface GameRepository : CrudRepository<Game, Long> {
    fun findByGameCode(gameCode: String): Game
}

@Entity
data class Game(
        val gameCode: String,
        @Id @GeneratedValue val id: Long? = null
)

interface PlayerRepository : CrudRepository<Player, Long> {
    fun findAllByGameGameCode(gameCode: String): List<Player>
}

@Entity
data class Player(
        val name: String,
        @ManyToOne @JoinColumn val game: Game,
        @Id @GeneratedValue val id: Long? = null
)

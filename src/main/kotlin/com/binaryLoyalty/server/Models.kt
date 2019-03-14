package com.binaryLoyalty.server

import org.springframework.data.repository.CrudRepository
import java.time.LocalDateTime
import java.time.LocalTime
import javax.persistence.*

interface GameRepository : CrudRepository<Game, Long> {
    fun findByGameCode(gameCode: String): Game
}

@Entity
data class Game(
        val gameCode: String,
        val state: GameState = GameState.WAITING,
        val lastModified: LocalDateTime = LocalDateTime.now(),
        @Id @GeneratedValue val id: Long? = null
)

interface PlayerRepository : CrudRepository<Player, Long> {
    fun findAllByGameGameCode(gameCode: String): List<Player>
}

@Entity
data class Player(
        val name: String,
        @ManyToOne @JoinColumn val game: Game,
        val isReady: Boolean = false,
        val loyalty: Loyalty = Loyalty.UNASSIGNED,
        @Id @GeneratedValue val id: Long? = null
)

enum class GameState {
    WAITING, GETTING_READY, STARTED;
}

enum class Loyalty {
    UNASSIGNED, BETRAYER, LOYALIST
}
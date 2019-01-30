package com.binaryLoyalty.server

import org.springframework.data.repository.CrudRepository
import java.time.LocalDate
import java.time.LocalTime
import javax.persistence.*

interface GameRepository : CrudRepository<Game, Long> {
    fun findByGameCode(gameCode: String): Game
}

@Entity
data class Game(
        val gameCode: String,
        val state: GameState = GameState.WAITING,
        val lastModified: LocalTime = LocalTime.now(),
        @Id @GeneratedValue val id: Long? = null
)

interface PlayerRepository : CrudRepository<Player, Long> {
    fun findAllByGameGameCode(gameCode: String): List<Player>
}

@Entity
data class Player(
        val name: String,
        @ManyToOne @JoinColumn val game: Game,
        val isReady : Boolean = false,
        @Id @GeneratedValue val id: Long? = null
)

enum class GameState {
    WAITING, GETTING_READY, STARTED;
}


package com.binaryLoyalty.server

import org.springframework.data.repository.CrudRepository
import javax.persistence.*

interface GameRepository : CrudRepository<Game, Long> {
    fun findAllByGameCode(gameCode: String)
}
@Entity
data class Game(
        @Id @GeneratedValue val id: Long? = null,
        val gameCode: String
)

interface PlayerRepository: CrudRepository<Player, Long>
@Entity
data class Player(
        @Id @GeneratedValue val id: Long? = null,
        @ManyToOne @JoinColumn val game: Game
)

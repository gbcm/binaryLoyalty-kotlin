package com.binaryLoyalty.server.IntegrationTests

import com.binaryLoyalty.server.GameRepository
import com.binaryLoyalty.server.PlayerRepository
import com.nhaarman.expect.expect
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.LinkedMultiValueMap

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class LobbyIntegrationTests : IntegrationTestsBase() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var gameRepo: GameRepository

    @Autowired
    lateinit var playerRepo: PlayerRepository

    @Test
    fun `Players can create and join games with DB access`() {
        //Act
        var entity = restTemplate.getForEntity<String>("/")

        //Assert
        expect(entity.statusCode).toBe(HttpStatus.OK)
        expect(entity.body).toContain("Start Game")

        //Act
        val formParams = LinkedMultiValueMap<String, String>()
        entity = restTemplate.postRedirectGetForEntity("/", formParams)

        //Assert
        expect(entity.body).toContain("Current Game:")
        val allGames = gameRepo.findAll()
        expect(allGames.count()).toBe(1)

        //Act
        formParams.add("gameCode", allGames.iterator().next().gameCode)
        entity = restTemplate.postRedirectGetForEntity("/join", formParams)

        //Assert
        val allPlayersIterator = playerRepo.findAll().iterator()
        expect(entity.body).toContain("Player ${allPlayersIterator.next().id}")
        expect(entity.body).toContain("Player ${allPlayersIterator.next().id}")
    }



}
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

    @Test
    fun `Players can create and join games with DB access`() {
        //Act
        var entity = restTemplate.getForEntity<String>("/")

        //Assert
        expect(entity.statusCode).toBe(HttpStatus.OK)
        expect(entity.body).toContain("Create Game")
        expect(entity.body!!.contains("Current Game")).toBe(false)

        //Act
        val formParams = LinkedMultiValueMap<String, String>()
        formParams.add("playerName", "Alice")
        entity = restTemplate.postRedirectGetForEntity("/", formParams)

        //Assert
        val allGames = gameRepo.findAll()
        expect(allGames.count()).toBe(1)
        val gameCode = allGames.iterator().next().gameCode
        expect(entity.body!!.contains("Create Game")).toBe(false)
        expect(entity.body).toContain("Current Game: $gameCode")

        //Act
        formParams.clear()
        formParams.add("gameCode", gameCode)
        formParams.add("playerName", "Bob")
        entity = restTemplate.postRedirectGetForEntity("/join", formParams)

        //Assert
        expect(entity.body).toContain("Alice")
        expect(entity.body).toContain("Bob")
        expect(entity.body).toContain("Current Player: Bob")
    }



}
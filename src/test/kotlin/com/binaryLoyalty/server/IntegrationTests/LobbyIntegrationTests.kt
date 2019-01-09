package com.binaryLoyalty.server.IntegrationTests

import com.binaryLoyalty.server.GameRepository
import com.nhaarman.expect.expect
import org.junit.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.http.HttpStatus
import org.springframework.util.LinkedMultiValueMap

class LobbyIntegrationTests : IntegrationTestsBase() {

    @Autowired
    lateinit var restTemplate: TestRestTemplate

    @Autowired
    lateinit var gameRepo: GameRepository

    @Test
    fun `Players can create and join games`() {
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
        expect(entity.body).toContain("Player Alice")
        expect(entity.body).toContain("Player Bob")
        expect(entity.body).toContain("Current Player: Bob")
    }
}
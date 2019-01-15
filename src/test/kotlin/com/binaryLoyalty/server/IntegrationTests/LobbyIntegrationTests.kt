package com.binaryLoyalty.server.IntegrationTests

import com.binaryLoyalty.server.GameRepository
import com.nhaarman.expect.expect
import org.junit.Test
import org.openqa.selenium.By
import org.springframework.beans.factory.annotation.Autowired

class LobbyIntegrationTests : IntegrationTestsBase() {
    @Autowired
    lateinit var gameRepo: GameRepository

    @Test
    fun `Players can create and join games`() {
        //Act
        driver.get(baseUrl())

        //Assert
        val buttons = driver.findElements(By.cssSelector("button"))
        expect(buttons.size).toBe(2)
        expect(buttons[0].isDisplayed).toBe(true)
        expect(buttons[0].text).toBe("Create Game")
        expect(buttons[1].isDisplayed).toBe(true)
        expect(buttons[1].text).toBe("Join Game")

        //Act
        val createInput = driver.findElement(By.cssSelector("#createFormPlayerName"))
        createInput.sendKeys("Alice")
        buttons[0].click()

        //Assert
        val allGames = gameRepo.findAll()
        expect(allGames.count()).toBe(1)
        val gameCode = allGames.iterator().next().gameCode
        val curGameHeader = driver.findElement(By.cssSelector("#currentGameHeader"))
        expect(curGameHeader.text).toBe("Current Game: $gameCode")
        val curPlayerHeader = driver.findElement(By.cssSelector("#currentPlayerHeader"))
        expect(curPlayerHeader.text).toBe("Current Player: Alice")

        //Act
        driver.get(baseUrl())
        val joinNameInput = driver.findElement(By.cssSelector("#joinFormPlayerName"))
        joinNameInput.sendKeys("Bob")
        val joinCodeInput = driver.findElement(By.cssSelector("#joinFormGameCode"))
        joinCodeInput.sendKeys(gameCode)
        driver.findElements(By.cssSelector("button"))[1].click()

        //Assert
        val curPlayerHeaderBob = driver.findElement(By.cssSelector("#currentPlayerHeader"))
        expect(curPlayerHeaderBob.text).toBe("Current Player: Bob")
        val playerItems = driver.findElements(By.cssSelector("#playerItem"))
        expect(playerItems.size).toBe(2)
        expect(playerItems[0].isDisplayed).toBe(true)
        expect(playerItems[0].text).toBe("Player Alice")
        expect(playerItems[1].isDisplayed).toBe(true)
        expect(playerItems[1].text).toBe("Player Bob")
    }
}
package com.binaryLoyalty.server.IntegrationTests

import com.binaryLoyalty.server.GameRepository
import com.binaryLoyalty.server.PlayerRepository
import com.nhaarman.expect.expect
import org.junit.Test
import org.openqa.selenium.By
import org.springframework.beans.factory.annotation.Autowired

class GameIntegrationTests : IntegrationTestsBase() {
    @Autowired
    lateinit var gameRepo: GameRepository

    @Autowired
    lateinit var playerRepo: PlayerRepository

    @Test
    fun `If one player starts game, others see readiness prompt`() {
        //Act Player 1
        driver.get(baseUrl())
        val createInput = driver.findElement(By.cssSelector("#createFormPlayerName"))
        val createSubmit = driver.findElement(By.cssSelector("#createFormSubmit"))
        createInput.sendKeys("Alice")
        createSubmit.click()
        val player1Id = playerRepo.findAll().iterator().next().id

        //Act Player 2
        val allGames = gameRepo.findAll()
        val gameCode = allGames.iterator().next().gameCode
        driver.get(baseUrl())
        val joinNameInput = driver.findElement(By.cssSelector("#joinFormPlayerName"))
        val joinCodeInput = driver.findElement(By.cssSelector("#joinFormGameCode"))
        val joinSubmit = driver.findElement(By.cssSelector("#joinFormSubmit"))
        joinNameInput.sendKeys("Bob")
        joinCodeInput.sendKeys(gameCode)
        joinSubmit.click()
        val startSubmit = driver.findElement(By.cssSelector("#startSubmit"))
        startSubmit.click()

        //Assert
        val waiting = driver.findElement(By.cssSelector("#waiting"))
        expect(waiting.text).toBe("Waiting for other players")
        var timer = driver.findElement(By.cssSelector("#timer"))
        val timerValue = timer.text.toInt()
        expect(timerValue).toBeSmallerThan(16)
        expect(timerValue).toBeGreaterThan(0)

        //I don't love this, but the selenium wait commands are for load times
        Thread.sleep(1000)

        driver.get("${baseUrl()}/game?pid=$player1Id")
        val promptYes = driver.findElement(By.cssSelector("#promptYes"))
        val promptNo = driver.findElement(By.cssSelector("#promptNo"))
        expect(promptYes.isDisplayed).toBe(true)
        expect(promptNo.isDisplayed).toBe(true)
        timer = driver.findElement(By.cssSelector("#timer"))
        expect(timer.text.toInt()).toBeSmallerThan(timerValue)
        expect(timer.text.toInt()).toBeGreaterThan(0)
    }
}
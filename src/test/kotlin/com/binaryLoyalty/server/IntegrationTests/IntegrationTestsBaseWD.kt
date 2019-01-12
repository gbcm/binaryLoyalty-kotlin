package com.binaryLoyalty.server.IntegrationTests

import com.binaryLoyalty.server.GameRepository
import com.nhaarman.expect.expect
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.openqa.selenium.firefox.FirefoxDriver
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.CrudRepository
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.MultiValueMap

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTestsBaseWD {

    val driver : WebDriver = ChromeDriver()

    @LocalServerPort
    var port: Int? = null

    fun baseUrl() : String = "http://localhost:$port"

    @After
    fun tearDown() {
        driver.quit()
    }

    @Test
    fun contextLoads() {
    }
}
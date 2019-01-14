package com.binaryLoyalty.server.IntegrationTests

import org.junit.After
import org.junit.Test
import org.junit.runner.RunWith
import org.openqa.selenium.WebDriver
import org.openqa.selenium.chrome.ChromeDriver
import org.reflections.Reflections
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.web.server.LocalServerPort
import org.springframework.context.ApplicationContext
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.junit4.SpringRunner
import java.lang.reflect.InvocationTargetException

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTestsBaseWD {

    @Autowired
    private lateinit var appContext: ApplicationContext

    val driver: WebDriver = ChromeDriver()

    @LocalServerPort
    var port: Int? = null

    fun baseUrl(): String = "http://localhost:$port"

    @After
    fun tearDown() {
        try {
            val reflections = Reflections("com.binaryLoyalty.server")
            val repos = reflections.getSubTypesOf(CrudRepository::class.java)
            val secondRoundRepos = mutableSetOf<Any>()
            for (repo in repos) {
                try {
                    repo.getMethod("deleteAll").invoke(appContext.getBean(repo.simpleName.decapitalize()))
                } catch (e: InvocationTargetException) {
                    secondRoundRepos.add(repo)
                }
            }
            for (repo in secondRoundRepos) {
                if (repo is Class<*>) {
                    repo.getMethod("deleteAll").invoke(appContext.getBean(repo.simpleName.decapitalize()))
                }
            }
        } finally {
            driver.quit()
        }
    }

    @Test
    fun contextLoads() {
    }
}
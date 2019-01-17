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
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.repository.CrudRepository
import org.springframework.test.context.junit4.SpringRunner
import java.lang.reflect.InvocationTargetException


@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTestsBase {

    @Autowired
    private lateinit var appContext: ApplicationContext

    val driver: WebDriver = ChromeDriver()

    @LocalServerPort
    var port: Int? = null

    fun baseUrl(): String = "http://localhost:$port"

    @After
    fun tearDown() {
        try {
            cleanOutDatabase()
        } finally {
            driver.quit()
        }
    }

    fun cleanOutDatabase() {
        var repositories = getAllCrudRepositoryClasses()
        while (repositories.size > 0) {
            val oldSize = repositories.size
            repositories = attemptDeleteAll(repositories)
            if (repositories.size == oldSize) {
                throw Exception("No repositories were successfully emptied. " +
                        "Thus you will need to manually clean your DB in your test.")
            }
        }
    }

    private fun getAllCrudRepositoryClasses(): MutableList<Class<*>> {
        val reflections = Reflections("com.binaryLoyalty.server")
        val ret = mutableListOf<Class<*>>()
        ret.addAll(reflections.getSubTypesOf(CrudRepository::class.java))
        return ret
    }

    private fun attemptDeleteAll(deleteFrom: MutableList<Class<*>>): MutableList<Class<*>> {
        val reposWithFKProblems = mutableListOf<Class<*>>()
        for (repo in deleteFrom) {
            try {
                repo.getMethod("deleteAll").invoke(appContext.getBean(repo))
            } catch (e: InvocationTargetException) {
                if (e.cause is DataIntegrityViolationException) {
                    reposWithFKProblems.add(repo)
                }
            }
        }
        return reposWithFKProblems
    }

    @Test
    fun contextLoads() {
    }
}
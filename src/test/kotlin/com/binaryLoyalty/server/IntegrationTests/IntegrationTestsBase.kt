package com.binaryLoyalty.server.IntegrationTests

import com.nhaarman.expect.expect
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.boot.test.web.client.getForEntity
import org.springframework.boot.test.web.client.postForEntity
import org.springframework.http.*
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.util.MultiValueMap

@RunWith(SpringRunner::class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class IntegrationTestsBase {
    @Test
    fun contextLoads() {
    }

    fun TestRestTemplate.postRedirectGetForEntity(url: String, formMap: MultiValueMap<String, String>): ResponseEntity<String> {
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_FORM_URLENCODED
        val request = HttpEntity(formMap, headers)

        val entity = this.postForEntity<String>(url, request)
        expect(entity.statusCode).toBe(HttpStatus.FOUND)
        return this.getForEntity(entity.headers.location!!)
    }
}
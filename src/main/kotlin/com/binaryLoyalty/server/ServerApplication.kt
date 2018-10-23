package com.binaryLoyalty.server

import com.samskivert.mustache.Mustache
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean

@SpringBootApplication
class ServerApplication {
    @Bean
    fun mustacheCompiler(loader: Mustache.TemplateLoader?): Mustache.Compiler =
            Mustache.compiler().defaultValue("").withLoader(loader)
}

fun main(args: Array<String>) {
    runApplication<ServerApplication>(*args)
}
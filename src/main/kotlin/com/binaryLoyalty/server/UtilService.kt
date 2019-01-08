package com.binaryLoyalty.server

import org.springframework.stereotype.Service
import java.util.*
import kotlin.streams.asSequence

@Service
class UtilService() {
    fun generateGameCode(): String {
        val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789"
        return Random().ints(5, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")
    }
}
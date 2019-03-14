package com.binaryLoyalty.server

import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.time.LocalTime

@Service
class TimeService {
    fun getCurrentTime(): LocalDateTime {
        return LocalDateTime.now()
    }

}

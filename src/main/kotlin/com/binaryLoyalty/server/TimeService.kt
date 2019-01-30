package com.binaryLoyalty.server

import org.springframework.stereotype.Service
import java.time.LocalTime

@Service
class TimeService {
    fun getCurrentTime(): LocalTime {
        return LocalTime.now()
    }

}

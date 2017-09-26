package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.util.TessUtils
import java.util.*

/**
 * Created by AFlyingGrayson on 9/26/17
 */
object TimeHandler {

    fun keepTime() {
        var lastHour = 0

        while (true) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            if (hour != lastHour) {
                if (hour % 12 == 0) {
                    TessUtils.server.channels.first { it.name == "pa-system" }?.sendMessage("A new day has begun. ${
                    when (Tess.rand.nextInt(100)) {
                        in 50..90 -> "It's very sunny."
                        in 80..100 -> "It's raining."
                        else -> "The skies are clear."
                    }}")
                }
                lastHour = hour
            }
        }
    }
}
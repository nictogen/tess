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
        var lastMinute = 0

        while (true) {
            val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
            val minute = Calendar.getInstance().get(Calendar.MINUTE)

            if (minute != lastMinute) {
                lastMinute = minute
                if (minute % 5 == 0) {
                    PlayerHandler.players.forEach {
                        it.mana++
                        if (it.mana > it.stats.first { it.type == PlayerHandler.StatType.MANA }.value) it.mana = it.stats.first { it.type == PlayerHandler.StatType.MANA }.value
                        if (!it.masteries.any { it.type == PlayerHandler.MagicType.MANA_EFFICIENCY }) {
                            it.masteries.clear()
                            it.masteries.addAll(
                                    arrayListOf(PlayerHandler.MasteryLevel(PlayerHandler.MagicType.CONSTRUCTION, PlayerHandler.MagicRank.F),
                                            PlayerHandler.MasteryLevel(PlayerHandler.MagicType.PROJECTION, PlayerHandler.MagicRank.F),
                                            PlayerHandler.MasteryLevel(PlayerHandler.MagicType.CONTROL, PlayerHandler.MagicRank.F),
                                            PlayerHandler.MasteryLevel(PlayerHandler.MagicType.ALCHEMY, PlayerHandler.MagicRank.F),
                                            PlayerHandler.MasteryLevel(PlayerHandler.MagicType.HEALING, PlayerHandler.MagicRank.F),
                                            PlayerHandler.MasteryLevel(PlayerHandler.MagicType.RUNE_MAGIC, PlayerHandler.MagicRank.F),
                                            PlayerHandler.MasteryLevel(PlayerHandler.MagicType.SUMMONING, PlayerHandler.MagicRank.F),
                                            PlayerHandler.MasteryLevel(PlayerHandler.MagicType.TRANSFORMATION, PlayerHandler.MagicRank.F),
                                            PlayerHandler.MasteryLevel(PlayerHandler.MagicType.MANA_EFFICIENCY, PlayerHandler.MagicRank.F)
                                    ))
                        }
                        if (!TessUtils.isAdmin(TessUtils.getMember(it)))
                            it.skills.forEach { skill ->
                                try {
                                    val type = PlayerHandler.MagicType.valueOf(skill.name.toUpperCase())
                                    val mastery = it.masteries.first { it.type == type }
                                    if (skill.value > mastery.rank.max) {
                                        val diff = skill.value - (mastery.rank.max - 1)
                                        skill.value = mastery.rank.max - 1
                                        it.xp += diff * 5
                                    }
                                } catch (e: Exception) { }
                            }
                    }
                }
            }

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
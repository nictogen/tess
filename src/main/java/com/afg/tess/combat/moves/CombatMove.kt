package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class CombatMove(private val target: Int) : AbstractUtilityMove(MainStat.NONE, Source.PHYSICAL, "move") {

    override fun getStorageName(): String {
        return "combatMove"
    }

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        val direction = target - user.area
        val speed = if (direction >= 0) 1 else -1
        user.area += speed
        if (user.area > 5) user.area = 5
        if (user.area < 0) user.area = 0

        combat.addLineToInfo("${user.name} moved, now residing in area ${user.area}.")
    }
}
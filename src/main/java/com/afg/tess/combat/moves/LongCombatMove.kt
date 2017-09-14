package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class LongCombatMove(source: Move.Source, name: String) : AbstractUtilityMove(MainStat.NONE, source, name) {

    var target = 0

    override fun getStorageName(): String {
        return "longCombatMove"
    }

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        val direction = target - user.area
        val speed = if (direction >= 0) 3 else -3
        if(Math.abs(direction) < 3)
            user.area = target
        else
            user.area += speed
        if (user.area > 5) user.area = 5
        if (user.area < 0) user.area = 0

        combat.addLineToInfo("${user.name} moved, now residing in area ${user.area}.")
    }

}
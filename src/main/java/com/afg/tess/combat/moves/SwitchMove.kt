package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class SwitchMove(source : Move.Source, name: String) : AbstractUtilityMove(MainStat.NONE, source, name) {

    override fun getStorageName(): String {
        return "switch"
    }

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        val formerArea = user.area
        user.area = targets[0].area
        targets[0].area = formerArea
        combat.addLineToInfo("${user.name} switched places with ${targets[0]}.")
    }
}
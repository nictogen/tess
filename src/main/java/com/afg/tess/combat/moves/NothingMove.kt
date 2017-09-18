package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/14/17
 */
class NothingMove : AbstractUtilityMove(MainStat.NONE, Source.PHYSICAL, "Nothing") {

    override fun getStorageName(): String {
        return "nothing"
    }

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        combat.addLineToInfo("${user.name} spaced out and did nothing.")
    }

}
package com.afg.tess.combat.moves

import com.afg.tess.TessUtils
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class AccurateDamageMove(mainStat: MainStat, type: Type, source: Source, name: String) : Move(mainStat, type, source, name) {

    override fun getBasePower(): Double {
        return 5.0
    }

    override fun getStorageName(): String {
        return "accurateDamage"
    }

    override fun decideIfHit(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat): Boolean {
        return if (target.dead) false
        else {
            combat.addLineToInfo("${user.name} hit ${target.name} (${TessUtils.numberToLetter(combat.participants.indexOf(target))}) with $name.")
            true
        }
    }
}
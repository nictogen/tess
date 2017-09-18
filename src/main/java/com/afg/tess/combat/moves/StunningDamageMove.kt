package com.afg.tess.combat.moves

import com.afg.tess.Tess
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class StunningDamageMove(mainStat: MainStat, type: Type, source: Source, name: String) : Move(mainStat, type, source, name) {
    override fun getBasePower(): Double {
        return 5.0
    }

    override fun getStorageName(): String {
        return "stunningDamage"
    }

    override fun calculateDamage(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        super.calculateDamage(user, target, combat)

        if (Tess.rand.nextInt(10) <= 3)
            if (!target.dead && target.nextMove != null) {
                combat.addLineToInfo("${user.name} stunned ${target.name}, cancelling their move.")
                target.nextMove = null
            }
    }
}
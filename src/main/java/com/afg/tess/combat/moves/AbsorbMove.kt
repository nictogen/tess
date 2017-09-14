package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */

class AbsorbMove(mainStat: MainStat, type: Type, source: Source, name: String) : Move(mainStat, type, source, name) {

    override fun getBasePower(): Double {
        return 5.0
    }

    override fun getStorageName(): String {
        return "absorb"
    }

    override fun doDamage(amount: Double, user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        super.doDamage(amount, user, target, combat)
        user.health += amount/2.0
        if (user.health > user.ogHealth)
            user.health = user.ogHealth
        combat.addLineToInfo("${user.name} absorbed <${amount/2.0}> hp from ${target.name}, ${user.name} now has <${user.health}> hp.")
    }
}
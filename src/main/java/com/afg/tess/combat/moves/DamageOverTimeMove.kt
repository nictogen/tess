package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class DamageOverTimeMove(mainStat: MainStat, type: Type, source: Source, name: String) : Move(mainStat, type, source, name), IOngoingMove {

    override fun ongoingEffect(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        combat.addLineToInfo("${user.name}'s $name did ongoing damage to ${target.name}")
        this.calculateDamage(user, target, combat)
    }

    override fun roundsAffecting(): Int { return 5 }

    override fun getOngoingName(): String {
        return name
    }

    override fun getBasePower(): Double {
        return 3.0
    }

    override fun getStorageName(): String {
        return "damageOverTime"
    }

}
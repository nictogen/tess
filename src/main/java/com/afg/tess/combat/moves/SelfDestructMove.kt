package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class SelfDestructMove(mainStat: MainStat, type: Type, source: Source, name: String) : Move(mainStat, type, source, name) {
    override fun getBasePower(): Double { return 50.0 }

    override fun getStorageName(): String {
        return "selfDestruct"
    }

    override fun performMove(user: CombatHandler.CombatParticipant, combat: Combat) {
        targets.forEach {
            if (Math.abs(it.area - user.area) <= 3) {
                if (decideIfHit(user, it, combat))
                    calculateDamage(user, it, combat)
            } else combat.addLineToInfo("${it.name} was out of range of ${user.name}'s ${this.name}")
        }
    }

    override fun calculateDamage(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        if(!user.dead) {
            combat.addLineToInfo("${user.name} blew themselves up.")
            user.health = 0.0
            user.dead = true
        }
        super.calculateDamage(user, target, combat)
    }
}
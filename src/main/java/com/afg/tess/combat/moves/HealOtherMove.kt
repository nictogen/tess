package com.afg.tess.combat.moves

import com.afg.tess.Tess
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class HealOtherMove(mainStat: MainStat, source: Source, name: String) : AbstractUtilityMove(mainStat, source, name) {

    override fun getStorageName(): String {
        return "heal"
    }

    override fun calculateDamage(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {}

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        targets.forEach {
            val healAmount = getMainStatValue(user).toDouble()/4.0 + Tess.rand.nextInt(getMainStatValue(user)).toDouble()/2.0
            it.health += healAmount
            if (it.health > it.ogHealth)
                it.health = it.ogHealth
            combat.addLineToInfo("${user.name} healed ${it.name} with ${this.name}, ${it.name} now has <${it.health}> hp.")
        }
    }
}
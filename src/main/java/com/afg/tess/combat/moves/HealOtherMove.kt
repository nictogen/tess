package com.afg.tess.combat.moves

import com.afg.tess.Tess
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class HealOtherMove(mainStat: MainStat, source: Source, name: String) : AbstractUtilityMove(mainStat, source, name) {

    override fun getStorageName(): String {
        return "healOther"
    }

    override fun calculateDamage(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {}

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        targets.forEach {
            val healAmount = Tess.rand.nextInt(getMainStatValue(user)) + 1
            it.health += healAmount.toDouble()
            if (it.health > it.ogHealth)
                it.health = it.ogHealth
            combat.addLineToInfo("${user.name} healed ${it.name} with ${this.name}, ${it.name} now has <${it.health}> hp.")
        }
    }
}
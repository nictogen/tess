package com.afg.tess.combat.moves

import com.afg.tess.Tess
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class HealOverTimeMove(mainStat: MainStat, type: Type, source: Source, name: String) : Move(mainStat, type, source, name), IOngoingMove {

    override fun performMove(user: CombatHandler.CombatParticipant, combat: Combat) {
        when (type) {
            Type.MELEE -> {
                targets.forEach {
                    if(!it.ongoingEffects.any { it.move == this }) {
                        combat.addLineToInfo("${user.name} applied $name to ${it.name}.")
                        it.ongoingEffects.add(CombatHandler.OngoingEffect(user, this.roundsAffecting(), this))
                    }}
            }
            Type.RANGE -> {
                targets.forEach {
                    if (Math.abs(it.area - user.area) >= 2) {
                        if (decideIfHit(user, it, combat))
                            calculateDamage(user, it, combat)
                    } else combat.addLineToInfo("${user.name} tried to use $name on ${it.name}, but they were too close.")
                }
            }
            else -> { }
        }
    }

    override fun ongoingEffect(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        combat.addLineToInfo("${user.name}'s $name did ongoing healing to ${target.name}")
        val healAmount = Tess.rand.nextInt(getMainStatValue(user)) + 1
        target.health += healAmount.toDouble()
        if (target.health > target.ogHealth)
            target.health = target.ogHealth
        combat.addLineToInfo("${user.name} healed ${target.name} with ${this.name}, ${target.name} now has <${target.health}> hp.")
    }

    override fun roundsAffecting(): Int { return 5 }

    override fun getOngoingName(): String {
        return name
    }

    override fun getBasePower(): Double {
        return 0.0
    }

    override fun getStorageName(): String {
        return "healOverTime"
    }

}
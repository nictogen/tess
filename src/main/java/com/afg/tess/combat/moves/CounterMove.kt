package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class CounterMove(mainStat: MainStat, type: Type, source: Source, name: String) : Move(mainStat, type, source, name) {
    override fun getBasePower(): Double { return 0.0 }

    override fun getStorageName(): String {
        return "counter"
    }

    override fun calculateDamage(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        if(this.type != Type.UTILITY) {
            if (target.nextMove != null) {
                if (target.nextMove!!.mainStat == this.mainStat && target.nextMove!!.type == this.type && target.nextMove !is CounterMove) {
                    combat.addLineToInfo("${user.name} countered ${target.name}, cancelling their move and sending it back at them.")
                    target.nextMove!!.calculateDamage(target, target, combat)
                    target.nextMove = null
                } else combat.addLineToInfo("${user.name} tried to counter, but ${target.name} was using a move they weren't prepared for.")
            } else {
                combat.addLineToInfo("${user.name} was too slow to counter ${target.name}")
            }
        }
    }
}
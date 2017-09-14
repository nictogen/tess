package com.afg.tess.combat.moves

import com.afg.tess.Tess
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.npcs.Ero
import com.afg.tess.combat.npcs.Guard

/**
 * Created by AFlyingGrayson on 9/12/17
 */
class FleeMove : AbstractUtilityMove(MainStat.NONE, Source.PHYSICAL, "Flee") {

    override fun getStorageName(): String {
        return "flee"
    }

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        val monsters = combat.participants.filter { it is Ero || (it is Guard && it.faction != user.faction) }.filter { !it.dead }
        if (monsters.isNotEmpty()) {
            val bestMonster = monsters.sortedByDescending { it.speed }[0]
            val chance = 50 * (user.speed / bestMonster.speed)
            if (Tess.rand.nextInt(100) < chance) {
                combat.addLineToInfo("${user.name} ran away!")
                combat.fleeingParticipants.add(user)
                user.dead = true
            } else {
                combat.addLineToInfo("${user.name} tried to run away, but ${bestMonster.name} blocked their escape!")
            }
        } else {
            combat.addLineToInfo("${user.name} ran away!")
            combat.fleeingParticipants.add(user)
            user.dead = true
        }
    }
}
package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
interface IOngoingMove {

    fun ongoingEffect(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat)

    fun roundsAffecting() : Int

    fun getOngoingName() : String
}
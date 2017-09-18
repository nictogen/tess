package com.afg.tess.combat.moves

import com.afg.tess.TessUtils
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/17/17
 */
class PowerStealMove(type : Move.Type, source: Source, val mainStatToSteal : MainStat, val sourceToSteal : Source, name : String) : Move(MainStat.NONE, type, source, name), IOngoingMove {
    override fun getBasePower(): Double { return 0.0 }

    val movesStolen = ArrayList<Move>()

    override fun ongoingEffect(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        val effect = user.ongoingEffects.filter { it.move == this }[0]
        if(effect.roundsLeft == 11){
            movesStolen.clear()
            user.power = user.ogPower
        }
    }

    override fun calculateDamage(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        if(user.ongoingEffects.any { it.move == this }) {
            if (target.power + 5 > user.power)
                user.power = target.power + 5
            movesStolen += user.getMoves()
            if (user is CombatHandler.Player) {
                var string = ""
                string += "\n\nStolen Moves: \n"
                movesStolen.forEach {
                    val s = it.saveData().split("/")
                    var extraData = ""
                    if (s.size > 5) {
                        val s2 = s.subList(5, s.size)
                        s2.forEach { extraData += "/$it" }
                    }
                    string += "\n<${it.name}> Move Type: ${it.getStorageName()}, Main Stat: ${it.mainStat.name.toLowerCase().capitalize()}, TargetType: ${it.type.name.toLowerCase().capitalize()}, Source: ${it.source.name.toLowerCase().capitalize()} $extraData"
                }
                TessUtils.getRpMember(user.player.playerID)?.sendMessage("```md\n$string```")
            }
            combat.addLineToInfo("${target.name} stole ${target.name}'s ${mainStatToSteal.name.toLowerCase()} and their ${sourceToSteal.name.toLowerCase()} moves.\n" +
                    "Their power is now ${user.power} and they stole ${movesStolen.size} moves.")
        } else combat.addLineToInfo("but the move needs more time to recharge.")
    }

    override fun roundsAffecting(): Int {
        return 15
    }

    override fun getOngoingName(): String {
        return "powerCooldown"
    }

    override fun getStorageName(): String {
        return "powerSteal"
    }

    override fun saveData(): String {
        return "${getStorageName()}/${type.name}/$source/$mainStatToSteal/$sourceToSteal/$name"
    }




}
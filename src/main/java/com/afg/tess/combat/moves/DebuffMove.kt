package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.combats.PvpCombat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class DebuffMove(type : Move.Type, source : Move.Source, name: String, statToDebuff: String) : Move(MainStat.NONE, type, source, name) {
    override fun getBasePower(): Double {
        return 0.0
    }

    var mainStatToDebuff: MainStat? = null
    var secondaryStatToDebuff: SecondaryStat? = null
    init {
        try {
            mainStatToDebuff = MainStat.valueOf(statToDebuff.toUpperCase())
        } catch (e : IllegalArgumentException){}
        if(mainStatToDebuff == null)
            try {
                this.secondaryStatToDebuff = SecondaryStat.valueOf(statToDebuff.toUpperCase())
            } catch (e : IllegalArgumentException){}
    }

    override fun calculateDamage(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {

        if(combat !is PvpCombat) {
            if(user.faction == target.faction){
                combat.addLineToInfo("But the target is in the same faction, so no debuff was applied.")
                return
            }
        }

        when(this.mainStatToDebuff){
            MainStat.STRENGTH -> {
                if (target.strength > target.ogStrength - 20){
                    target.strength -= 5
                    if(target.strength < target.ogStrength - 20)
                        target.strength = target.ogStrength - 20
                } else {
                    combat.addLineToInfo("${target.name} tried to power down ${target.name}'s ${mainStatToDebuff!!.name.toLowerCase()} with ${this.name}, but it was too low. (${target.strength})")
                    
                }
                combat.addLineToInfo("${target.name} powered down ${target.name}'s ${mainStatToDebuff!!.name.toLowerCase()} with ${this.name}. It is now ${target.strength}")

            }
            MainStat.INTELLIGENCE -> {
                if(target.intelligence > target.ogIntelligence - 20) {
                    target.intelligence -= 5
                    if (target.intelligence < target.ogIntelligence- 20)
                        target.intelligence = target.ogIntelligence- 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power down ${target.name}'s ${mainStatToDebuff!!.name.toLowerCase()} with ${this.name}, but it was too low. (${target.intelligence})")
                    
                }
                combat.addLineToInfo("${user.name} powered down ${target.name}'s ${mainStatToDebuff!!.name.toLowerCase()} with ${this.name}. It is now ${target.intelligence}")

            }
            MainStat.POWER -> {
                if(target.power > target.ogPower- 20) {
                    target.power -= 5
                    if(target.power < target.ogPower - 20)
                        target.power = target.ogPower - 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power down ${target.name}'s ${mainStatToDebuff!!.name.toLowerCase()} with ${this.name}, but it was too low. (${target.power})")
                    
                }
                combat.addLineToInfo("${user.name} powered down ${target.name}'s ${mainStatToDebuff!!.name.toLowerCase()} with ${this.name}. It is now ${target.power}")
            }
            else -> {}
        }

        when(this.secondaryStatToDebuff){
            SecondaryStat.ACCURACY -> {
                if (target.accuracy > target.ogAccuracy - 20) {
                    target.accuracy -= 5
                    if(target.accuracy < target.ogAccuracy - 20)
                        target.accuracy = target.ogAccuracy - 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power down ${target.name}'s ${secondaryStatToDebuff!!.name.toLowerCase()} with ${this.name}, but it was too low. (${target.accuracy})")
                    
                }
                combat.addLineToInfo("${user.name} powered down ${target.name}'s ${secondaryStatToDebuff!!.name.toLowerCase()} with ${this.name}. It is now ${target.accuracy}")
            }
            SecondaryStat.DEFENSE -> {
                if (target.defense > target.ogDefense - 20) {
                    target.defense -= 5
                    if(target.defense < target.ogDefense - 20)
                        target.defense = target.ogDefense - 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power down ${target.name}'s ${secondaryStatToDebuff!!.name.toLowerCase()} with ${this.name}, but it was too low. (${target.defense})")
                    
                }
                combat.addLineToInfo("${user.name} powered down ${target.name}'s ${secondaryStatToDebuff!!.name.toLowerCase()} with ${this.name}. It is now ${target.defense}")
            }
            SecondaryStat.SPEED -> {
                if (target.speed > target.ogSpeed - 20) {
                    target.speed -= 5
                    if(target.speed < target.ogSpeed - 20)
                        target.speed = target.ogSpeed - 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power down ${target.name}'s ${secondaryStatToDebuff!!.name.toLowerCase()} with ${this.name}, but it was too low. (${target.speed})")
                    
                }
                combat.addLineToInfo("${user.name} powered down ${target.name}'s ${secondaryStatToDebuff!!.name.toLowerCase()} with ${this.name}. It is now ${target.speed}")
            }
            else -> {}
        }
   
    }

    override fun saveData(): String {
        val string : String = if(mainStatToDebuff != null) mainStatToDebuff!!.name else if(secondaryStatToDebuff != null) secondaryStatToDebuff!!.name else ""
        return "${getStorageName()}/${mainStat.name}/${type.name}/$source/$name/$string"
    }

    override fun getStorageName(): String {
        return "debuff"
    }
}
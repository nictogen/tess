package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class SelfPowerUpMove(mainStat: MainStat, source : Move.Source, name: String, statToPowerUp: String) : AbstractUtilityMove(mainStat, source, name) {

    override fun getBasePower(): Double { return 5.0}

    var mainStatToPowerUp : MainStat? = null
    var secondaryStatToPowerUp : SecondaryStat? = null
    init {
        try {
            mainStatToPowerUp = MainStat.valueOf(statToPowerUp.toUpperCase())
        } catch (e : IllegalArgumentException){}
        if(mainStatToPowerUp == null)
        try {
            secondaryStatToPowerUp = SecondaryStat.valueOf(statToPowerUp.toUpperCase())
        } catch (e : IllegalArgumentException){}
    }

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        when(this.mainStatToPowerUp){
            MainStat.STRENGTH -> {
                if (user.strength < user.ogStrength+ 20){
                    user.strength += 5
                    if(user.strength > user.ogStrength + 20)
                        user.strength = user.ogStrength + 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.strength})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.strength}")

            }
            MainStat.INTELLIGENCE -> {
                if(user.intelligence < user.ogIntelligence+ 20) {
                    user.intelligence += 5
                    if (user.intelligence > user.ogIntelligence+ 20)
                        user.intelligence = user.ogIntelligence+ 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.intelligence})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.intelligence}")

            }
            MainStat.POWER -> {
                if(user.power < user.ogPower+ 20) {
                    user.power += 5
                    if(user.power > user.ogPower + 20)
                        user.power = user.ogPower + 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.power})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.power}")
            }
            else -> {}
        }

        when(this.secondaryStatToPowerUp){
            SecondaryStat.ACCURACY -> {
                if (user.accuracy < user.ogAccuracy + 20) {
                    user.accuracy += 5
                    if(user.accuracy > user.ogAccuracy + 20)
                        user.accuracy = user.ogAccuracy + 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.accuracy})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.accuracy}")
            }
            SecondaryStat.DEFENSE -> {
                if (user.defense < user.ogDefense + 20) {
                    user.defense += 5
                    if(user.defense > user.ogDefense + 20)
                        user.defense = user.ogDefense + 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.defense})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.defense}")
            }
            SecondaryStat.SPEED -> {
                if (user.speed < user.ogSpeed + 20) {
                    user.speed += 5
                    if(user.speed > user.ogSpeed + 20)
                        user.speed = user.ogSpeed + 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.speed})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.speed}")
            }
            else -> {}
        }
    }

    override fun saveData(): String {
        val string : String = if(mainStatToPowerUp != null) mainStatToPowerUp!!.name else if(secondaryStatToPowerUp != null) secondaryStatToPowerUp!!.name else ""
        return "${getStorageName()}/${mainStat.name}/${type.name}/$source/$name/$string"
    }

    override fun getStorageName(): String {
        return "selfPowerUp"
    }
}
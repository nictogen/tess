package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class SelfPowerUpMove(source : Move.Source, var mainStatToPowerUp : MainStat, var secondaryStatToPowerUp : SecondaryStat, name : String) : AbstractUtilityMove(MainStat.NONE, source, name) {

    override fun getBasePower(): Double { return 5.0}

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        when(this.mainStatToPowerUp){
            MainStat.STRENGTH -> {
                if (user.strength < user.ogStrength+ 10){
                    user.strength += 5
                    if(user.strength > user.ogStrength + 10)
                        user.strength = user.ogStrength + 10
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.strength})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.strength}")

            }
            MainStat.INTELLIGENCE -> {
                if(user.intelligence < user.ogIntelligence+ 10) {
                    user.intelligence += 5
                    if (user.intelligence > user.ogIntelligence+ 10)
                        user.intelligence = user.ogIntelligence+ 10
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.intelligence})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.intelligence}")

            }
            MainStat.POWER -> {
                if(user.power < user.ogPower+ 10) {
                    user.power += 5
                    if(user.power > user.ogPower + 10)
                        user.power = user.ogPower + 10
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.power})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.power}")
            }
            else -> { }
        }

        when(this.secondaryStatToPowerUp){
            SecondaryStat.ACCURACY -> {
                if (user.accuracy < user.ogAccuracy + 10) {
                    user.accuracy += 5
                    if(user.accuracy > user.ogAccuracy + 10)
                        user.accuracy = user.ogAccuracy + 10
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.accuracy})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.accuracy}")
            }
            SecondaryStat.DEFENSE -> {
                if (user.defense < user.ogDefense + 10) {
                    user.defense += 5
                    if(user.defense > user.ogDefense + 10)
                        user.defense = user.ogDefense + 10
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.defense})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.defense}")
            }
            SecondaryStat.SPEED -> {
                if (user.speed < user.ogSpeed + 10) {
                    user.speed += 5
                    if(user.speed > user.ogSpeed + 10)
                        user.speed = user.ogSpeed + 10
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.speed})")
                    return@effect
                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.speed}")
            }
            else -> {}
        }
    }

    override fun saveData(): String {
        return "${getStorageName()}/$source/$mainStatToPowerUp/$secondaryStatToPowerUp/$name"
    }

    override fun getStorageName(): String {
        return "selfPowerUp"
    }
}
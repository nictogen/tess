package com.afg.tess.combat.moves

import com.afg.tess.Tess
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class RandomExtremeSelfPowerUpMove(source : Move.Source, name: String) : AbstractUtilityMove(MainStat.NONE, source, name) {

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        val stat = Tess.rand.nextInt(6)

        val mainStatToPowerUp = when(stat){
            0 -> MainStat.STRENGTH
            1 -> MainStat.INTELLIGENCE
            2 -> MainStat.POWER
            else -> null
        }

        val secondaryStatToPowerUp = when(stat){
            3 -> SecondaryStat.ACCURACY
            4 -> SecondaryStat.DEFENSE
            5 -> SecondaryStat.SPEED
            else -> null
        }

        when(mainStatToPowerUp){
            MainStat.STRENGTH -> {
                if (user.strength < user.ogStrength + 30){
                    user.strength += 10
                    if(user.strength > user.ogStrength + 30)
                        user.strength = user.ogStrength + 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.strength})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.strength}")

            }
            MainStat.INTELLIGENCE -> {
                if(user.intelligence < user.ogIntelligence + 30) {
                    user.intelligence += 10
                    if (user.intelligence > user.ogIntelligence+ 30)
                        user.intelligence = user.ogIntelligence+ 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.intelligence})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.intelligence}")

            }
            MainStat.POWER -> {
                if(user.power < user.ogPower+ 30) {
                    user.power += 10
                    if(user.power > user.ogPower + 30)
                        user.power = user.ogPower + 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.power})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.power}")
            }
            else -> {}
        }

        when(secondaryStatToPowerUp){
            SecondaryStat.ACCURACY -> {
                if (user.accuracy < user.ogAccuracy + 30) {
                    user.accuracy += 10
                    if(user.accuracy > user.ogAccuracy + 30)
                        user.accuracy = user.ogAccuracy + 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.accuracy})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.accuracy}")
            }
            SecondaryStat.DEFENSE -> {
                if (user.defense < user.ogDefense+ 30) {
                    user.defense += 10
                    if(user.defense > user.ogDefense+ 30)
                        user.defense = user.ogDefense+ 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.defense})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.defense}")
            }
            SecondaryStat.SPEED -> {
                if (user.speed < user.ogSpeed+ 30) {
                    user.speed += 10
                    if(user.speed > user.ogSpeed+ 30)
                        user.speed = user.ogSpeed+ 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.speed})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.speed}")
            }
            else -> {}
        }

    }

    override fun saveData(): String {
        return "${getStorageName()}/$source/$name"
    }

    override fun getStorageName(): String {
        return "randomExtremeSelfPowerUp"
    }
}
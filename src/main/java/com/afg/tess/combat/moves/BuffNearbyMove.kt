package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/17/17
 */
class BuffNearbyMove(source : Move.Source, var mainStatToPowerUp : MainStat, var secondaryStatToPowerUp : SecondaryStat, name : String) : AbstractUtilityMove(MainStat.NONE, source, name) {

    override fun getBasePower(): Double { return 5.0}

    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        targets.forEach {
            when (this.mainStatToPowerUp) {
                MainStat.STRENGTH -> {
                    if (it.strength < it.ogStrength + 5) {
                        it.strength += 5
                        if (it.strength > it.ogStrength + 5)
                            it.strength = it.ogStrength + 5
                    } else {
                        combat.addLineToInfo("${user.name} tried to power up ${it.name}'s ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${it.strength})")
                        return@effect
                    }
                    combat.addLineToInfo("${user.name} powered up ${it.name}'s ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${it.strength}")

                }
                MainStat.INTELLIGENCE -> {
                    if (it.intelligence < it.ogIntelligence + 5) {
                        it.intelligence += 5
                        if (it.intelligence > it.ogIntelligence + 5)
                            it.intelligence = it.ogIntelligence + 5
                    } else {
                        combat.addLineToInfo("${user.name} tried to power up ${it.name}'s ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${it.intelligence})")
                        return@effect
                    }
                    combat.addLineToInfo("${user.name} powered up ${it.name}'s ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${it.intelligence}")

                }
                MainStat.POWER -> {
                    if (it.power < it.ogPower + 5) {
                        it.power += 5
                        if (it.power > it.ogPower + 5)
                            it.power = it.ogPower + 5
                    } else {
                        combat.addLineToInfo("${user.name} tried to power up ${it.name}'s ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${it.power})")
                        return@effect
                    }
                    combat.addLineToInfo("${user.name} powered up ${it.name}'s ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${it.power}")
                }
                else -> {
                }
            }

            when (this.secondaryStatToPowerUp) {
                SecondaryStat.ACCURACY -> {
                    if (it.accuracy < it.ogAccuracy + 5) {
                        it.accuracy += 5
                        if (it.accuracy > it.ogAccuracy + 5)
                            it.accuracy = it.ogAccuracy + 5
                    } else {
                        combat.addLineToInfo("${user.name} tried to power up ${it.name}'s ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${it.accuracy})")
                        return@effect
                    }
                    combat.addLineToInfo("${user.name} powered up ${it.name}'s ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${it.accuracy}")
                }
                SecondaryStat.DEFENSE -> {
                    if (it.defense < it.ogDefense + 5) {
                        it.defense += 5
                        if (it.defense > it.ogDefense + 5)
                            it.defense = it.ogDefense + 5
                    } else {
                        combat.addLineToInfo("${user.name} tried to power up ${it.name}'s ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${it.defense})")
                        return@effect
                    }
                    combat.addLineToInfo("${user.name} powered up ${it.name}'s ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${it.defense}")
                }
                SecondaryStat.SPEED -> {
                    if (it.speed < it.ogSpeed + 5) {
                        it.speed += 5
                        if (it.speed > it.ogSpeed + 5)
                            it.speed = it.ogSpeed + 5
                    } else {
                        combat.addLineToInfo("${user.name} tried to power up ${it.name}'s ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${it.speed})")
                        return@effect
                    }
                    combat.addLineToInfo("${user.name} powered up ${it.name}'s ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${it.speed}")
                }
                else -> {
                }
            }
        }
    }

    override fun saveData(): String {
        return "${getStorageName()}/$source/$mainStatToPowerUp/$secondaryStatToPowerUp/$name"
    }

    override fun getStorageName(): String {
        return "buffNearby"
    }
}
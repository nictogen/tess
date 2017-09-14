package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class ExtremeSelfPowerUpMove(source : Move.Source, name: String, statToPowerUp : String, var mainStatToPowerDown : MainStat, var secondaryStatToPowerDown: SecondaryStat) : AbstractUtilityMove(MainStat.NONE, source, name) {

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
                if (user.strength < user.ogStrength + 30){
                    user.strength += 10
                    if(user.strength > user.ogStrength + 30)
                        user.strength = user.ogStrength + 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.strength})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.strength}")

            }
            MainStat.INTELLIGENCE -> {
                if(user.intelligence < user.ogIntelligence + 30) {
                    user.intelligence += 10
                    if (user.intelligence > user.ogIntelligence+ 30)
                        user.intelligence = user.ogIntelligence+ 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.intelligence})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.intelligence}")

            }
            MainStat.POWER -> {
                if(user.power < user.ogPower+ 30) {
                    user.power += 10
                    if(user.power > user.ogPower + 30)
                        user.power = user.ogPower + 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.power})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.power}")
            }
            else -> {}
        }

        when(this.secondaryStatToPowerUp){
            SecondaryStat.ACCURACY -> {
                if (user.accuracy < user.ogAccuracy + 30) {
                    user.accuracy += 10
                    if(user.accuracy > user.ogAccuracy + 30)
                        user.accuracy = user.ogAccuracy + 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.accuracy})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.accuracy}")
            }
            SecondaryStat.DEFENSE -> {
                if (user.defense < user.ogDefense+ 30) {
                    user.defense += 10
                    if(user.defense > user.ogDefense+ 30)
                        user.defense = user.ogDefense+ 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.defense})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.defense}")
            }
            SecondaryStat.SPEED -> {
                if (user.speed < user.ogSpeed+ 30) {
                    user.speed += 10
                    if(user.speed > user.ogSpeed+ 30)
                        user.speed = user.ogSpeed+ 30
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.speed})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp!!.name.toLowerCase()} with ${this.name}. It is now ${user.speed}")
            }
            else -> {}
        }

        when(this.mainStatToPowerDown){
            MainStat.STRENGTH -> {
                if (user.strength > user.ogStrength - 15){
                    user.strength -= 5
                    if(user.strength < user.ogStrength - 15)
                        user.strength = user.ogStrength - 15
                    if(user.strength < 0)
                        user.strength = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${mainStatToPowerDown!!.name.toLowerCase()} with ${this.name}, but it was too low. (${user.strength})")

                }
                combat.addLineToInfo("${user.name} powered down their ${mainStatToPowerDown!!.name.toLowerCase()} with ${this.name}. It is now ${user.strength}")

            }
            MainStat.INTELLIGENCE -> {
                if(user.intelligence > user.ogIntelligence - 15) {
                    user.intelligence -= 5
                    if (user.intelligence < user.ogIntelligence- 15)
                        user.intelligence = user.ogIntelligence- 15
                    if(user.intelligence < 0)
                        user.intelligence = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${mainStatToPowerDown!!.name.toLowerCase()} with ${this.name}, but it was too low. (${user.intelligence})")

                }
                combat.addLineToInfo("${user.name} powered down their ${mainStatToPowerDown!!.name.toLowerCase()} with ${this.name}. It is now ${user.intelligence}")

            }
            MainStat.POWER -> {
                if(user.power < user.ogPower- 15) {
                    user.power -= 5
                    if(user.power > user.ogPower - 15)
                        user.power = user.ogPower - 15
                    if(user.power < 0)
                        user.power = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${mainStatToPowerDown!!.name.toLowerCase()} with ${this.name}, but it was too low. (${user.power})")
                    
                }
                combat.addLineToInfo("${user.name} powered down their ${mainStatToPowerDown!!.name.toLowerCase()} with ${this.name}. It is now ${user.power}")
            }
            else -> {}
        }

        when(this.secondaryStatToPowerDown){
            SecondaryStat.ACCURACY -> {
                if (user.accuracy > user.ogAccuracy - 15) {
                    user.accuracy -= 5
                    if(user.accuracy < user.ogAccuracy - 15)
                        user.accuracy = user.ogAccuracy - 15
                    if(user.accuracy < 0)
                        user.accuracy = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}, but it was too low. (${user.accuracy})")

                }
                combat.addLineToInfo("${user.name} powered down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}. It is now ${user.accuracy}")
            }
            SecondaryStat.DEFENSE -> {
                if (user.defense > user.ogDefense- 15) {
                    user.defense -= 5
                    if(user.defense < user.ogDefense- 15)
                        user.defense = user.ogDefense- 15
                    if(user.defense < 0)
                        user.defense = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}, but it was too low. (${user.defense})")

                }
                combat.addLineToInfo("${user.name} powered down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}. It is now ${user.defense}")
            }
            SecondaryStat.SPEED -> {
                if (user.speed > user.ogSpeed- 15) {
                    user.speed -= 5
                    if(user.speed < user.ogSpeed- 15)
                        user.speed = user.ogSpeed- 15
                    if(user.speed < 0)
                        user.speed = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}, but it was too low. (${user.speed})")

                }
                combat.addLineToInfo("${user.name} powered down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}. It is now ${user.speed}")
            }
        }
    }

    override fun saveData(): String {
        val string : String = if(mainStatToPowerUp != null) mainStatToPowerUp!!.name else if(secondaryStatToPowerUp != null) secondaryStatToPowerUp!!.name else ""
        return "${getStorageName()}/${mainStat.name}/${type.name}/$source/$name/$string/$mainStatToPowerUp/$mainStatToPowerDown"
    }

    override fun getStorageName(): String {
        return "extremeSelfPowerUp"
    }
}
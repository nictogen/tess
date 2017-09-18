package com.afg.tess.combat.moves

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class ExtremeSelfPowerUpMove(source : Move.Source, var mainStatToPowerUp: MainStat, var secondaryStatToPowerUp: SecondaryStat, var mainStatToPowerDown : MainStat, var secondaryStatToPowerDown: SecondaryStat, name : String) : AbstractUtilityMove(MainStat.NONE, source, name) {


    override fun effect(user: CombatHandler.CombatParticipant, combat: Combat) {
        when(this.mainStatToPowerUp){
            MainStat.STRENGTH -> {
                if (user.strength < user.ogStrength + 20){
                    user.strength += 10
                    if(user.strength > user.ogStrength + 20)
                        user.strength = user.ogStrength + 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.strength})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.strength}")

            }
            MainStat.INTELLIGENCE -> {
                if(user.intelligence < user.ogIntelligence + 20) {
                    user.intelligence += 10
                    if (user.intelligence > user.ogIntelligence+ 20)
                        user.intelligence = user.ogIntelligence+ 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.intelligence})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.intelligence}")

            }
            MainStat.POWER -> {
                if(user.power < user.ogPower+ 20) {
                    user.power += 10
                    if(user.power > user.ogPower + 20)
                        user.power = user.ogPower + 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.power})")

                }
                combat.addLineToInfo("${user.name} powered up their ${mainStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.power}")
            }
            else -> {}
        }

        when(this.secondaryStatToPowerUp){
            SecondaryStat.ACCURACY -> {
                if (user.accuracy < user.ogAccuracy + 20) {
                    user.accuracy += 10
                    if(user.accuracy > user.ogAccuracy + 20)
                        user.accuracy = user.ogAccuracy + 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.accuracy})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.accuracy}")
            }
            SecondaryStat.DEFENSE -> {
                if (user.defense < user.ogDefense+ 20) {
                    user.defense += 10
                    if(user.defense > user.ogDefense+ 20)
                        user.defense = user.ogDefense+ 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.defense})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.defense}")
            }
            SecondaryStat.SPEED -> {
                if (user.speed < user.ogSpeed+ 20) {
                    user.speed += 10
                    if(user.speed > user.ogSpeed+ 20)
                        user.speed = user.ogSpeed+ 20
                } else {
                    combat.addLineToInfo("${user.name} tried to power up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}, but it was maxed out. (${user.speed})")

                }
                combat.addLineToInfo("${user.name} powered up their ${secondaryStatToPowerUp.name.toLowerCase()} with ${this.name}. It is now ${user.speed}")
            }
            else -> {}
        }

        when(this.mainStatToPowerDown){
            MainStat.STRENGTH -> {
                if (user.strength > user.ogStrength - 20){
                    user.strength -= 10
                    if(user.strength < user.ogStrength - 20)
                        user.strength = user.ogStrength - 20
                    if(user.strength < 0)
                        user.strength = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${mainStatToPowerDown.name.toLowerCase()} with ${this.name}, but it was too low. (${user.strength})")

                }
                combat.addLineToInfo("${user.name} powered down their ${mainStatToPowerDown.name.toLowerCase()} with ${this.name}. It is now ${user.strength}")

            }
            MainStat.INTELLIGENCE -> {
                if(user.intelligence > user.ogIntelligence - 20) {
                    user.intelligence -= 10
                    if (user.intelligence < user.ogIntelligence- 20)
                        user.intelligence = user.ogIntelligence- 20
                    if(user.intelligence < 0)
                        user.intelligence = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${mainStatToPowerDown.name.toLowerCase()} with ${this.name}, but it was too low. (${user.intelligence})")

                }
                combat.addLineToInfo("${user.name} powered down their ${mainStatToPowerDown.name.toLowerCase()} with ${this.name}. It is now ${user.intelligence}")

            }
            MainStat.POWER -> {
                if(user.power < user.ogPower- 20) {
                    user.power -= 10
                    if(user.power > user.ogPower - 20)
                        user.power = user.ogPower - 20
                    if(user.power < 0)
                        user.power = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${mainStatToPowerDown.name.toLowerCase()} with ${this.name}, but it was too low. (${user.power})")
                    
                }
                combat.addLineToInfo("${user.name} powered down their ${mainStatToPowerDown.name.toLowerCase()} with ${this.name}. It is now ${user.power}")
            }
            else -> {}
        }

        when(this.secondaryStatToPowerDown){
            SecondaryStat.ACCURACY -> {
                if (user.accuracy > user.ogAccuracy - 20) {
                    user.accuracy -= 10
                    if(user.accuracy < user.ogAccuracy - 20)
                        user.accuracy = user.ogAccuracy - 20
                    if(user.accuracy < 0)
                        user.accuracy = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}, but it was too low. (${user.accuracy})")

                }
                combat.addLineToInfo("${user.name} powered down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}. It is now ${user.accuracy}")
            }
            SecondaryStat.DEFENSE -> {
                if (user.defense > user.ogDefense- 20) {
                    user.defense -= 10
                    if(user.defense < user.ogDefense- 20)
                        user.defense = user.ogDefense- 20
                    if(user.defense < 0)
                        user.defense = 0
                } else {
                    combat.addLineToInfo("${user.name} tried to power down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}, but it was too low. (${user.defense})")

                }
                combat.addLineToInfo("${user.name} powered down their ${secondaryStatToPowerDown.name.toLowerCase()} with ${this.name}. It is now ${user.defense}")
            }
            SecondaryStat.SPEED -> {
                if (user.speed > user.ogSpeed- 20) {
                    user.speed -= 10
                    if(user.speed < user.ogSpeed- 20)
                        user.speed = user.ogSpeed- 20
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
        return "${getStorageName()}/$source/$mainStatToPowerUp/$secondaryStatToPowerUp/$mainStatToPowerDown/$secondaryStatToPowerDown/$name"
    }

    override fun getStorageName(): String {
        return "extremeSelfPowerUp"
    }
}
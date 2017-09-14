package com.afg.tess.combat.moves

import com.afg.tess.Tess
import com.afg.tess.TessUtils
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.combats.PvpCombat

/**
 * Created by AFlyingGrayson on 9/8/17
 */
abstract class Move(var mainStat: MainStat, var type: Type, var source: Source, var name: String) {
    val targets = ArrayList<CombatHandler.CombatParticipant>()

    companion object {
        fun createMove(string: String): Move {
            val args = string.split("/")
            var move: Move = BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Source.PHYSICAL, "default")
            try {
                val mainStat = Move.MainStat.valueOf(args[1].toUpperCase())
                val type = Move.Type.valueOf(args[2].toUpperCase())
                val source = Source.valueOf(args[3].toUpperCase())
                val name = args[4]
                when (args[0]) {
                    "basicDamage" -> move = BasicDamageMove(mainStat, type, source, name)
                    "selfPowerUp" -> move = SelfPowerUpMove(mainStat, source, name, args[5])
                    "longCombatMove" -> move = LongCombatMove(source, name)
                    "counter" -> move = CounterMove(mainStat, type, source, name)
                    "selfDestruct" -> move = SelfDestructMove(mainStat, Move.Type.RANGE, source, name)
                    "healOther" -> move = HealOtherMove(mainStat, source, name)
                    "switch" -> move = SwitchMove(source, name)
                    "stunningDamage" -> move = StunningDamageMove(mainStat, type, source, name)
                    "absorb" -> move = AbsorbMove(mainStat, type, source, name)
                    "accurateDamage" -> move = AccurateDamageMove(mainStat, type, source, name)
                    "damageOverTime" -> move = DamageOverTimeMove(mainStat, type, source, name)
                    "randomExtremeSelfPowerUp" -> move = RandomExtremeSelfPowerUpMove(source, name)
                    "healOverTime" -> move = HealOverTimeMove(mainStat, type, source, name)
                    "extremeSelfPowerUp" -> move = ExtremeSelfPowerUpMove(source, name, args[5], MainStat.valueOf(args[6].toUpperCase()), SecondaryStat.valueOf(args[7].toUpperCase()))
                    "debuff" -> move = DebuffMove(type, source, name, args[5])
                }
            } catch (e: Exception) {
            }

            return move
        }
    }

    fun getMainStatValue(user: CombatHandler.CombatParticipant): Int {
        return when (mainStat) {
            MainStat.STRENGTH -> user.strength
            MainStat.INTELLIGENCE -> user.intelligence
            MainStat.POWER -> user.power
            else -> {
                0
            }
        }
    }
    open fun effect(user: CombatHandler.CombatParticipant, combat: Combat){}
    abstract fun getBasePower() : Double

    open fun performMove(user: CombatHandler.CombatParticipant, combat: Combat) {
        when (type) {
            Type.MELEE -> {
                targets.forEach {
                    if (Math.abs(it.area - user.area) <= 1) {
                        if (decideIfHit(user, it, combat))
                            calculateDamage(user, it, combat)
                    } else combat.addLineToInfo("${user.name} tried to use $name on ${it.name}, but they were too far away.")
                }
            }
            Type.RANGE -> {
                targets.forEach {
                    if (Math.abs(it.area - user.area) >= 2) {
                        if (decideIfHit(user, it, combat))
                            calculateDamage(user, it, combat)
                    } else combat.addLineToInfo("${user.name} tried to use $name on ${it.name}, but they were too close.")
                }
            }
            Type.UTILITY -> {
                effect(user, combat)
            }
        }
    }

    open fun decideIfHit(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat): Boolean {
        if (target.dead) return false
        val diff = target.speed - user.accuracy/2
        val chance = 75 - diff
        val roll = Tess.rand.nextInt(100)

        return if (roll <= chance) {
            combat.addLineToInfo("${user.name} hit ${target.name} (${TessUtils.numberToLetter(combat.participants.indexOf(target))}) with $name.")
            true
        } else {
            combat.addLineToInfo("${user.name}'s $name was dodged by ${target.name} (${TessUtils.numberToLetter(combat.participants.indexOf(target))}).")
            false
        }
    }

    open fun calculateDamage(user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {

        if(combat !is PvpCombat) {
            if(user.faction == target.faction){
                combat.addLineToInfo("But the target is in the same faction, so no damage was done.")
                return
            }
        }
        val diff = getMainStatValue(user).toDouble()/target.defense.toDouble()

        this.doDamage((getBasePower() + Tess.rand.nextInt(getBasePower().toInt()/5)) * diff, user, target, combat)
    }

    open fun doDamage(amount: Double, user: CombatHandler.CombatParticipant, target: CombatHandler.CombatParticipant, combat: Combat) {
        var damage = amount
        if (damage <= 0.0) damage = 0.0
        target.health -= damage
        combat.addLineToInfo("  ${target.name} took < $damage > damage, and now has <${target.health}> health.")
        if(this is IOngoingMove){
            if(!target.ongoingEffects.any { it.move == this }) {
                combat.addLineToInfo("  ${user.name} applied ongoing effect to ${target.name}.")
                target.ongoingEffects.add(CombatHandler.OngoingEffect(user, this.roundsAffecting(), this))
            }
        }
        if (target.health <= 0) {
            target.dead = true
            target.killer = user
            combat.addLineToInfo("  ${target.name} has died")
        }
    }

    open fun saveData(): String {
        return "${getStorageName()}/${mainStat.name}/${type.name}/$source/$name"
    }

    abstract fun getStorageName(): String

    enum class MainStat {
        STRENGTH,
        INTELLIGENCE,
        POWER,
        NONE
    }

    enum class SecondaryStat {
        SPEED,
        ACCURACY,
        DEFENSE
    }

    enum class Type {
        MELEE,
        RANGE,
        UTILITY
    }

    enum class Source {
        PHYSICAL,
        TECH,
        POWER
    }
}
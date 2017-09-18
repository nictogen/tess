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
                when (args[0]) {
                    "basicDamage" -> move = BasicDamageMove(getMainStat(args[1]), getAttackType(args[2]), getSource(args[3]), args[4])
                    "selfPowerUp" -> move = SelfPowerUpMove(getSource(args[1]), getMainStat(args[2]), getSecondaryStat(args[3]), args[4])
                    "longCombatMove" -> move = LongCombatMove(getSource(args[1]), args[2])
                    "counter" -> move = CounterMove(getMainStat(args[1]), getType(args[2]), getSource(args[3]), args[4])
                    "selfDestruct" -> move = SelfDestructMove(getMainStat(args[1]), getSource(args[3]), args[4])
                    "heal" -> move = HealOtherMove(getMainStat(args[1]), getSource(args[2]), args[3])
                    "switch" -> move = SwitchMove(getSource(args[1]), args[2])
                    "stunningDamage" -> move = StunningDamageMove(getMainStat(args[1]), getAttackType(args[2]), getSource(args[3]), args[4])
                    "absorb" -> move = AbsorbMove(getMainStat(args[1]), getAttackType(args[2]), getSource(args[3]), args[4])
                    "accurateDamage" -> move = AccurateDamageMove(getMainStat(args[1]), getAttackType(args[2]), getSource(args[3]), args[4])
                    "damageOverTime" -> move = DamageOverTimeMove(getMainStat(args[1]), getAttackType(args[2]), getSource(args[3]), args[4])
                    "randomExtremeSelfPowerUp" -> move = RandomExtremeSelfPowerUpMove(getSource(args[1]), args[2])
                    "healOverTime" -> move = HealOverTimeMove(getMainStat(args[1]), getAttackType(args[2]), getSource(args[3]), args[4])
                    "extremeSelfPowerUp" -> move = ExtremeSelfPowerUpMove(getSource(args[1]), getMainStat(args[2]), getSecondaryStat(args[3]), MainStat.valueOf(args[4].toUpperCase()), SecondaryStat.valueOf(args[5].toUpperCase()), args[6])
                    "debuff" -> move = DebuffMove(getAttackType(args[1]), getSource(args[2]), getMainStat(args[3]), getSecondaryStat(args[4]), args[4])
                    "powerSteal" -> move = PowerStealMove(getAttackType(args[1]), getSource(args[2]), getMainStat(args[3]), getSource(args[4]), args[5])
                    "buffNearby" -> move = BuffNearbyMove(getSource(args[1]), getMainStat(args[2]), getSecondaryStat(args[3]), args[4])
                }
            } catch (e: Exception) { }

            return move
        }

        fun getMainStat(string: String) : MainStat{
            return when(string.toUpperCase()){
                "POWER" -> Move.MainStat.valueOf(string.toUpperCase())
                "STRENGTH" -> Move.MainStat.valueOf(string.toUpperCase())
                "INTELLIGENCE" -> Move.MainStat.valueOf(string.toUpperCase())
                else -> Move.MainStat.NONE
            }
        }

        fun getSecondaryStat(string: String) : SecondaryStat {
            return when(string.toUpperCase()){
                "DEFENSE" -> SecondaryStat.DEFENSE
                "ACCURACY" -> SecondaryStat.ACCURACY
                "SPEED" -> SecondaryStat.SPEED
                else -> SecondaryStat.NONE
            }
        }

        fun getType(string: String) : Type {
            return Type.valueOf(string.toUpperCase())
        }

        fun getAttackType(string: String) : Type {
            return if(Type.valueOf(string.toUpperCase()) == Type.UTILITY) Type.MELEE else Type.valueOf(string.toUpperCase())
        }

        fun getSource(string: String) : Source {
            return Source.valueOf(string.toUpperCase())
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
        DEFENSE,
        NONE
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
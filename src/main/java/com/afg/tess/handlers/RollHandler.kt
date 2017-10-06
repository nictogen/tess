package com.afg.tess.handlers

import com.afg.tess.init.Tess

/**
 * Created by AFlyingGrayson on 9/26/17
 */
object RollHandler {

    fun roll(skill: PlayerHandler.Skill, penalty: Int): Result {
        return roll(penalty, skill.value)
    }

    fun roll(stat: PlayerHandler.Stat, penalty: Int): Result {
        return roll(penalty, stat.value)
    }

    fun opposedRoll(stat: PlayerHandler.Stat, penalty: Int, stat2: PlayerHandler.Stat, penalty2: Int): Result {
        return oppose(roll(penalty, stat.value), true, roll(penalty2, stat2.value), true)
    }

    fun opposedRoll(stat: PlayerHandler.Stat, penalty: Int, skill: PlayerHandler.Skill, penalty2: Int): Result {
        return oppose(roll(penalty, stat.value), true, roll(penalty2, skill.value), false)
    }

    fun opposedRoll(skill: PlayerHandler.Skill, penalty: Int, stat: PlayerHandler.Stat, penalty2: Int): Result {
        return oppose(roll(penalty, skill.value), false, roll(penalty2, stat.value), true)
    }

    fun opposedRoll(skill: PlayerHandler.Skill, penalty: Int, skill2: PlayerHandler.Skill, penalty2: Int): Result {
        return oppose(roll(penalty, skill.value), false, roll(penalty2, skill2.value), false)
    }

    fun oppose(result1 : Result, stat: Boolean, result2 : Result, stat2: Boolean) : Result {
        return when {
            result1 == Result.CRITICAL -> Result.CRITICAL
            result1.successValue < 0 || result2.successValue < 0 -> result1
            else -> {
                var result = result1.successValue - result2.successValue - if(stat && !stat2) 1 else if(!stat && stat2) - 1 else 0
                if(result < -3) result = -3
                if(result > 3) result = 3
                val list = Result.values()
                list.sortBy { it.successValue }
                return list[result + 3]
            }
        }
    }

    private fun roll(penalty: Int, target : Int) : Result {
        val amount = Tess.rand.nextInt(100) + 1
        return when {
            amount > 98 -> Result.FUMBLE
            amount == 1 -> Result.CRITICAL
            else -> when {
                amount - penalty < target / 5 -> Result.EXTREME
                amount - penalty < target / 2 -> Result.HARD
                amount - penalty < target -> Result.NORMAL
                else -> Result.FAIL
            }
        }
    }

    fun calculateDamage(base :Int, result: Result) : Int{
        return Math.floor(result.damageMod*base.toDouble()).toInt()
    }

    enum class Result(val resultName: String, val successValue : Int, val damageMod : Double) {
        FUMBLE("fumble", -3, 0.0),
        FAIL("fail", -2, 0.0),
        NEAR("near success", -1, 0.25),
        NORMAL("normal success", 0, 1.0),
        HARD("hard success", 1, 1.25),
        EXTREME("extreme success", 2, 1.5),
        CRITICAL("critical success", 3, 2.0)
    }
}
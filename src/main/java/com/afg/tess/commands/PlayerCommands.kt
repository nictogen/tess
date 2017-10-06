package com.afg.tess.commands

import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.RollHandler
import com.afg.tess.init.Tess
import com.afg.tess.util.TessUtils
import com.afg.tess.util.rpName
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType

/**
 * Created by AFlyingGrayson on 9/5/17
 */
object PlayerCommands {

    val pickupMap = HashMap<PlayerHandler.Player, PlayerHandler.Player>()

    @Command(aliases = arrayOf("!travel", "!t"))
    fun onTravel(info: CommandHandler.MessageInfo, location: String) {
        if (location.length > 1) {
            if (TessUtils.isAdmin(info.user) || info.player.location == "")
                LocationHandler.travelToLocationAnywhere(info.user, info.player, location)
            else
                LocationHandler.travelToLocation(info.player, location, info.message)
        } else
            LocationHandler.travelToLocation(info.player, Integer.parseInt(location), info.message)
    }

    @Command(aliases = arrayOf("!playerinfo", "!p"))
    fun onPlayerInfo(info: CommandHandler.MessageInfo): String {

        if (!info.message.isPrivateMessage && (info.message.channelReceiver != null && !info.message.channelReceiver.name.contains("spam")))
            return ""

        var string = "#${info.player.rpName} Player Info:\n"

        string += "\nHP: ${info.player.health}/${info.player.maxHealth}"
        string += "\nMana: ${info.player.mana}/${info.player.stats.first { it.type == PlayerHandler.StatType.MANA }.value}"
        string += "\nXP: ${info.player.xp}"
        string += "\n\nStats: "

        info.player.stats.forEach { stat ->
            string += "\n  ${stat.type.name.toLowerCase().capitalize()}: ${stat.value}"
        }
        string += "\n\nSkills: "
        val skills = info.player.skills
        skills.sortByDescending { it.value }
        skills.forEach {
            string += "\n  ${it.name}: ${it.value}"
            if (it.mastery) string += " *"
            if (it.weakPoint) string += " -"
            try {
                val type = PlayerHandler.MagicType.valueOf(it.name.toUpperCase())
                string += " Rank: ${info.player.masteries.first { it.type == type }.rank}"
            } catch (e: Exception) { }
        }
        string += "\n* Talent"
        string += "\n- Weak Point"

        return "```md\n$string```"
    }

    @Command(aliases = arrayOf("!addtostat"))
    fun onStat(info: CommandHandler.MessageInfo, statName: String, amount: Int): String {
        if(amount < 0) return ""
        val stat = info.player.stats.first { it.type.name.toLowerCase() == statName.toLowerCase() }
        if (amount + stat.value > 90) return "You can't increase a stat above 90."
        if (stat.type == PlayerHandler.StatType.MANA) return "You can't increase your mana through this trivial method, puny human."
        val cost = amount * 10
        if (info.player.xp >= cost) info.player.xp -= cost
        else return "${info.player.rpName} doesn't have enough xp ($cost) to increase their stat to that."
        stat.value += amount
        PlayerHandler.saveData(info.player)
        return "${info.player.rpName} increased their ${stat.type.name.toLowerCase().capitalize()} to ${stat.value}."
    }

    @Command(aliases = arrayOf("!addtoskill"))
    fun onSkill(info: CommandHandler.MessageInfo, skillName: String, amount: Int): String {
        if(amount < 0) return ""
        when {
            info.player.skills.any { it.name.toLowerCase() == skillName.toLowerCase() } -> {
                val skill = info.player.skills.first { it.name.toLowerCase() == skillName.toLowerCase() }
                try {
                    val type = PlayerHandler.MagicType.valueOf(skill.name.toUpperCase())
                    val mastery = info.player.masteries.first { it.type == type }
                    if (skill.value + amount > mastery.rank.max) {
                       return "You can't increase that skill to that level until you increase your mastery rank in it."
                    }
                } catch (e: Exception) { }
                when {
                    amount + skill.value > 50 && skill.weakPoint -> {
                        return "A skill that you're weak in can't be increased above 50"
                    }
                    amount + skill.value > 70 && !skill.mastery -> {
                        return "You can't increase a skill that you don't have a talent in above 70"
                    }
                    amount + skill.value > 90 -> {
                        return "You can't increase a skill above 90."
                    }
                }
                val cost = amount * 5
                return if (info.player.xp >= cost) {
                    info.player.xp -= cost
                    skill.value += amount
                    PlayerHandler.saveData(info.player)
                    "Added $amount to ${info.player.rpName}'s ${skill.name} for $cost."
                } else "${info.player.rpName} doesn't have enough xp ($cost) to increase their stat to that."
            }
            else -> return ""
        }
    }

    @Command(aliases = arrayOf("sleep"))
    fun onSleep(info: CommandHandler.MessageInfo): String {
        val permission = Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).setState(PermissionType.SEND_MESSAGES, PermissionState.ALLOWED).build()
        val permission2 = Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).setState(PermissionType.SEND_MESSAGES, PermissionState.DENIED).build()
        val channel = TessUtils.server.channels.first { it.name == "dreamland" }!!
        if (channel.getOverwrittenPermissions(info.user).getState(PermissionType.READ_MESSAGES) == PermissionState.ALLOWED) channel.updateOverwrittenPermissions(info.user, permission2)
        else channel.updateOverwrittenPermissions(info.user, permission)
        return ""
    }

    @Command(aliases = arrayOf("!roll", "!r"))
    fun onRoll(info: CommandHandler.MessageInfo, skillOrStatName: String, penalty: Int): String {
        return when {
            info.player.stats.any { it.type.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                val stat = info.player.stats.first { it.type.name.toLowerCase() == skillOrStatName.toLowerCase() }
                val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                "${info.player.rpName}'s ${stat.type.name.toLowerCase().capitalize()}$penaltyString earned a(n) ${RollHandler.roll(stat, penalty).resultName}."
            }
            info.player.skills.any { it.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                val skill = info.player.skills.first { it.name.toLowerCase() == skillOrStatName.toLowerCase() }
                val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                "${info.player.rpName}'s ${skill.name}$penaltyString earned a(n) ${RollHandler.roll(skill, penalty).resultName}."
            }
            info.player.spells.any { it.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                val spell = info.player.spells.first { it.name.toLowerCase() == skillOrStatName.toLowerCase() }
                val efficiency = info.player.skills.first { it.name == "Mana_Efficiency" }.value.toDouble()
                val cost = if (spell.type == PlayerHandler.MagicType.SUMMONING) spell.manaCost else Math.floor(spell.manaCost.toDouble() * ((100.0 - efficiency) / 100.0)).toInt()
                var string = if (info.player.mana >= cost) {
                    val skill = TessUtils.skillFromMagicType(spell.type, info.player)
                    val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                    val result = RollHandler.roll(skill, penalty + cost)
                    "${info.player.rpName}'s ${spell.name}$penaltyString earned a(n) ${result.resultName}." +
                            if (spell.damage > 0) "\n If this spell was an attack, the recommended damage would be ${RollHandler.calculateDamage(spell.damage, result)}." else ""
                } else "${info.player.rpName} ran out of mana before they could finish casting the spell, and it failed."
                info.player.mana -= cost
                if (info.player.mana < 0) info.player.mana = 0
                string += if (info.player.mana == 0) "\n${info.player.rpName}'s mana is now ${info.player.mana}, they are now knocked out."
                else "\n${info.player.rpName}'s mana is now ${info.player.mana}"
                return string
            }
            else -> ""
        }
    }

    @Command(aliases = arrayOf("!opposedroll", "!or"))
    fun onOpposedRoll(info: CommandHandler.MessageInfo, skillOrStatName: String, penalty: Int, player: PlayerHandler.Player, skillOrStatName2: String, penalty2: Int): String {
        return when {
            info.player.stats.any { it.type.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                val stat = info.player.stats.first { it.type.name.toLowerCase() == skillOrStatName.toLowerCase() }
                val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                val result = when {
                    player.stats.any { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                        val stat2 = player.stats.first { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() }
                        RollHandler.opposedRoll(stat, penalty, stat2, penalty2)
                    }
                    player.skills.any { it.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                        val skill = player.skills.first { it.name.toLowerCase() == skillOrStatName2.toLowerCase() }
                        RollHandler.opposedRoll(stat, penalty, skill, penalty2)
                    }
                    player.spells.any { it.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                        val spell = player.spells.first { it.name.toLowerCase() == skillOrStatName2.toLowerCase() }
                        val efficiency = player.skills.first { it.name == "Mana_Efficiency" }.value.toDouble()
                        val cost = if (spell.type == PlayerHandler.MagicType.SUMMONING) spell.manaCost else Math.floor(spell.manaCost.toDouble() * ((100.0 - efficiency) / 100.0)).toInt()
                        val skill2 = TessUtils.skillFromMagicType(spell.type, player)
                        var string = ""
                        if (player.mana < cost) {
                            string += "${info.player.rpName}'s ${stat.type.name.toLowerCase().capitalize()}$penaltyString earned a(n) ${RollHandler.roll(stat, penalty)}."
                            string += "\n${player.rpName} ran out of mana before they could finish casting the spell, and it failed."
                        } else {
                            string += "${info.player.rpName}'s ${stat.type.name.toLowerCase().capitalize()}$penaltyString, opposed by ${player.rpName}, earned a(n) ${RollHandler.opposedRoll(stat, penalty, skill2, penalty2 + cost)}."
                        }
                        player.mana -= cost
                        if (player.mana < 0) info.player.mana = 0
                        string += if (info.player.mana == 0) "\n${info.player.rpName}'s mana is now ${info.player.mana}, they are now knocked out."
                        else "\n${info.player.rpName}'s mana is now ${info.player.mana}"
                        return string
                    }
                    else -> null!!
                }
                "${info.player.rpName}'s ${stat.type.name.toLowerCase().capitalize()}$penaltyString, opposed by ${player.rpName}, earned a(n) ${result.resultName}."
            }
            info.player.skills.any { it.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                val skill = info.player.skills.first { it.name.toLowerCase() == skillOrStatName.toLowerCase() }
                val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                val result = when {
                    player.stats.any { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                        RollHandler.opposedRoll(skill, penalty, player.stats.first { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() }, penalty2)
                    }
                    player.skills.any { it.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                        RollHandler.opposedRoll(skill, penalty, player.skills.first { it.name.toLowerCase() == skillOrStatName2.toLowerCase() }, penalty2)
                    }
                    player.spells.any { it.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                        val spell = player.spells.first { it.name.toLowerCase() == skillOrStatName2.toLowerCase() }
                        val efficiency = player.skills.first { it.name == "Mana_Efficiency" }.value.toDouble()
                        val cost = if (spell.type == PlayerHandler.MagicType.SUMMONING) spell.manaCost else Math.floor(spell.manaCost.toDouble() * ((100.0 - efficiency) / 100.0)).toInt()
                        val skill2 = TessUtils.skillFromMagicType(spell.type, player)
                        var string = ""
                        if (player.mana < cost) {
                            string += "${info.player.rpName}'s ${skill.name.toLowerCase().capitalize()}$penaltyString earned a(n) ${RollHandler.roll(skill, penalty)}."
                            string += "\n${player.rpName} ran out of mana before they could finish casting the spell, and it failed."
                        } else {
                            string += "${info.player.rpName}'s ${skill.name.toLowerCase().capitalize()}$penaltyString, opposed by ${player.rpName}, earned a(n) ${RollHandler.opposedRoll(skill, penalty, skill2, penalty2 + cost)}."
                        }
                        player.mana -= cost
                        if (player.mana < 0) info.player.mana = 0
                        string += if (info.player.mana == 0) "\n${info.player.rpName}'s mana is now ${info.player.mana}, they are now knocked out."
                        else "\n${info.player.rpName}'s mana is now ${info.player.mana}"
                        return string
                    }
                    else -> null!!
                }
                "${info.player.rpName}'s ${skill.name}$penaltyString, opposed by ${player.rpName}, earned a(n) ${result.resultName}."
            }
            info.player.spells.any { it.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                val efficiency = info.player.skills.first { it.name == "Mana_Efficiency" }.value.toDouble()
                val spell = info.player.spells.first { it.name.toLowerCase() == skillOrStatName.toLowerCase() }
                val cost = if (spell.type == PlayerHandler.MagicType.SUMMONING) spell.manaCost else Math.floor(spell.manaCost.toDouble() * ((100.0 - efficiency) / 100.0)).toInt()
                var string = if (info.player.mana >= cost) {
                    val skill = TessUtils.skillFromMagicType(spell.type, info.player)
                    val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                    val result = when {
                        player.stats.any { it.type.name == skillOrStatName2.toUpperCase() } -> RollHandler.opposedRoll(skill, penalty + cost, player.stats.first { it.type.name == skillOrStatName2.toUpperCase() }, penalty2)
                        player.skills.any { it.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> RollHandler.opposedRoll(skill, penalty + cost, player.skills.first { it.name.toLowerCase() == skillOrStatName2.toLowerCase() }, penalty2)
                        else -> null!!
                    }
                    "${info.player.rpName}'s ${spell.name}$penaltyString, opposed by ${player.rpName}, earned a(n) ${result.resultName}." +
                            if (spell.damage > 0) "\n If this spell was an attack, the recommended damage would be ${RollHandler.calculateDamage(spell.damage, result)}." else ""
                } else "${info.player.rpName} ran out of mana before they could finish casting the spell, and it failed."
                info.player.mana -= cost
                if (info.player.mana < 0) info.player.mana = 0
                string += if (info.player.mana == 0) "\n${info.player.rpName}'s mana is now ${info.player.mana}, they are now knocked out."
                else "\n${info.player.rpName}'s mana is now ${info.player.mana}"
                return string
            }
            else -> ""
        }
    }

    @Command(aliases = arrayOf("!castvscast", "!cvc"))
    fun onCastVsCast(info: CommandHandler.MessageInfo, spell: PlayerHandler.Spell, modifier: Int, player: PlayerHandler.Player, spellName: String, modifier2: Int): String {
        val efficiency = info.player.skills.first { it.name == "Mana_Efficiency" }.value.toDouble()
        val cost = if (spell.type == PlayerHandler.MagicType.SUMMONING) spell.manaCost else Math.floor(spell.manaCost.toDouble() * ((100.0 - efficiency) / 100.0)).toInt()
        val skill = TessUtils.skillFromMagicType(spell.type, info.player)

        val efficiency2 = player.skills.first { it.name == "Mana_Efficiency" }.value.toDouble()
        val spell2 = player.spells.first { it.name.toLowerCase() == spellName.toLowerCase() }
        val cost2 = if (spell.type == PlayerHandler.MagicType.SUMMONING) spell2.manaCost else Math.floor(spell2.manaCost.toDouble() * ((100.0 - efficiency2) / 100.0)).toInt()
        val skill2 = TessUtils.skillFromMagicType(spell2.type, player)

        var string = ""

        var result = RollHandler.roll(skill, modifier + cost)
        var result2 = RollHandler.roll(skill2, modifier2 + cost)
        if (info.player.mana < cost) {
            string += "${info.player.rpName} ran out of mana before they could finish casting the spell, and it failed."
            result = RollHandler.Result.FAIL
        }
        if (player.mana < cost2) {
            string += "${player.rpName} ran out of mana before they could finish casting the spell, and it failed."
            result2 = RollHandler.Result.FAIL
        }
        when {
            result.successValue < 0 && result2.successValue < 0 -> {
                string += "Both spells failed, and had no effect"
            }
            result.successValue > result2.successValue -> {
                val trueResult = RollHandler.oppose(result, false, result2, false)
                string += "${info.player.rpName}'s ${spell.name} won, earning a(n) ${trueResult.resultName}." +
                        if (spell.damage > 0) "\n If this spell was an attack, the recommended damage would be ${RollHandler.calculateDamage(spell.damage, trueResult)}." else ""
            }
            result.successValue < result2.successValue -> {
                val trueResult = RollHandler.oppose(result2, false, result, false)
                string += "${player.rpName}'s ${spell2.name} won, earning a(n) ${trueResult.resultName}." +
                        if (spell2.damage > 0) "\n If this spell was an attack, the recommended damage would be ${RollHandler.calculateDamage(spell2.damage, trueResult)}." else ""

            }
            else -> {
                string += "The spells were of equal power, and counteracted each other."
            }
        }

        info.player.mana -= cost
        player.mana -= cost2

        if (info.player.mana < 0) info.player.mana = 0
        if (player.mana < 0) player.mana = 0

        string += if (info.player.mana == 0) "\n${info.player.rpName}'s mana is now ${info.player.mana}, they are now knocked out."
        else "\n${info.player.rpName}'s mana is now ${info.player.mana}"

        string += if (player.mana == 0) "\n${player.rpName}'s mana is now ${player.mana}, they are now knocked out."
        else "\n${player.rpName}'s mana is now ${player.mana}"
        return string
    }

    @Command(aliases = arrayOf("!spellbook", "!sb"))
    fun onSpellBook(info: CommandHandler.MessageInfo): String {
        var string = "```\n\n${info.player.rpName}'s Spells: "

        info.player.spells.forEach { spell ->
            string += "\n${spell.name}:"
            string += "\n Form: ${spell.rank} rank ${spell.type.name.toLowerCase().capitalize()}"
            string += "\n Mana Cost: ${spell.manaCost} Base Damage: ${spell.damage}, Maximum Modifier: ${spell.modifier}"
            string += "\n Desc: ${spell.description}"
        }
        return "$string```"
    }

    @Command(aliases = arrayOf("!damage", "!d"))
    fun onDamage(info: CommandHandler.MessageInfo, amount: Int): String {
        if (amount < 0) return "Don't try to cheat the system with negative damage you bitch."
        info.player.health -= amount
        var string = "${info.player.rpName} took $amount damage. "
        if (info.player.health < 0) info.player.health = 0
        string += if (info.player.health == 0) "${info.player.rpName}'s health is now ${info.player.health}, they are now knocked out."
        else "${info.player.rpName}'s health is now ${info.player.health}."
        return string
    }

    @Command(aliases = arrayOf("!scaleddamage", "!sd"))
    fun onScaledDamage(info: CommandHandler.MessageInfo, amount: Int, successRankName : String): String {
        if (amount < 0) return "Don't try to cheat the system with negative damage you bitch."
        val successRank = RollHandler.Result.valueOf(successRankName.toUpperCase())
        val r = RollHandler.calculateDamage(amount, successRank)
        var string = "${info.player.rpName} took $r damage. "
        info.player.health -= r
        if (info.player.health < 0) info.player.health = 0
        string += if (info.player.health == 0) "${info.player.rpName}'s health is now ${info.player.health}, they are now knocked out."
        else "${info.player.rpName}'s health is now ${info.player.health}."
        return string
    }

    @Command(aliases = arrayOf("!losemana", "!lm"))
    fun onLoseMana(info: CommandHandler.MessageInfo, amount: Int): String {
        if (amount < 0) return "Don't try to cheat the system with negative mana you bitch."
        info.player.mana -= amount
        var string = "${info.player.rpName} lost $amount mana. "
        if (info.player.mana < 0) info.player.mana = 0
        string += if (info.player.mana == 0) "${info.player.rpName}'s mana is now ${info.player.mana}, they are now knocked out."
        else "${info.player.rpName}'s mana is now ${info.player.mana}."
        return string
    }


}

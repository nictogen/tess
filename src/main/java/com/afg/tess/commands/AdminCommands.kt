package com.afg.tess.commands

import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.RollHandler
import com.afg.tess.init.Tess
import com.afg.tess.util.TessUtils
import com.afg.tess.util.rpName


/**
 * Created by AFlyingGrayson on 9/5/17
 */
object AdminCommands {

    val admins = ArrayList<PlayerHandler.Player>()

    @Command(aliases = arrayOf("!adminmode", "!a"))
    fun onAdminMode(info: CommandHandler.MessageInfo) {
        if (TessUtils.isAdmin(info.user)) {
            if (!admins.contains(info.player)) {
                admins.add(info.player)
                LocationHandler.unlockAllChannels(info.player, info.user)
            } else {
                admins.remove(info.player)
                LocationHandler.lockAllOtherChannels(info.player, info.user)
            }
        }
    }

    @Command(aliases = arrayOf("!rl"))
    fun onReloadLocations(info: CommandHandler.MessageInfo): String {
        return if (TessUtils.isAdmin(info.user)) {
            LocationHandler.loadLocations()
            "Reloaded Locations"
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!setskill"))
    fun onSetSkill(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, skillName: String, amount: Int, mastery: Boolean, weak: Boolean): String {
        return if (TessUtils.isAdmin(info.user)) {
            when {
                player.skills.any { it.name.toLowerCase() == skillName.toLowerCase() } -> {
                    val skill = player.skills.first { it.name.toLowerCase() == skillName.toLowerCase() }
                    val diff = amount - skill.value
                    val cost = diff * 5
                    if (player.xp >= cost) {
                        player.xp -= cost
                        skill.value = amount
                        skill.mastery = mastery
                        skill.weakPoint = weak
                        PlayerHandler.saveData(player)
                        "Added $diff to ${player.rpName}'s ${skill.name} for $cost."
                    } else return "${player.rpName} doesn't have enough xp ($cost) to increase their stat to that."
                }
                else -> {
                    player.skills.add(PlayerHandler.Skill(skillName, amount, mastery, weak))
                    return "Added $skillName to ${player.rpName}'s skills"
                }
            }
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!setstat"))
    fun onStat(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, statName: String, amount: Int): String {
        return if (TessUtils.isAdmin(info.user)) {
            val stat = player.stats.first { it.type.name.toLowerCase() == statName.toLowerCase() }
            stat.value = amount
            PlayerHandler.saveData(player)
            "Set ${player.rpName}'s stat at ${stat.value}."
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!setmasteryrank", "!smr"))
    fun onMastery(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, masteryName: String, rankName: String): String {
        return if (TessUtils.isAdmin(info.user)) {
            val mastery = player.masteries.first { it.type.name == masteryName.toUpperCase() }
            val rank = PlayerHandler.MagicRank.valueOf(rankName.toUpperCase())
            mastery.rank = rank
            PlayerHandler.saveData(player)
            "Set ${player.rpName}'s ${mastery.type.name.toLowerCase()} rank to $rank."
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!addspell"))
    fun onSpell(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, spellName: String, rankName: String, type : String, cost : Int, damage : Int, modifier : Int): String {
        return if (TessUtils.isAdmin(info.user)) {
            if(!player.spells.any { it.name == spellName}) {
                var desc = ""
                info.message.content.split(" ").subList(7, info.message.content.split(" ").size).forEach { desc += it + " " }
                player.spells.add(PlayerHandler.Spell(spellName, PlayerHandler.MagicRank.valueOf(rankName.toUpperCase()), PlayerHandler.MagicType.valueOf(type.toUpperCase()), cost, damage, modifier, ""))
            }
            PlayerHandler.saveData(player)
            "Added $spellName to ${player.rpName}'s spellbook."
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!removespell"))
    fun onRemoveSpell(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, spellName: String): String {
        return if (TessUtils.isAdmin(info.user)) {
            player.spells.remove(player.spells.first{ it.name == spellName  })
            PlayerHandler.saveData(player)
            "Removed $spellName from ${player.rpName}'s spellbook."
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!removeskill"))
    fun onRemoveSkill(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, skillName: String): String {
        return if (TessUtils.isAdmin(info.user)) {
            return when {
                player.skills.any { it.name.toLowerCase() == skillName.toLowerCase() } -> {
                    val skill = player.skills.first { it.name.toLowerCase() == skillName.toLowerCase() }
                    player.skills.remove(skill)
                    "Removed ${skill.name} from ${player.rpName}"
                }
                else -> "No skills with that name."
            }
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!xp"))
    fun onXP(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, amount: Int): String {
        return if (TessUtils.isAdmin(info.user)) {
            player.xp += amount
            PlayerHandler.saveData(player)
            "Gave ${player.rpName} $amount xp."
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!forceroll", "!fr"))
    fun onRoll(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, skillOrStatName: String, penalty: Int): String {
        return if (TessUtils.isAdmin(info.user)) {
            when {
                player.stats.any { it.type.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                    val stat = player.stats.first { it.type.name.toLowerCase() == skillOrStatName.toLowerCase() }
                    val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                    "${player.rpName}'s ${stat.type.name.toLowerCase().capitalize()}$penaltyString earned a(n) ${RollHandler.roll(stat, penalty).resultName}."
                }
                player.skills.any { it.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                    val skill = player.skills.first { it.name.toLowerCase() == skillOrStatName.toLowerCase() }
                    val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                    "${player.rpName}'s ${skill.name}$penaltyString earned a(n) ${RollHandler.roll(skill, penalty).resultName}."
                }
                else -> ""
            }
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!forceopposedroll", "!for"))
    fun onOpposedRoll(info: CommandHandler.MessageInfo, player1: PlayerHandler.Player, skillOrStatName: String, penalty: Int, player2: PlayerHandler.Player, skillOrStatName2: String, penalty2: Int): String {
        if (TessUtils.isAdmin(info.user)) {
            return when {
                player1.stats.any { it.type.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                    val stat = player1.stats.first { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() }
                    val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                    val result = when {
                        player2.stats.any { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                            val stat2 = player2.stats.first { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() }
                            RollHandler.opposedRoll(stat, penalty, stat2, penalty2)
                        }
                        player2.skills.any { it.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                            val skill = player2.skills.first { it.name.toLowerCase() == skillOrStatName2.toLowerCase() }
                            RollHandler.opposedRoll(stat, penalty, skill, penalty2)
                        }
                        else -> null!!
                    }
                    "${player1.rpName}'s ${stat.type.name.toLowerCase().capitalize()}$penaltyString earned a(n) ${result.resultName}."
                }
                player1.skills.any { it.name.toLowerCase() == skillOrStatName.toLowerCase() } -> {
                    val skill = player1.skills.first { it.name.toLowerCase() == skillOrStatName.toLowerCase() }
                    val penaltyString = if (penalty == 0) "" else ", modified by $penalty,"
                    val result = when {
                        player2.stats.any { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                            RollHandler.opposedRoll(skill, penalty, player2.stats.first { it.type.name.toLowerCase() == skillOrStatName2.toLowerCase() }, penalty2)
                        }
                        player2.skills.any { it.name.toLowerCase() == skillOrStatName2.toLowerCase() } -> {
                            RollHandler.opposedRoll(skill, penalty, player2.skills.first { it.name.toLowerCase() == skillOrStatName2.toLowerCase() }, penalty2)
                        }
                        else -> null!!
                    }
                    "${player1.rpName}'s ${skill.name}$penaltyString earned a(n) ${result.resultName}."
                }
                else -> ""
            }
        } else return "You are not an admin."
    }

    @Command(aliases = arrayOf("!hp"))
    fun onAddHealth(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, amount: Int): String {
        if (TessUtils.isAdmin(info.user)) {
            player.health += amount
            if (player.health < 0) player.health = 0
            if (player.health > player.maxHealth) player.health = player.maxHealth
            if (player.health == 0) return "${player.rpName}'s health is now ${player.health}, they are now knocked out."
            return "${player.rpName}'s health is now ${player.health}"
        } else return "You are not an admin."
    }

    @Command(aliases = arrayOf("!mana", "!m"))
    fun onAddMana(info: CommandHandler.MessageInfo, player: PlayerHandler.Player, amount: Int): String {
        if (TessUtils.isAdmin(info.user)) {
            player.mana += amount
            if (player.mana < 0) player.mana = 0
            if (player.mana > player.stats.first { it.type == PlayerHandler.StatType.MANA }.value) player.mana = player.stats.first { it.type == PlayerHandler.StatType.MANA }.value
            if (player.mana == 0) return "${player.rpName}'s mana is now ${player.mana}, they are now knocked out."
            return "${player.rpName}'s mana is now ${player.mana}"
        } else return "You are not an admin."
    }

    @Command(aliases = arrayOf("!clear"))
    fun onClear(info: CommandHandler.MessageInfo): String {
        return if (TessUtils.isAdmin(info.user)) {
            if(info.message.channelReceiver != null){
                info.message.channelReceiver.getMessageHistory(100).get().messages.forEach {
                    if(it.content.isNotEmpty())
                        if(it.author == Tess.api.yourself) it.delete()
                        else when(it.content[0]){
                            '!' -> it.delete()
                            ':' -> it.delete()
                            '[' -> it.delete()
                        }
                }
            }
            ""
        } else "You are not an admin."
    }

}
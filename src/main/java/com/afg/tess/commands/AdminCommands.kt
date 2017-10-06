package com.afg.tess.commands

import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.init.Tess
import com.afg.tess.util.TessUtils
import com.afg.tess.util.rpName
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter


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
        return if (TessUtils.isAdmin(info.user)) PlayerCommands.onRoll(CommandHandler.MessageInfo(info.message, player, info.user), skillOrStatName, penalty)
        else "You are not an admin."
    }

    @Command(aliases = arrayOf("!forceopposedroll", "!for"))
    fun onOpposedRoll(info: CommandHandler.MessageInfo, player1: PlayerHandler.Player, skillOrStatName: String, penalty: Int, player2: PlayerHandler.Player, skillOrStatName2: String, penalty2: Int): String {
        return if (TessUtils.isAdmin(info.user)) PlayerCommands.onOpposedRoll(CommandHandler.MessageInfo(info.message, player1, info.user), skillOrStatName, penalty, player2, skillOrStatName2, penalty2)
        else "You are not an admin."
    }

    @Command(aliases = arrayOf("!forcecastvscast", "!fcvc"))
    fun onCastVsCast(info: CommandHandler.MessageInfo, player1: PlayerHandler.Player, spellName : String, modifier: Int, player2: PlayerHandler.Player, spellName2: String, modifier2: Int): String {
        return if (TessUtils.isAdmin(info.user)) PlayerCommands.onCastVsCast(CommandHandler.MessageInfo(info.message, player1, info.user), player1.spells.first { it.name == spellName }, modifier, player2, spellName2, modifier2)
        else "You are not an admin."
    }

    @Command(aliases = arrayOf("!force", "!f"))
    fun onForce(info: CommandHandler.MessageInfo, player1: PlayerHandler.Player): String {
        val string = info.message.content.split(" ")
        var message = ""
        (2 until string.size).forEach { message += ("${string[it]} ") }
        string.forEach { message += (it + " ") }
        return if (TessUtils.isAdmin(info.user)){
            CommandHandler.readCommand(info.message, message, player1)
            ""
        }
        else "You are not an admin."
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

    @Command(aliases = arrayOf("!createnpc", "!cnpc"))
    fun onCreateNPC(info: CommandHandler.MessageInfo, name : String) : String{

        //Creating the player
        val player = PlayerHandler.Player()
        player.playerID = name

        //Stats
        player.stats.add(PlayerHandler.Stat(PlayerHandler.StatType.STRENGTH, 1))
        player.stats.add(PlayerHandler.Stat(PlayerHandler.StatType.COORDINATION, 1))
        player.stats.add(PlayerHandler.Stat(PlayerHandler.StatType.CONSTITUTION, 1))
        player.stats.add(PlayerHandler.Stat(PlayerHandler.StatType.SENSE, 1))
        player.stats.add(PlayerHandler.Stat(PlayerHandler.StatType.CHARM, 1))
        player.stats.add(PlayerHandler.Stat(PlayerHandler.StatType.MANA, 1))
        player.stats.add(PlayerHandler.Stat(PlayerHandler.StatType.INTELLIGENCE, 1))

        //Mana Skills
        player.skills.add(PlayerHandler.Skill("Mana_Efficiency", 0, false, false))
        player.skills.add(PlayerHandler.Skill("Construction", 0, false, false))
        player.skills.add(PlayerHandler.Skill("Projection", 0, false, false))
        player.skills.add(PlayerHandler.Skill("Control", 0, false, false))
        player.skills.add(PlayerHandler.Skill("Alchemy", 0, false, false))
        player.skills.add(PlayerHandler.Skill("Healing", 0, false, false))
        player.skills.add(PlayerHandler.Skill("Rune_Magic", 0, false, false))
        player.skills.add(PlayerHandler.Skill("Summoning", 0, false, false))
        player.skills.add(PlayerHandler.Skill("Transformation", 0, false, false))

        //Adding the player
        PlayerHandler.players.add(player)
        PlayerHandler.saveData(player)

        val playerList = File(Tess.playerListFilePath)
        playerList.createNewFile()

        val fileWriter = FileWriter(playerList)
        val printWriter = PrintWriter(fileWriter)

        for (s in PlayerHandler.players)
            printWriter.println(s.playerID)

        printWriter.close()

        return "Created npc $name."
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
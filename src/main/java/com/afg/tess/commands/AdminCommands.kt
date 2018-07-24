package com.afg.tess.commands

import com.afg.tess.*
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.moves.*
import com.afg.tess.combat.npcs.Ero
import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter


/**
 * Created by AFlyingGrayson on 9/5/17
 */
object AdminCommands {

    val admins = ArrayList<PlayerData.Player>()

    @Command(aliases = arrayOf("!rollstats", "!r"))
    fun onRollStats(info: CommandHandler.MessageInfo, player: PlayerData.Player): String {
        if (TessUtils.isModerator(info.user)) {
            var strength = 3
            var speed = 3
            var health = 3
            var intelligence = 3
            var power = 3
            var accuracy = 3
            var defense = 3

            val rand = Tess.rand
            player.moves.clear()

            when (player.race) {
                PlayerData.Race.HUMAN -> {
                    strength += rand.nextInt(4)
                    speed += rand.nextInt(4)
                    health += rand.nextInt(4)
                    intelligence += rand.nextInt(4)
                    power += rand.nextInt(4)
                    accuracy += rand.nextInt(4)
                    defense += rand.nextInt(4)
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                }
                PlayerData.Race.EX -> {
                    strength += rand.nextInt(4)
                    speed += rand.nextInt(4)
                    health += rand.nextInt(4)
                    intelligence += rand.nextInt(4)
                    power += rand.nextInt(8) + 5
                    accuracy += rand.nextInt(4)
                    defense += rand.nextInt(5)
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                }
                PlayerData.Race.EROS -> {
                    strength += rand.nextInt(10) + 5
                    speed += rand.nextInt(10) + 5
                    health += rand.nextInt(15) + 5
                    intelligence += rand.nextInt(2)
                    power += rand.nextInt(15) + 5
                    accuracy += rand.nextInt(8) + 5
                    defense += rand.nextInt(15) + 5
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Bite"))
                    player.moves.add(BasicDamageMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Energy_Blast"))
                }
                PlayerData.Race.EROEX -> {
                    strength += rand.nextInt(10)
                    speed += rand.nextInt(10)
                    health += rand.nextInt(10)
                    intelligence += rand.nextInt(5)
                    power += rand.nextInt(10)
                    accuracy += rand.nextInt(10)
                    defense += rand.nextInt(10)
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                    player.moves.add(BasicDamageMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Energy_Blast"))
                    player.moves.add(SelfDestructMove(Move.MainStat.POWER, Move.Source.POWER, "Self_Destruct"))
                    player.moves.add(LongCombatMove(Move.Source.POWER, "Fly"))
                }
                PlayerData.Race.EROEXY -> {
                    strength += rand.nextInt(10) + 5
                    speed += rand.nextInt(10) + 5
                    health += rand.nextInt(10) + 5
                    intelligence += rand.nextInt(5)
                    power += rand.nextInt(10) + 10
                    accuracy += rand.nextInt(10) + 5
                    defense += rand.nextInt(10) + 5
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                    player.moves.add(BasicDamageMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Energy_Blast"))
                    player.moves.add(SelfDestructMove(Move.MainStat.POWER, Move.Source.POWER, "Self_Destruct"))
                    player.moves.add(LongCombatMove(Move.Source.POWER, "Fly"))
                }
                PlayerData.Race.CONDUCTOR -> {
                    strength += rand.nextInt(5)
                    speed += rand.nextInt(5)
                    health += rand.nextInt(5)
                    intelligence += rand.nextInt(10) + 5
                    power += rand.nextInt(10)
                    accuracy += rand.nextInt(10)
                    defense += rand.nextInt(5)
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                    player.moves.add(BasicDamageMove(Move.MainStat.INTELLIGENCE, Move.Type.RANGE, Move.Source.TECH, "Energy_Blast"))
                    player.moves.add(SelfDestructMove(Move.MainStat.INTELLIGENCE, Move.Source.TECH, "Self_Destruct"))
                    player.moves.add(HealOtherMove(Move.MainStat.INTELLIGENCE, Move.Source.TECH, "Healing_Nanites"))
                }
                PlayerData.Race.HYBRIDEX -> {
                    strength += rand.nextInt(10) + 5
                    speed += rand.nextInt(10) + 5
                    health += rand.nextInt(10) + 5
                    intelligence += rand.nextInt(5)
                    power += rand.nextInt(10) + 10
                    accuracy += rand.nextInt(10) + 5
                    defense += rand.nextInt(10) + 5
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                    player.moves.add(BasicDamageMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Energy_Blast"))
                    player.moves.add(SelfDestructMove(Move.MainStat.POWER, Move.Source.POWER, "Self_Destruct"))
                    player.moves.add(LongCombatMove(Move.Source.POWER, "Fly"))
                }
                PlayerData.Race.EXY -> {
                    strength += rand.nextInt(5) + 5
                    speed += rand.nextInt(5) + 5
                    health += rand.nextInt(5) + 5
                    intelligence += rand.nextInt(4)
                    power += rand.nextInt(10) + 10
                    accuracy += rand.nextInt(5) + 5
                    defense += rand.nextInt(5) + 5
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                }
                PlayerData.Race.ADAPTOR -> {
                    strength += rand.nextInt(5) + 2
                    speed += rand.nextInt(5) + 2
                    health += rand.nextInt(5) + 2
                    intelligence += rand.nextInt(10) + 7
                    power += rand.nextInt(10) + 2
                    accuracy += rand.nextInt(10) + 2
                    defense += rand.nextInt(5) + 2
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                    player.moves.add(BasicDamageMove(Move.MainStat.INTELLIGENCE, Move.Type.RANGE, Move.Source.TECH, "Energy_Blast"))
                    player.moves.add(SelfDestructMove(Move.MainStat.INTELLIGENCE, Move.Source.TECH, "Self_Destruct"))
                    player.moves.add(HealOtherMove(Move.MainStat.INTELLIGENCE, Move.Source.TECH, "Healing_Nanites"))
                }
                PlayerData.Race.TATTOOEDHUMAN -> {
                    strength += rand.nextInt(4)
                    speed += rand.nextInt(4)
                    health += rand.nextInt(4)
                    intelligence += rand.nextInt(4)
                    power += rand.nextInt(7) + 10
                    accuracy += rand.nextInt(4)
                    defense += rand.nextInt(4)
                    player.moves.add(BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Punch"))
                }
            }

            player.strength = strength
            player.speed = speed
            player.maxHealth = health
            player.health = health.toDouble()
            player.intelligence = intelligence
            player.power = power
            player.accuracy = accuracy
            player.defense = defense

            player.saveData()
            var string = "${player.rpName}'s Stats:\n"
            string += "\nStrength:       ${player.strength}"
            string += "\nSpeed:            ${player.speed}"
            string += "\nMax Health:  ${player.maxHealth}"
            string += "\nHealth:           ${player.health}"
            string += "\nIntelligence:  ${player.intelligence}"
            string += "\nPower:            ${player.power}"
            string += "\nAccuracy:        ${player.accuracy}"
            string += "\nDefense:         ${player.defense}"
            return string
        } else return "You aren't a moderator."
    }

    @Command(aliases = arrayOf("!spawnmonster", "!sm"))
    fun onSpawnMonster(info: CommandHandler.MessageInfo, location: String, rank: Int) {
        if (TessUtils.isAdmin(info.user) || TessUtils.isArcLeader(info.user))
            Ero.spawnMonster(LocationHandler.getLocationFromName(location)!!, info.message, rank, true)
    }

    @Command(aliases = arrayOf("!allowScan", "!as"))
    fun onAllowScan(info: CommandHandler.MessageInfo, player: PlayerData.Player): String {
        return if (TessUtils.isModerator(info.user)) {
            player.canScan = 1
            player.saveData()
            "${player.rpName} is now allowed to scan."
        } else "You aren't a moderator"
    }

    @Command(aliases = arrayOf("!adminmode", "!a"))
    fun onAdminMode(info: CommandHandler.MessageInfo) {
        if (TessUtils.isArcLeader(info.user) || TessUtils.isAdmin(info.user)) {
            if (!admins.contains(info.player)) {
                admins.add(info.player)
                LocationHandler.unlockAllChannels(info.player, info.user)
            } else {
                admins.remove(info.player)
                LocationHandler.lockAllOtherChannels(info.player, info.user)
            }
        }
    }

    @Command(aliases = arrayOf("!tess"))
    fun onTess(info: CommandHandler.MessageInfo): String {
        return if (TessUtils.isAdmin(info.user)) {
            var string = ""
            info.message.content.split(" ").subList(1, info.message.content.split(" ").size).forEach { string += "$it " }
            string
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!reloadlocations", "!rl"))
    fun onReloadLocations(info: CommandHandler.MessageInfo): String {
        return if (TessUtils.isAdmin(info.user) || TessUtils.isArcLeader(info.user)) {
            LocationHandler.loadLocations()
            Factions.loadData()
            "Reloaded Locations"
        } else "You are not an admin or arc leader."
    }

    @Command(aliases = arrayOf("!newfaction", "nf"))
    fun onNewFaction(info: CommandHandler.MessageInfo, factionName: String, adminName: String, gruntName: String, firstMember: PlayerData.Player): String {
        if (TessUtils.isAdmin(info.user)) {
            val faction = Factions.Faction()
            faction.name = factionName
            faction.adminName = adminName
            faction.gruntName = gruntName
            faction.admins.add(firstMember)
            val factionList = File(Tess.factionListFilePath)
            factionList.createNewFile()

            Factions.factionList.add(faction)

            val fileWriter = FileWriter(factionList)
            val printWriter = PrintWriter(fileWriter)

            for (f in Factions.factionList)
                printWriter.println(f.name)

            printWriter.close()

            faction.saveData()

            return "Created faction: ${faction.name}"
        } else return "You are not an admin."
    }

    @Command(aliases = arrayOf("!race"))
    fun onRaceSet(info: CommandHandler.MessageInfo, player: PlayerData.Player, race: String): String {
        if (TessUtils.isModerator(info.user)) {
            val newRace = PlayerData.Race.valueOf(race.toUpperCase())
            var s = ""
            if (player.race == PlayerData.Race.EROEX && newRace == PlayerData.Race.EROEXY) {
                player.strength += 5
                player.power += 5
                player.accuracy += 5
                player.speed += 5
                player.maxHealth += 5
                player.health = player.maxHealth.toDouble()
                player.defense += 5
                s += "$${player.rpName} has evolved!\n"
            } else if (player.race == PlayerData.Race.EX && newRace == PlayerData.Race.EXY) {
                player.power += 10
                player.accuracy += 5
                player.speed += 5
                s += "$${player.rpName} has evolved!\n"
            } else if (player.race == PlayerData.Race.HUMAN && newRace == PlayerData.Race.TATTOOEDHUMAN) {
                player.maxHealth += 5 + Tess.rand.nextInt(3)
                player.health = player.maxHealth.toDouble()
                player.power += 8
                player.power += Tess.rand.nextInt(10)
                s += "$${player.rpName} has evolved!\n"
            }
            player.race = newRace
            player.saveData()
            s += "${player.name}'s race is now ${player.race.name.toLowerCase().capitalize()}"
            return s
        } else return "You are not a moderator."
    }

    @Command(aliases = arrayOf("!bartender"))
    fun onBartender(info: CommandHandler.MessageInfo, player: PlayerData.Player): String {
        return if (TessUtils.isModerator(info.user)) {
            player.bartender = 1
            player.saveData()
            "${player.rpName} is now a bartender."
        } else "You are not a moderator."
    }

    @Command(aliases = arrayOf("!arcleader"))
    fun onArcLeader(info: CommandHandler.MessageInfo, player: PlayerData.Player): String {
        return if (TessUtils.isAdmin(info.user)) {
            if (player.arcleader == 0) {
                player.arcleader = 1
                player.saveData()
                "${player.rpName} is now the leader of an arc."
            } else {
                player.arcleader = 0
                player.saveData()
                "${player.rpName} is no longer the leader of an arc."
            }
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!income"))
    fun onIncome(info: CommandHandler.MessageInfo, player: PlayerData.Player, income: Int): String {
        return if (TessUtils.isAdmin(info.user)) {
            player.income = income
            player.saveData()
            "Set ${player.rpName}'s income."
        } else "You are not an admin."
    }

    @Command(aliases = arrayOf("!endcombat", "!ec"))
    fun onEndCombat(info: CommandHandler.MessageInfo): String {
        return if (TessUtils.isAdmin(info.user)) {
            CombatHandler.combatList.remove(TessUtils.getCombat(info.message.channel))
            "Ended Combat"
        } else "You are not an admin."
    }
}

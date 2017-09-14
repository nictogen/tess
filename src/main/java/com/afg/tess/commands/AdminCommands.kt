package com.afg.tess.commands

import com.afg.tess.*
import com.afg.tess.combat.moves.*
import com.afg.tess.combat.npcs.Ero
import de.btobastian.javacord.entities.message.Message
import de.btobastian.sdcf4j.Command
import de.btobastian.sdcf4j.CommandExecutor
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter


/**
 * Created by AFlyingGrayson on 9/5/17
 */
object AdminCommands : CommandExecutor {

    @Command(aliases = arrayOf("!rollstats", "!r"), description = "Roll a player's stats")
    fun onRollStats(message: Message, args: Array<String>) {
        message.delete()
        if (TessUtils.isModerator(message.author)) {
            if (args.size == 1) {
                val player = TessUtils.getPlayer(args[0])
                val name = TessUtils.getName(TessUtils.getRpMember(args[0])!!)
                if (player != null) {
                    var strength = 3
                    var speed = 3
                    var health = 3
                    var intelligence = 3
                    var power = 3
                    var accuracy = 3
                    var defense = 3

                    val rand = Tess.rand
                    player.moves.clear()

                    val race = TessUtils.getRace(player.playerID)
                    when (race) {
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
                            player.moves.add(SelfDestructMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Self_Destruct"))
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
                            player.moves.add(SelfDestructMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Self_Destruct"))
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
                            player.moves.add(SelfDestructMove(Move.MainStat.INTELLIGENCE, Move.Type.RANGE, Move.Source.TECH, "Self_Destruct"))
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
                            player.moves.add(SelfDestructMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Self_Destruct"))
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
                            player.moves.add(SelfDestructMove(Move.MainStat.INTELLIGENCE, Move.Type.RANGE, Move.Source.TECH, "Self_Destruct"))
                            player.moves.add(HealOtherMove(Move.MainStat.INTELLIGENCE, Move.Source.TECH, "Healing_Nanites"))
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
                    var string = "$name's Stats:\n"
                    string += "\nStrength:       ${player.strength}"
                    string += "\nSpeed:            ${player.speed}"
                    string += "\nMax Health:  ${player.maxHealth}"
                    string += "\nHealth:           ${player.health}"
                    string += "\nIntelligence:  ${player.intelligence}"
                    string += "\nPower:            ${player.power}"
                    string += "\nAccuracy:        ${player.accuracy}"
                    string += "\nDefense:         ${player.defense}"
                    message.reply(string)
                    return@onRollStats
                } else message.reply(args[0] + " isn't a player.")
            } else message.reply("You didn't do the right parameters.")
        }
    }

    @Command(aliases = arrayOf("!spawnmonster", "!sm"), description = "Spawn a monster")
    fun onSpawnMonster(message: Message, args: Array<String>) {
        message.delete()
        if (TessUtils.isAdmin(message.author)) {
            val location = LocationHandler.getLocationFromName(args[0])
            if (location != null) {
                try {
                    if (Ero.spawnMonster(location, message, Integer.parseInt(args[1]), true))
                        TessUtils.getChannelFromName("announcements")?.sendMessage("ALERT: A rank ${Integer.parseInt(args[1])} Ero has appeared in ${location.channel.name}.")
                } catch (e: Exception) {
                }

            }
        }
    }

    @Command(aliases = arrayOf("!allowScan", "!as"), description = "Allows a scan")
    fun onAllowScan(message: Message, args: Array<String>) {
        message.delete()
        if (TessUtils.isModerator(message.author)) {
            if (args.size == 1) {
                val player = TessUtils.getPlayer(args[0])
                val name = TessUtils.getName(TessUtils.getRpMember(args[0])!!)
                if (player != null) {
                    player.canScan = 1
                    player.saveData()
                    message.reply("$name is now allowed to scan.")
                }
            }
        }

    }

    @Command(aliases = arrayOf("!adminmode", "!a"), description = "Toggles admin mode")
    fun onAdminMode(message: Message) {
        message.delete()
        val roles = message.author.getRoles(TessUtils.getServer())

        if (roles.contains(TessUtils.getRole("Perms"))) {
            if (!roles.contains(TessUtils.getRole("Admin")))
                TessUtils.getRole("Admin")?.addUser(message.author)
            else TessUtils.getRole("Admin")?.removeUser(message.author)
        }

    }

    @Command(aliases = arrayOf("!tess"), description = "Talks with tess")
    fun onTess(message: Message, args: Array<String>) {
        message.delete()
        if (TessUtils.isAdmin(message.author)) {
            var string = ""
            args.forEach { string += "$it " }
            message.reply(string)
        }
    }

    @Command(aliases = arrayOf("!reloadlocations", "!rl"), description = "Reloads locations")
    fun onReloadLocations(message: Message) {
        message.delete()
        if (TessUtils.isAdmin(message.author)) {
            LocationHandler.loadLocations()
            Factions.loadData()
            message.reply("Reloaded Locations")
        }

    }

    @Command(aliases = arrayOf("!newfaction", "nf"), description = "Creates a new faction")
    fun onNewFaction(message: Message, args: Array<String>) {
        message.delete()
        if (TessUtils.isAdmin(message.author)) {
            try {
                val faction = Factions.Faction()
                faction.name = args[0]
                faction.adminName = args[1]
                faction.gruntName = args[2]
                faction.admins.add(TessUtils.getPlayer(args[3])!!)
                val factionList = File(Tess.factionListFilePath)
                factionList.createNewFile()

                Factions.factionList.add(faction)

                val fileWriter = FileWriter(factionList)
                val printWriter = PrintWriter(fileWriter)

                for (f in Factions.factionList)
                    printWriter.println(f.name)

                printWriter.close()

                faction.saveData()

                message.reply("Created faction: ${faction.name}")
            } catch (e: Exception) {
            }
        }
    }

    @Command(aliases = arrayOf("!race"), description = "Sets a player's race")
    fun onRaceSet(message: Message, args: Array<String>) {
        message.delete()
        if (TessUtils.isModerator(message.author) && args.size == 2) {
            val player = TessUtils.getPlayer(args[0])
            val name = TessUtils.getName(TessUtils.getRpMember(args[0])!!)
            if (player != null) {
                try {
                    player.race = PlayerData.Race.valueOf(args[1])
                    message.reply("$name's race is now ${player.race.name.toLowerCase().capitalize()}")
                } catch (e: Exception) {
                }
            }
        }

    }

}
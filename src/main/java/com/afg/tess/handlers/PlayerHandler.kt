package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.util.ISaveable
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import java.util.*


/**
 * Created by AFlyingGrayson on 9/5/17
 */
object PlayerHandler {

    val players = ArrayList<Player>()

    /**
     * Creates a new player and saves it to the data file
     */
    fun createPlayer(member: User, message: Message) {
        players.forEach { player ->
            if (player.playerID == member.id) {
                message.reply(member.name + " is already a player.")
                return
            }
        }

        //Creating the player
        val player = Player()
        player.playerID = member.id

        //Stats
//        player.stats.add(Stat(StatType.STRENGTH, 1))
//        player.stats.add(Stat(StatType.COORDINATION, 1))
//        player.stats.add(Stat(StatType.CONSTITUTION, 1))
//        player.stats.add(Stat(StatType.CHARM, 1))
//        player.stats.add(Stat(StatType.INTELLIGENCE, 1))
//        player.stats.addAll(race.getStartingStats())
//        player.talents.addAll(race.getStartingTalents())
//        player.talents.addAll(playerClass.getStartingTalents())
//        player.race = race::class.simpleName!!
//        player.playerClass = playerClass::class.simpleName!!

        //Adding the player
        players.add(player)
        player.saveData()

        message.reply(member.name + " is now a player.")
    }

    /**
     * The actual player, along with all its data variables
     */
    class Player : ISaveable{
        var playerID = ""
        var location = UUID.randomUUID().toString()
//        var race = ""
//        var playerClass = ""
//        var stats = ArrayList<Stat>()
//        var talents = ArrayList<Talent>()
//        var xp = 0
//        val maxHealth : Int
//        get() = if(stats.any { it.type == StatType.STRENGTH} && stats.any { it.type == StatType.CONSTITUTION }) ((stats.first { it.type == StatType.STRENGTH }.value + stats.first{ it.type == StatType.CONSTITUTION}.value)) else 0
//        var health = maxHealth

        override fun getFolderPath() = Tess.playerDataFolderPath!!

        override fun getFileName() = playerID
    }

    class Stat(private val type: StatType, var value: Int) {
        override fun toString(): String {
            return "$type$$value"
        }
    }

    enum class StatType {
        STRENGTH,
        COORDINATION,
        CONSTITUTION,
        INTELLIGENCE,
        CHARM
    }

}
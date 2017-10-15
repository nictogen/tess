package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.players.items.Item
import com.afg.tess.players.skills.Skill
import com.afg.tess.players.skills.fighter.Strike
import com.afg.tess.util.ISaveable
import de.btobastian.javacord.entities.channels.ServerTextChannelBuilder
import de.btobastian.javacord.entities.message.Message
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by AFlyingGrayson on 9/5/17
 */
object PlayerHandler {

    val players = ArrayList<Player>()

    /**
     * Creates a new player and saves it to the data file
     */
    fun createPlayer(message: Message, name : String) {

        //Creating the player
        val player = Player()

        player.channelID = ServerTextChannelBuilder(message.channel.asServerTextChannel().get().server).setName("rp-name").create().get().id.toString()
        player.rpName = name
        //Stats
//        player.stats.add(Stat(StatType.STRENGTH, race.strength))
//        player.stats.add(Stat(StatType.COORDINATION, race.coordination))
//        player.stats.add(Stat(StatType.HEALTH, race.health))
//        player.stats.add(Stat(StatType.CHARM, race.charm))
//        player.stats.add(Stat(StatType.INTELLIGENCE, race.intelligence))
//        player.stats.add(Stat(StatType.MAGIC, race.magic))
//        player.stats.add(Stat(StatType.LUCK, race.luck))
//
//        player.skills.add(playerClass.defaultSkill.getID())
//        player.skills.addAll(race.skills.map { it.getID() })
//
//        player.race = race
//
//        player.playerClass = playerClass

        //Adding the player
        players.add(player)
        player.saveData()

        message.channel.sendMessage("Created player: $name")
    }

    /**
     * The actual player, along with all its data variables
     */
    class Player : ISaveable {
        var channelID = ""
        var rpName = ""
        var location = UUID.randomUUID().toString()
        var combatLocationX = -1
        var combatLocationY = -1
        var race = Race.HUMAN
        var playerClass = Class.FIGHTER
        var stats = ArrayList<Stat>()
        var skills = ArrayList<String>()
        var inventory = ArrayList<Item.ItemStack>()
        var mainHand : Item.ItemStack? = null
        var offHand : Item.ItemStack? = null
        var clothes : Item.ItemStack? = null
        var party = ""
        var blocking = false
        var recovering = false
//        var xp = 0
        var health = 1.0

        override fun getFolderPath() = Tess.playerDataFolderPath!!

        override fun getFileName() = channelID

        fun attack(player: Player, strengthPercent : Double, accuracy : Int) : String {
            return if(Tess.rand.nextInt(100) <= accuracy){
                val attack = stats.first { it.type == StatType.STRENGTH }.value.toDouble()*(strengthPercent/100)
                val defense = player.stats.first { it.type == StatType.COORDINATION }.value.toDouble()
                var damage = attack * attack / (attack + defense)
                damage = Math.round(damage * 100.0) / 100.0
                player.health -= damage
                player.health = Math.round(health * 100.0) / 100.0
                player.saveData()
                "${this.rpName} hit ${player.rpName} for $damage damage.\n${player.rpName} now has ${player.health} hp."
                //TODO death, defeat, etc
                //TODO evasion mechanics, armor, etc
                //TODO weapon accuracy/damage, mastery skills, etc
            } else {
                "${this.rpName}'s attack missed."
            }
        }
    }

    class Stat(val type: StatType, var value: Int) {
        override fun toString(): String { return "$type$$value" }
    }

    enum class StatType {
        STRENGTH,
        COORDINATION,
        HEALTH,
        INTELLIGENCE,
        CHARM,
        MAGIC,
        LUCK
    }

    enum class Race(var strength : Int, var coordination : Int, var health : Int, var intelligence : Int, var charm : Int, var magic : Int, var luck : Int, var skills : ArrayList<Skill>){
        HUMAN(5, 5, 5, 5, 5, 5, 5, arrayListOf())
    }

    enum class Class(var defaultSkill : Skill, var skills: ArrayList<Skill>) {
        FIGHTER(Strike, arrayListOf())
    }

}
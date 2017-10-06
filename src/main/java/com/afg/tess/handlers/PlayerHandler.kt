package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.starProjectedType


/**
 * Created by AFlyingGrayson on 9/5/17
 */
object PlayerHandler {

    val players = ArrayList<Player>()

    /**
     * Loads all player data, used only when starting up
     */
    @Suppress("UNCHECKED_CAST")
    fun loadData() {
        val playerArray = ArrayList<String>()

        val dr = File(Tess.playerDataFolderPath)
        dr.mkdirs()

        val playerList = File(Tess.playerListFilePath)
        playerList.createNewFile()
        var scanner = Scanner(playerList)
        while (scanner.hasNextLine())
            playerArray.add(scanner.nextLine())
        scanner.close()

        playerArray.forEach { ID ->
            val playerDataFile = File(dr, ID)
            val playerData = HashMap<String, String>()
            playerDataFile.createNewFile()

            scanner = Scanner(playerDataFile)
            while (scanner.hasNextLine()) {
                val s = scanner.nextLine()
                playerData.put(TessUtils.getKey(s), TessUtils.getValue(s))
            }

            val player = Player()

            player::class.memberProperties.filter { it is KMutableProperty<*> }.forEach { property ->
                val varProperty = property as KMutableProperty<*>
                if (playerData[property.name] != null)
                    when {
                        property.returnType == Int::class.starProjectedType -> varProperty.setter.call(player, Integer.parseInt(playerData[property.name]))
                        property.returnType == String::class.starProjectedType -> varProperty.setter.call(player, playerData[property.name])
                        property.returnType == Boolean::class.starProjectedType -> varProperty.setter.call(player, playerData[property.name] == "true")
                        property.returnType.isSubtypeOf(ArrayList::class.starProjectedType) -> {
                            val list = property.getter.call(player) as ArrayList<*>
                            list.clear()
                            val dataStrings = TessUtils.listFromString(playerData[property.name]!!)
                            dataStrings.forEach {
                                val args = it.split("$")
                                if(args.size > 1)
                                when (property.name) {
                                    "stats" -> (list as ArrayList<Stat>).add(Stat(StatType.valueOf(args[0]), Integer.parseInt(args[1])))
                                    "skills" -> (list as ArrayList<Skill>).add(Skill(args[0], Integer.parseInt(args[1]), args[2] == "true", args[3] == "true"))
                                    "spells" -> (list as ArrayList<Spell>).add(Spell(args[0], MagicRank.valueOf(args[1]), MagicType.valueOf(args[2]), Integer.parseInt(args[3]), Integer.parseInt(args[4]), Integer.parseInt(args[5]), args[6]))
                                    "masteries" -> (list as ArrayList<MasteryLevel>).add(MasteryLevel(MagicType.valueOf(args[0]), MagicRank.valueOf(args[1])))
                                }
                            }
                        }
                    }
            }
            players.add(player)
        }
    }

    /**
     * Creates a new player and saves it to the data file
     */
    fun createPlayer(member: User, message: Message) {
        players.forEach { player ->
            if (player.playerID == member.mentionTag) {
                message.reply(member.name + " is already a player.")
                return
            }
        }

        //Creating the player
        val player = Player()
        player.playerID = member.id

        //Stats
        player.stats.add(Stat(StatType.STRENGTH, 1))
        player.stats.add(Stat(StatType.COORDINATION, 1))
        player.stats.add(Stat(StatType.CONSTITUTION, 1))
        player.stats.add(Stat(StatType.SENSE, 1))
        player.stats.add(Stat(StatType.CHARM, 1))
        player.stats.add(Stat(StatType.MANA, 1))
        player.stats.add(Stat(StatType.INTELLIGENCE, 1))

        //Mana Skills
        player.skills.add(Skill("Mana_Efficiency", 0, false, false))
        player.skills.add(Skill("Construction", 0, false, false))
        player.skills.add(Skill("Projection", 0, false, false))
        player.skills.add(Skill("Control", 0, false, false))
        player.skills.add(Skill("Alchemy", 0, false, false))
        player.skills.add(Skill("Healing", 0, false, false))
        player.skills.add(Skill("Rune_Magic", 0, false, false))
        player.skills.add(Skill("Summoning",  0, false, false))
        player.skills.add(Skill("Transformation", 0, false, false))

        //Adding the player
        players.add(player)
        saveData(player)

        val playerList = File(Tess.playerListFilePath)
        playerList.createNewFile()

        val fileWriter = FileWriter(playerList)
        val printWriter = PrintWriter(fileWriter)

        for (s in players)
            printWriter.println(s.playerID)

        printWriter.close()

        message.reply(member.name + " is now a player.")
    }

    /**
     * Saves a player to the data file
     */
    fun saveData(player: Player) {
        val data = HashMap<String, String>()
        player::class.memberProperties.forEach { property ->
            data.put(property.name, property.getter.call(player).toString())
        }
        val dr = File(Tess.playerDataFolderPath)
        dr.mkdirs()
        val playerDataFile = File(dr, player.playerID)
        playerDataFile.createNewFile()
        val fileWriter = FileWriter(playerDataFile)
        val printWriter = PrintWriter(fileWriter)
        data.forEach { k, v -> printWriter.println(k + "=" + v) }
        printWriter.close()
    }

    /**
     * The actual player, along with all its data variables
     */
    class Player {
        var playerID = ""
        var location = "dock"
        var stats = ArrayList<Stat>()
        var skills = ArrayList<Skill>()
        var spells = ArrayList<Spell>()
        var masteries = ArrayList<MasteryLevel>()
        var xp = 0
        var mana = 0
        val maxHealth : Int
        get() = if(stats.any { it.type == StatType.STRENGTH} && stats.any { it.type == StatType.CONSTITUTION }) ((stats.first { it.type == StatType.STRENGTH }.value + stats.first{ it.type == StatType.CONSTITUTION}.value)) / 5 else 0
        var health = maxHealth
    }

    class Stat(val type: StatType, var value: Int) {
        override fun toString(): String {
            return "$type$$value"
        }
    }

    class Skill(var name: String, var value: Int, var mastery : Boolean, var weakPoint : Boolean) {
        override fun toString(): String {
            return "$name$$value$$mastery$$weakPoint"
        }
    }

    class Spell(var name: String, var rank: MagicRank, var type : MagicType, var manaCost: Int, var damage: Int, var modifier : Int, var description: String){
        override fun toString(): String {
            return "$name$$rank$$type$$manaCost$$damage$$modifier$$description"
        }
    }

    class MasteryLevel(var type: MagicType, var rank: MagicRank){
        override fun toString(): String {
            return "$type$$rank"
        }
    }

    enum class MagicType{
        CONSTRUCTION,
        PROJECTION,
        CONTROL,
        ALCHEMY,
        HEALING,
        RUNE_MAGIC,
        SUMMONING,
        TRANSFORMATION,
        MANA_EFFICIENCY
    }

    enum class MagicRank(var max: Int){
        F(10),
        C(20),
        B(45),
        A(70),
        S(85),
        D(100)
    }

    enum class StatType {
        STRENGTH,
        COORDINATION,
        CONSTITUTION,
        SENSE,
        INTELLIGENCE,
        CHARM,
        MANA
    }

}
package com.afg.tess

import com.afg.tess.combat.moves.Move
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap



/**
 * Created by AFlyingGrayson on 9/5/17
 */
object PlayerData {

    val players = ArrayList<Player>()

    fun killPlayer(player: Player){
        val newPlayer = Player()
        newPlayer.name = player.name
        newPlayer.playerID = player.playerID
        player.backupData()
        players.remove(player)
        players.add(newPlayer)
        newPlayer.saveData()
    }

    fun loadData(){
        val playerArray = ArrayList<String>()

        val dr = File(Tess.playerDataFolderPath)
        dr.mkdirs()

        val playerList = File(Tess.playerListFilePath)
        playerList.createNewFile()
        var scanner = Scanner(playerList)
        while (scanner.hasNextLine())
            playerArray.add(scanner.nextLine())
        scanner.close()

        playerArray.forEach {
            name ->

            val playerDataFile = File(dr, name)
            val playerData = HashMap<String, String>()
            playerDataFile.createNewFile()

            scanner = Scanner(playerDataFile)
            while (scanner.hasNextLine()){
                val s = scanner.nextLine()
                playerData.put(TessUtils.getKey(s), TessUtils.getValue(s))
            }

            val player = Player()
            player.loadData(playerData)
            players.add(player)
        }
    }

    fun createPlayer(member: User, message: Message){
        players.forEach { player ->
            if(player.playerID == member.mentionTag){
                message.reply(member.name + " is already a player.")
                return
            }
        }
        val player = Player()
        player.name = member.name
        player.playerID = member.id
        players.add(player)
        player.saveData()

        val playerList = File(Tess.playerListFilePath)
        playerList.createNewFile()

        val fileWriter = FileWriter(playerList)
        val printWriter = PrintWriter(fileWriter)

        for (s in players)
            printWriter.println(s.name)

        printWriter.close()

        message.reply(member.name + " is now a player.")
    }

    class Player {
        var name = ""
        var money = 0.0
        var playerID = ""
        var backpackSize = 10
        val items = ArrayList<ItemStack>()
        val moves = ArrayList<Move>()
        var location = ""

        var maxHealth = 1
        var strength = 0
        var speed = 0
        var health = 1.0
        var intelligence = 0
        var power = 0
        var accuracy = 0
        var defense = 0

        var canScan = 0

        var race = Race.HUMAN

        fun loadData(data : HashMap<String, String>){
            if(data["money"] != null)           money = data["money"]!!.toDouble()
            if(data["playerID"] != null)        playerID =  data["playerID"]!!
            if(data["name"] != null)            name = data["name"]!!
            if(data["backpackSize"] != null)    backpackSize = Integer.parseInt(data["backpackSize"])
            if(data["strength"] != null)        strength = Integer.parseInt(data["strength"])
            if(data["speed"] != null)           speed = Integer.parseInt(data["speed"])
            if(data["health"] != null)          health = data["health"]!!.toDouble()
            if(data["intelligence"] != null)    intelligence = Integer.parseInt(data["intelligence"])
            if(data["power"] != null)           power = Integer.parseInt(data["power"])
            if(data["maxHealth"] != null)       maxHealth = Integer.parseInt(data["maxHealth"])
            if(data["accuracy"] != null)        accuracy = Integer.parseInt(data["accuracy"])
            if(data["defense"] != null)         defense = Integer.parseInt(data["defense"])
            if(data["canScan"] != null)         canScan = Integer.parseInt(data["canScan"])
            if(data["location"] != null)        location = data["location"]!!
            if(data["race"] != null)            race = Race.valueOf(data["race"]!!)
            items.clear()
            (0..backpackSize)
                    .filter { data["item$it"] != null }
                    .forEach { items.add(ItemStack(data["item$it"]!!))}
            (0..4)
                    .filter { data["move$it"] != null }
                    .forEach {
                        val move = Move.createMove(data["move$it"]!!)
                        moves.add(move)
                    }
        }

        fun saveData() {
            val data = HashMap<String, String>()
            data.put("money", money.toString())
            data.put("playerID", playerID)
            data.put("name", name)
            data.put("backpackSize", backpackSize.toString())
            data.put("strength", strength.toString())
            data.put("speed", speed.toString())
            data.put("health", health.toString())
            data.put("intelligence", intelligence.toString())
            data.put("power", power.toString())
            data.put("maxHealth", maxHealth.toString())
            data.put("accuracy", accuracy.toString())
            data.put("defense", defense.toString())
            data.put("canScan", canScan.toString())
            data.put("location", location)
            data.put("race", race.name)
            var id = 0
            items.forEach { data.put("item${id++}", it.saveData()) }
            id = 0
            moves.forEach { data.put("move${id++}", it.saveData()) }
            val dr = File(Tess.playerDataFolderPath)
            dr.mkdirs()
            val playerDataFile = File(dr, name)
            playerDataFile.createNewFile()
            val fileWriter = FileWriter(playerDataFile)
            val printWriter = PrintWriter(fileWriter)
            data.forEach { k, v ->  printWriter.println(k + "=" + v)}
            printWriter.close()
        }

        fun backupData(){
            val data = HashMap<String, String>()
            data.put("money", money.toString())
            data.put("playerID", playerID)
            data.put("name", name)
            data.put("backpackSize", backpackSize.toString())
            data.put("strength", strength.toString())
            data.put("speed", speed.toString())
            data.put("health", health.toString())
            data.put("intelligence", intelligence.toString())
            data.put("power", power.toString())
            data.put("maxHealth", maxHealth.toString())
            data.put("accuracy", accuracy.toString())
            data.put("defense", defense.toString())
            data.put("canScan", canScan.toString())
            data.put("location", location)
            data.put("race", race.name)
            var id = 0
            items.forEach { data.put("item${id++}", it.saveData()) }
            id = 0
            moves.forEach { data.put("move${id++}", it.saveData()) }
            val dr = File(Tess.playerDataFolderPath)
            dr.mkdirs()
            val playerDataFile = File(dr, "${name}_backup")
            playerDataFile.createNewFile()
            val fileWriter = FileWriter(playerDataFile)
            val printWriter = PrintWriter(fileWriter)
            data.forEach { k, v ->  printWriter.println(k + "=" + v)}
            printWriter.close()
        }


    }

    enum class Stat {
        STRENGTH,
        SPEED,
        HEALTH,
        INTELLIGENCE,
        POWER,
        MAXHEALTH,
        ACCURACY,
        DEFENSE
    }

    enum class Race {
        HUMAN,
        EROS,
        EROEX,
        EROEXY,
        HYBRIDEX,
        EX,
        EXY,
        CONDUCTOR,
        ADAPTOR
    }
}
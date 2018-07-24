package com.afg.tess

import com.afg.tess.combat.moves.Move
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.user.User
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
                playerData[TessUtils.getKey(s)] = TessUtils.getValue(s)
            }

            val player = Player()
            player.loadData(playerData)
            players.add(player)
        }
    }

    fun createPlayer(member: User, message: Message){
        players.forEach { player ->
            if(member.mentionTag.contains(player.playerID.toString())){
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
        var playerID : Long = 0
        var backpackSize = 10
        val items = ArrayList<ItemStack>()
        val moves = ArrayList<Move>()
        val contacts = LinkedList<Long>()
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
        var bartender = 0
        var drunkness = 0

        var income = 0

        var arcleader = 0

        var race = Race.HUMAN

        fun loadData(data : HashMap<String, String>){
            if(data["money"] != null)           money = data["money"]!!.toDouble()
            if(data["playerID"] != null)        playerID =  data["playerID"]!!.toLong()
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
            if(data["bartender"] != null)       bartender = Integer.parseInt(data["bartender"])
            if(data["drunkness"] != null)       drunkness = Integer.parseInt(data["drunkness"])
            if(data["arcleader"] != null)       arcleader = Integer.parseInt(data["arcleader"])
            if(data["income"] != null)          income = Integer.parseInt(data["income"])
            contacts.clear()
            if(data["contacts"] != null){ data["contacts"]?.split("$")?.forEach { if(it.isNotEmpty()) contacts.add(it.toLong()) } }
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
            data["money"] = money.toString()
            data["playerID"] = playerID.toString()
            data["name"] = name
            data["backpackSize"] = backpackSize.toString()
            data["strength"] = strength.toString()
            data["speed"] = speed.toString()
            data["health"] = health.toString()
            data["intelligence"] = intelligence.toString()
            data["power"] = power.toString()
            data["maxHealth"] = maxHealth.toString()
            data["accuracy"] = accuracy.toString()
            data["defense"] = defense.toString()
            data["canScan"] = canScan.toString()
            data["location"] = location
            data["race"] = race.name
            data["bartender"] = bartender.toString()
            data["drunkness"] = drunkness.toString()
            data["arcleader"] = arcleader.toString()
            data["income"] = income.toString()
            var id = 0
            items.forEach { data["item${id++}"] = it.saveData() }
            id = 0
            moves.forEach { data["move${id++}"] = it.saveData() }
            var contactList = ""
            contacts.forEach {
                contactList += "$it$"
            }
            data["contacts"] = contactList
            val dr = File(Tess.playerDataFolderPath)
            dr.mkdirs()
            val playerDataFile = File(dr, name)
            playerDataFile.createNewFile()
            val fileWriter = FileWriter(playerDataFile)
            val printWriter = PrintWriter(fileWriter)
            data.forEach { k, v ->  printWriter.println("$k=$v")}
            printWriter.close()
        }

        fun backupData(){
            val data = HashMap<String, String>()
            data["money"] = money.toString()
            data["playerID"] = playerID.toString()
            data["name"] = name
            data["backpackSize"] = backpackSize.toString()
            data["strength"] = strength.toString()
            data["speed"] = speed.toString()
            data["health"] = health.toString()
            data["intelligence"] = intelligence.toString()
            data["power"] = power.toString()
            data["maxHealth"] = maxHealth.toString()
            data["accuracy"] = accuracy.toString()
            data["defense"] = defense.toString()
            data["canScan"] = canScan.toString()
            data["location"] = location
            data["race"] = race.name
            data["bartender"] = bartender.toString()
            data["drunkness"] = drunkness.toString()
            data["arcleader"] = arcleader.toString()
            data["income"] = income.toString()
            var id = 0
            items.forEach { data["item${id++}"] = it.saveData() }
            id = 0
            moves.forEach { data["move${id++}"] = it.saveData() }
            val dr = File(Tess.playerDataFolderPath)
            dr.mkdirs()
            val playerDataFile = File(dr, "${name}_backup")
            playerDataFile.createNewFile()
            val fileWriter = FileWriter(playerDataFile)
            val printWriter = PrintWriter(fileWriter)
            data.forEach { k, v ->  printWriter.println("$k=$v")}
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
        ADAPTOR,
        TATTOOEDHUMAN
    }

}

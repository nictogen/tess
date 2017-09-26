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

        playerArray.forEach { ID ->
            val playerDataFile = File(dr, ID)
            val playerData = HashMap<String, String>()
            playerDataFile.createNewFile()

            scanner = Scanner(playerDataFile)
            while (scanner.hasNextLine()){
                val s = scanner.nextLine()
                playerData.put(TessUtils.getKey(s), TessUtils.getValue(s))
            }

            val player = Player()

            player::class.memberProperties.filter { it is KMutableProperty<*> }.forEach { property ->
                val varProperty = property as KMutableProperty<*>
                if(playerData[property.name] != null)
                    when(property.returnType){
                        Int::class.starProjectedType -> varProperty.setter.call(player, Integer.parseInt(playerData[property.name]))
                        String::class.starProjectedType -> varProperty.setter.call(player, playerData[property.name])
                    }
            }
            players.add(player)
        }
    }

    /**
     * Creates a new player and saves it to the data file
     */
    fun createPlayer(member: User, message: Message){
        players.forEach { player ->
            if(player.playerID == member.mentionTag){
                message.reply(member.name + " is already a player.")
                return
            }
        }
        val player = Player()
        player.playerID = member.id
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
     * Loads a player from the data file
     */
    fun saveData(player: Player) {
        val data = HashMap<String, String>()
        player::class.memberProperties.forEach { property -> data.put(property.name, property.getter.call(player).toString()) }
        val dr = File(Tess.playerDataFolderPath)
        dr.mkdirs()
        val playerDataFile = File(dr, player.playerID)
        playerDataFile.createNewFile()
        val fileWriter = FileWriter(playerDataFile)
        val printWriter = PrintWriter(fileWriter)
        data.forEach { k, v ->  printWriter.println(k + "=" + v)}
        printWriter.close()
    }

    /**
     * The actual player, along with all its data variables
     */
    class Player {
        var playerID = ""
        var location = ""
    }

}
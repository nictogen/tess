package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.util.ISaveable
import de.btobastian.javacord.entities.channels.ServerTextChannelBuilder
import de.btobastian.javacord.entities.message.Message


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

        val server = message.channel.asServerTextChannel().get().server

        player.serverID = server.id
        player.channelID = ServerTextChannelBuilder(server).setCategory(server.channelCategories.first { it.name.toLowerCase().contains("rp") }).setName("rp-${name.toLowerCase().replace(" ", "-")}").create().get().id
        player.rpName = name
        player.location = ServerHandler.serverList.first { it.id == server.id }.defaultLocation

        //Adding the player
        players.add(player)
        player.saveData()

        message.channel.sendMessage("Created player: $name")
    }

    /**
     * The actual player, along with all its data variables
     */
    class Player : ISaveable {
        var channelID : Long = 0
        var serverID : Long = 0
        var rpName = ""
        var location = ""

        override fun getFolderPath() = Tess.playerDataFolderPath!!
        override fun getFileName() = channelID.toString()
    }
}
package com.afg.tess.init

import com.afg.tess.commands.AdminCommands
import com.afg.tess.commands.PlayerCommands
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.TimeHandler
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.listener.message.MessageCreateListener

/**
 * Created by AFlyingGrayson on 9/7/17
 */
class Main {

    companion object {

        @JvmStatic
        fun main() {
            //Connect to ARP Server Account
            Tess.api = PrivateTokens.getAPI()
            Tess.api.connectBlocking()

            //Connect to Current Arc Account
            Tess.arcApi = PrivateTokens.getNAPI()
            Tess.arcApi.connectBlocking()

            //Register handler to listen for the 'make player' message
            Tess.api.registerListener(MessageCreateListener { _, message -> if (message.channelReceiver != null && message.content.contains("make") && message.content.contains("a player") && TessUtils.isAdmin(message.author)) message.channelReceiver.server.members.forEach { m -> if (message.content.contains(m.id) && !PlayerHandler.players.any { it.playerID == m.id }) PlayerHandler.createPlayer(m, message) } })

            //Register handler to listen for commands
            Tess.api.registerListener(CommandHandler)

            //Add all created commands to the handler
            CommandHandler.loadCommands(AdminCommands)
            CommandHandler.loadCommands(PlayerCommands)

            //Load player data
            PlayerHandler.loadData()

            //Load locations
            LocationHandler.loadLocations()

            //Start keeping time
            TimeHandler.keepTime()
        }


    }


}
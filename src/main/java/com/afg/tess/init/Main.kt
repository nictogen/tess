package com.afg.tess.init

import com.afg.tess.commands.AdminCommands
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ShipHandler
import com.afg.tess.handlers.TravelHandler
import com.afg.tess.util.ISaveable
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import de.btobastian.javacord.listener.message.MessageCreateListener
import java.util.*

/**
 * Created by AFlyingGrayson on 9/7/17
 */
class Main {

    companion object {

        var lastMinute = 0
        @JvmStatic
        fun main() {
            //Connect to ARP Server Account
            Tess.api = PrivateTokens.getAPI()
            Tess.api.connectBlocking()

            //Connect to Current Arc Account
//            Tess.arcApi = PrivateTokens.getNAPI()
//            Tess.arcApi.connectBlocking()

            Tess.api.registerListener(MessageCreateListener { _, message -> if (message.channelReceiver != null && message.content.contains("make") && message.content.contains("a player") && TessUtils.isAdmin(message.author)) message.channelReceiver.server.members.forEach { m -> if (message.content.contains(m.id) && !PlayerHandler.players.any { it.playerID == m.id }) PlayerHandler.createPlayer(m, message) } })


            //Register handler to listen for commands
            Tess.api.registerListener(CommandHandler)

            //Load the saveable things
            ISaveable.loadData(Tess.playerDataFolderPath, PlayerHandler.players)
            ISaveable.loadData(Tess.locationFolderPath, TravelHandler.locations)
            ISaveable.loadData(Tess.shipFolderPath, ShipHandler.ships)

            //Add all created commands to the handler
            CommandHandler.loadCommands(AdminCommands)

            TravelHandler.updateChannels()
//            CommandHandler.loadCommands(PlayerCommands)


            while(true){
                val minute = Calendar.getInstance().get(Calendar.MINUTE)
                if(lastMinute != minute) {
                    val permission = Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).build()
                    val permission2 = Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).build()
                    Tess.api.channels.forEach { c ->
                        if (c.topic != null && c.topic.toLowerCase().contains("nearby")) {
                            if(TravelHandler.locations.any { it.channelID == c.id }) {
                                val location = TravelHandler.locations.first { it.channelID == c.id }
                                if (PlayerHandler.players.any { it.location == location.uuid }) {
                                    PlayerHandler.players.forEach { p ->
                                        if (p.location == location.uuid)
                                            c.updateOverwrittenPermissions(TessUtils.getMember(p), permission)
                                        else c.updateOverwrittenPermissions(TessUtils.getMember(p), permission2)
                                    }
                                } else {
                                    location.channelID = ""; location.saveData(); c.delete()
                                }
                            } else c.delete()
                        }
                    }
                    lastMinute = minute
                }
            }

        }


    }


}
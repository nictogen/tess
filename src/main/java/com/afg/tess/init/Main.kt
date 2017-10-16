package com.afg.tess.init

import com.afg.tess.handlers.*
import com.afg.tess.util.ISaveable
import com.afg.tess.util.TessUtils
import java.io.File


/**
 * Created by AFlyingGrayson on 9/7/17
 */
class Main {

    companion object {

        @JvmStatic
        fun main() {
            //Connect to Traveling Tess Account
            Tess.api = PrivateTokens.getAPI()
            //Load the saveable things
            ISaveable.loadData(Tess.playerDataFolderPath, PlayerHandler.players, true)
            ISaveable.loadData(Tess.locationFolderPath, LocationHandler.locations, true)
            ISaveable.loadData(Tess.serverFolderPath, ServerHandler.serverList, true)


            Tess.api.addServerJoinListener { l ->
                if(!ServerHandler.serverList.any { it.id == l.server.id }) {
                    val server = ServerHandler.Server()
                    server.id = l.server.id
                    val location = LocationHandler.Location()
                    location.uuid = server.defaultLocation
                    location.name = "Starting Point"
                    location.unicodeEmoji = "\uD83C\uDF4B"
                    location.saveData()
                    LocationHandler.locations.add(location)
                    ServerHandler.serverList.add(server)
                    server.saveData()
                }
            }

            Tess.api.addMessageCreateListener { t ->
                val channel = t.message.serverTextChannel.get()
                val server = channel.server
                if(!t.message.author.get().isBot && t.message.content.contains(Tess.api.yourself.mentionTag) || (Tess.api.yourself.getNickname(server).isPresent && t.message.content.contains(Tess.api.yourself.nicknameMentionTag))){
                    val args = t.message.content.split(" ")
                    try {
                        if (TessUtils.isPlayerChannel(channel.id, server)) {
                            val player = TessUtils.getPlayer(channel.id, server.id)
                            if (args.size == 1) {
                                ReactionResponseHandler.TravelMessage(channel, LocationHandler.locations.first { it.uuid == player.location })
                            } else if (TessUtils.isAdmin(t.message.author.get(), server)) {
                                when {
                                    args[1].toLowerCase().contains("new") -> {
                                        val location = LocationHandler.createLocation(LocationHandler.locations.first { player.location == it.uuid }, args[2])
                                        if (server.customEmojis.any { t.message.content.contains(it.mentionTag) }) {
                                            location.customEmojiName = server.customEmojis.first { t.message.content.contains(it.mentionTag) }.name
                                        } else {
                                            location.unicodeEmoji = args[3]
                                        }
                                        location.saveData()
                                    }
                                    args[1].toLowerCase().contains("remove") -> {
                                        val location = LocationHandler.locations.first { player.location == it.uuid }.nearbyLocations.first { it.name.toLowerCase() == args[2].toLowerCase() }
                                        LocationHandler.locations.forEach {
                                            it.nearby.removeAll(it.nearby.filter { it == location.uuid })
                                        }
                                        LocationHandler.locations.remove(location)
                                        val dir = File(Tess.locationFolderPath)
                                        dir.mkdirs()
                                        val dataFile = File(dir, location.uuid)
                                        dataFile.createNewFile()
                                        dataFile.delete()
                                    }
                                    t.message.content.contains("greeting") -> {

                                    }
                                }
                            }
                        }
                    } catch (e : Exception){}
                    t.message.delete()
                }
            }

            Tess.api.addMessageCreateListener({ t ->
                val message = t.message
                val server = t.message.channel.asServerTextChannel().get().server
                if (message.channel != null && message.content.contains("new player") && TessUtils.isAdmin(message.author.get(), server)){
                    val s = message.content.substring(message.content.indexOf("new player")).substring(11)
                    PlayerHandler.createPlayer(message, s)
                    t.message.delete()
                }
            })
            Tess.api.addMessageCreateListener(LocationHandler)
            Tess.api.addReactionAddListener(ReactionResponseHandler)
            ReactionHandler.start()
        }


    }


}
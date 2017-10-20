package com.afg.tess.init

import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.ReactionHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.util.ISaveable
import com.afg.tess.util.TessUtils


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
            Tess.api.servers.forEach {
                ISaveable.loadData("tessData/${it.id}", LocationHandler.locations, false)
                it.addServerChannelCreateListener { event ->
                    if (event.channel.asServerTextChannel().isPresent) {
                        val channel = event.channel.asServerTextChannel().get()
                        if (channel.category.isPresent && LocationHandler.locations.any { it.channelID == channel.category.get().id }) {
                            val location = LocationHandler.locations.first { it.channelID == channel.category.get().id }
                            if (!location.nearby.contains(event.channel.name)) {
                                location.nearby.add(event.channel.name)
                                location.saveData()
                            }
                        }
                    }
                }
            }

            Tess.api.addMessageCreateListener { event ->
                val channel = event.message.serverTextChannel.get()
                val server = channel.server
                if (!event.message.author.get().isBot && event.message.content.contains(Tess.api.yourself.mentionTag) || (Tess.api.yourself.getNickname(server).isPresent && event.message.content.contains(Tess.api.yourself.nicknameMentionTag))) {
                    val args = event.message.content.split(" ")
                    try {
                        if (args.size == 1) {
                            ReactionResponseHandler.TravelMessage(channel, event.message.author.get())
                        } else if (TessUtils.isAdmin(event.message.author.get(), server)) {
                            when {
                                args[1].toLowerCase().contains("new") -> {
                                    val s = event.message.content.substring(event.message.content.indexOf(args[2])).substring(3)
                                    val location = LocationHandler.createLocation(s, event.message.serverTextChannel.get().server)
                                    location.unicodeEmoji = args[2]
                                    location.saveData()
                                    try {
                                        event.message.addReaction(location.unicodeEmoji).get()
                                    } catch (e: Exception) {
                                        location.delete(LocationHandler.locations)
                                    }
                                }
                            }
                        }
                    } catch (e: Exception) {
                    }
                    event.message.delete()
                }
            }
            Tess.api.addServerChannelDeleteListener { event ->
                LocationHandler.locations.firstOrNull { event.channel.asChannelCategory().isPresent && it.channelID == event.channel.id }?.delete(LocationHandler.locations)
                val l = LocationHandler.locations.firstOrNull { event.channel.asServerTextChannel().isPresent && event.channel.asServerTextChannel().get().category.isPresent && it.channelID == event.channel.asServerTextChannel().get().category.get().id }
                l?.nearby?.remove(event.channel.name)
                l?.saveData()
            }

            Tess.api.addReactionAddListener(ReactionResponseHandler)
            ReactionHandler.start()
        }


    }


}
package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.util.ISaveable
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.events.message.MessageCreateEvent
import de.btobastian.javacord.listeners.message.MessageCreateListener
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by AFlyingGrayson on 10/7/17
 */
object LocationHandler : MessageCreateListener {

    override fun onMessageCreate(event: MessageCreateEvent) {
        val server = event.message.channel.asServerTextChannel().get().server
        if (!event.message.author.get().isYourself && TessUtils.isPlayerChannel(event.message.channel.id, server) && event.message.content.isNotEmpty() && event.message.content[0] != '!') {
            if(event.message.content.contains(Tess.api.yourself.mentionTag) || (Tess.api.yourself.getNickname(server).isPresent && event.message.content.contains(Tess.api.yourself.nicknameMentionTag))) return
            val player = TessUtils.getPlayer(event.channel.id, server.id)
            val emoji = server.customEmojis.first { it.name == player.rpName.toLowerCase().replace(" ", "") }
            PlayerHandler.players.filter { it.location == player.location }.forEach { p ->
                server.channels.first { it.id == p.channelID }.asServerTextChannel().get().sendMessage("${emoji.mentionTag} ${player.rpName}: ${event.message.content}")
            }
            if(server.channels.any { it.name.toLowerCase().contains("observing")}){
                server.channels.first { it.name.toLowerCase().contains("observing") }.asServerTextChannel().get().sendMessage("${emoji.mentionTag} (${LocationHandler.locations.first { it.uuid == player.location }.name}) ${player.rpName}: ${event.message.content}")
            }
            event.message.delete()
        }
    }

    val locations = ArrayList<Location>()

    fun createLocation(parentLocation: Location, name: String): Location {
        val location = Location()
        location.nearby.add(parentLocation.uuid)
        parentLocation.nearby.add(location.uuid)
        parentLocation.saveData()
        location.name = name
        location.saveData()
        locations.add(location)
        return location
    }

    open class Location : ISaveable {
        var uuid = UUID.randomUUID().toString()
        var unicodeEmoji = ""
        var customEmojiName = ""
        var name = ""
        var nearby = ArrayList<String>()

        val nearbyLocations: ArrayList<Location>
            get() {
                val list = ArrayList<Location>()
                nearby.forEach { try { list.add(locations.first { l -> l.uuid == it }) } catch (e: Exception) { } }
                return list
            }

        override fun getFileName() = uuid
        override fun getFolderPath() = Tess.locationFolderPath!!
    }
}
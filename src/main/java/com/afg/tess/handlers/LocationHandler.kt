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
    override fun onMessageCreate(event : MessageCreateEvent) {
        if (!event.message.author.get().isYourself && TessUtils.isPlayerChannel(event.message.channel.id.toString()) && event.message.content[0] != '!') {
            val player = TessUtils.getPlayer(event.channel.id.toString())
            val server = event.message.channel.asServerTextChannel().get().server
            PlayerHandler.players.filter { it.location == player.location }.forEach { p ->
                val emoji = server.customEmojis.first { it.name == player.rpName.toLowerCase().replace(" ", "-") }
                server.channels.first { it.id.toString() == p.channelID }.asServerTextChannel().get().sendMessage("${emoji.mentionTag} ${player.rpName}: ${event.message.content}")
            }
            event.message.delete()
        }
    }

    val locations = ArrayList<Location>()

    fun createLocation(parentLocation: Location?, name: String): Location {
        val location = Location()
        if (parentLocation != null) {
            location.nearby.add(parentLocation.uuid)
            parentLocation.nearby.add(location.uuid)
            parentLocation.saveData()
        }
        location.name = name
        location.saveData()
        locations.add(location)
        return location
    }

    fun travel(player: PlayerHandler.Player, location: Location) {
        player.location = location.uuid
        player.saveData()
    }

    open class Location : ISaveable {
        var uuid = UUID.randomUUID().toString()
        var unicodeEmoji = ""
        var emojiString = ""
        var name = ""
        var nearby = ArrayList<String>()
        var combatSquares = LinkedList<CombatSquare>()

        init { (0..2).forEach { (0..5).map { if (it > 2) CombatSquare.WHITE else CombatSquare.BLACK }.forEach { combatSquares.add(it) } } }

        var combat = false
        var nextCombatMinute = 0
        var combatSecond = 0
        val nearbyLocations: ArrayList<Location>
            get() {
                val list = ArrayList<Location>()
                nearby.forEach { try { list.add(locations.first { l -> l.uuid == it }) } catch (e: Exception) { } }
                return list
            }

        override fun getFileName() = uuid
        override fun getFolderPath() = Tess.locationFolderPath!!

        enum class CombatSquare {
            WHITE,
            BLACK
        }
    }
}
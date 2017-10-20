package com.afg.tess.handlers

import com.afg.tess.util.getRpName
import com.afg.tess.util.sendEmbedMessage
import com.afg.tess.util.setOverwrittenPermissions
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.ServerTextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import de.btobastian.javacord.entities.permissions.PermissionsBuilder
import de.btobastian.javacord.events.message.reaction.ReactionAddEvent
import de.btobastian.javacord.listeners.message.reaction.ReactionAddListener
import java.awt.Color
import java.util.*

/**
 * Created by AFlyingGrayson on 10/11/17
 */
object ReactionResponseHandler : ReactionAddListener {

    override fun onReactionAdd(event: ReactionAddEvent) {
        reactionMessageList.filter { event.user == it.user && it.messageID == event.message.get().id }.forEach { it.onReaction(event.channel.asServerTextChannel().get().server, event.user, event.reaction.get()) }
    }

    val reactionMessageList = ArrayList<ReactionMessage>()

    abstract class ReactionMessage(message: Message, var user: User, var messageID: Long = message.id) {
        init {
            ReactionResponseHandler.reactionMessageList.add(this)
        }

        abstract fun onReaction(server: Server, user: User, reaction: Reaction)
    }

    class TravelMessage(channel: ServerTextChannel, user: User, message: Message = channel.sendEmbedMessage(Color.GREEN, "") {
        setDescription("Where would you like to travel, ${user.getRpName(channel.server)}?")
        LocationHandler.locations.filter { it.serverID == channel.server.id }.forEach { l ->
            addField(l.unicodeEmoji, l.name, true)
        }
    }) : ReactionMessage(message, user) {
        init {
            LocationHandler.locations.filter { it.serverID == channel.server.id }.forEach { l ->
                message.addReactionToQueue(l.unicodeEmoji)
            }
        }

        override fun onReaction(server: Server, user: User, reaction: Reaction) {
            LocationHandler.locations.filter { it.serverID == reaction.message.serverTextChannel.get().server.id }.forEach { l ->
                if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == l.unicodeEmoji) {
                    var location = server.channelCategories.firstOrNull { it.id == l.channelID }
                    if (location == null) {
                        val categoryBuilder = server.channelCategoryBuilder
                        categoryBuilder.setName(l.name)
                        location = categoryBuilder.create().get()
                        location.setOverwrittenPermissions(PermissionType.READ_MESSAGES, PermissionState.DENIED)
                        l.nearby.forEach {
                            val channelBuilder = server.textChannelBuilder
                            channelBuilder.setCategory(location)
                            channelBuilder.setName(it)
                            channelBuilder.create().get()
                        }
                        l.channelID = location.id
                        l.saveData()
                    }
                    if (location != null) {
                        val builder = PermissionsBuilder(location.getOverwrittenPermissions(user))
                        builder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED)
                        location.setOverwrittenPermissions(user, builder.build())
                        LocationHandler.locations.forEach { l2 ->
                            val location2 = server.channelCategories.firstOrNull { it.id == l2.channelID }
                            if (location2 != null && location2 != location) {
                                val builder2 = PermissionsBuilder(location2.getOverwrittenPermissions(user))
                                builder2.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED)
                                location2.setOverwrittenPermissions(user, builder2.build()).get()
                                if(!server.members.filter { !it.getRoles(server).any { it.permissions.getState(PermissionType.ADMINISTRATOR) == PermissionState.ALLOWED } }
                                        .filter { it != user }
                                        .any { location2.getEffectivePermissions(it).getState(PermissionType.READ_MESSAGES) == PermissionState.ALLOWED }){
                                    l2.channelID = 0
                                    l2.saveData()
                                    location2.channels.forEach { it.delete() }
                                    location2.delete()
                                }
                            }
                        }
                        reaction.message.delete()
                        reactionMessageList.remove(this)
                        return
                    }
                }
            }
        }
    }
}





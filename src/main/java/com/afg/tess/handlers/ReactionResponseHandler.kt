package com.afg.tess.handlers

import com.afg.tess.util.TessUtils
import com.afg.tess.util.sendEmbedMessage
import de.btobastian.javacord.entities.channels.ServerTextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.Reaction
import de.btobastian.javacord.events.message.reaction.ReactionAddEvent
import de.btobastian.javacord.listeners.message.reaction.ReactionAddListener
import java.awt.Color
import java.util.*

/**
 * Created by AFlyingGrayson on 10/11/17
 */
object ReactionResponseHandler : ReactionAddListener {

    override fun onReactionAdd(event : ReactionAddEvent) {
        val server = event.channel.asServerTextChannel().get().server
        if (!event.user.isBot && TessUtils.isPlayerChannel(event.message.get().channel.id, server)) {
            reactionMessageList.filter {it.messageID == event.message.get().id }.forEach { it.onReaction(TessUtils.getPlayer(event.channel.id, server.id), event.reaction.get()) }
        }
    }

    val reactionMessageList = ArrayList<ReactionMessage>()

    abstract class ReactionMessage(message: Message, var messageID: Long = message.id) {
        init { ReactionResponseHandler.reactionMessageList.add(this) }
        abstract fun onReaction(player: PlayerHandler.Player, reaction: Reaction)
    }

    class TravelMessage(channel: ServerTextChannel, var location: LocationHandler.Location,
                        message: Message = channel.sendEmbedMessage(Color.GREEN, "") {
                            setDescription("Where would you like to travel?")
                            location.nearbyLocations.forEach { l ->
                                if(l.customEmojiName != "") addField(channel.server.customEmojis.first { it.isCustomEmoji && it.asCustomEmoji().get().name == l.customEmojiName }.mentionTag, l.name, true)
                                else addField(l.unicodeEmoji, l.name, true)
                            }
                        }) : ReactionMessage(message) {
        init {
            location.nearbyLocations.forEach { l->
                if(l.customEmojiName != "") message.addReactionToQueue(channel.server.customEmojis.first { it.isCustomEmoji && it.asCustomEmoji().get().name == l.customEmojiName })
                else message.addReactionToQueue(l.unicodeEmoji)
            }
        }

        override fun onReaction(player: PlayerHandler.Player, reaction: Reaction) {
            this.location.nearbyLocations.forEach {
                if ((reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == it.unicodeEmoji) || (reaction.emoji.isCustomEmoji && reaction.emoji.asCustomEmoji().get().name == it.customEmojiName)) {
                    player.location = it.uuid
                    player.saveData()
                    reaction.message.channel.sendMessage("You travelled to ${it.name}.")
                    reaction.message.delete()
                    reactionMessageList.remove(this)
                }
            }
        }
    }
}





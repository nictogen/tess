package com.afg.tess.players.items

import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.handlers.addReactionToQueue
import com.afg.tess.util.TessUtils
import com.afg.tess.util.sendEmbedMessage
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.emoji.CustomEmoji
import java.awt.Color

/**
 * Created by AFlyingGrayson on 10/7/17
 */
abstract class Item {

    companion object {
        var itemList = ArrayList<Item>()
    }

    abstract fun use(player: PlayerHandler.Player) : String

    abstract fun getName() : String
    abstract fun getEmojiName() : String

    fun getEmoji() : CustomEmoji = TessUtils.emojiServer.customEmojis.first { it.name == getEmojiName() }

    class ItemStack(var item: Item, var amount : Int)

    class ItemMessage(player: PlayerHandler.Player, location: LocationHandler.Location, var item: Item, message: Message = TessUtils.server.channels.first { it.id.toString() == player.channelID }.asServerTextChannel().get().sendEmbedMessage(Color.RED) {
        setDescription(TessUtils.combatSquaresFromLocation(location) + "\n\nPreparing to use ${item.getName()}.")
        addField(item.getName(), item.getEmoji().mentionTag, true)
    }) : ReactionResponseHandler.CompletionMessage(message, player, location
    ) {
        override fun execute() : ReactionResponseHandler.Recap {
            val desc = item.use(player)
            return ReactionResponseHandler.Recap(desc, item.getName(), item.getEmoji().mentionTag)
        }
        init {
            message.addReactionToQueue("\uD83D\uDD19") //Back arrow
        }
    }
}
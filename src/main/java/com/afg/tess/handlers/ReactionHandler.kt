package com.afg.tess.handlers

import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.emoji.CustomEmoji
import java.util.*

/**
 * Created by AFlyingGrayson on 10/13/17
 */
object ReactionHandler : Thread() {

    override fun run() {
        while (true) {
            Thread.sleep(10)
            emojiQueue.removeAll(emojiQueue.filter { it.message.isDeleted })
            while (emojiQueue.isNotEmpty()) {
                val e = emojiQueue.element()
                val message = e.message.channel.getMessageById(e.message.id).get()
                if (e.customEmoji != null) {
                    message.addReaction(e.customEmoji).get()
                    Thread.sleep(250)
                    if (message.reactions.any { it.emoji.isCustomEmoji && it.emoji.asCustomEmoji() == e.customEmoji }) emojiQueue.remove()
                } else {
                    message.addReaction(e.unicodeEmoji).get()
                    Thread.sleep(250)
                    if (message.reactions.any { it.emoji.isUnicodeEmoji && it.emoji.asUnicodeEmoji().get() == e.unicodeEmoji }) emojiQueue.remove()
                }
            }
        }
    }

    val emojiQueue = LinkedList<EmojiToMessage>() as Queue<EmojiToMessage>

    class EmojiToMessage(var customEmoji: CustomEmoji? = null, var unicodeEmoji: String = "", var message: Message)

    fun addReactionToQueue(emoji: ReactionHandler.EmojiToMessage) {
        ReactionHandler.emojiQueue.add(emoji)
    }
}

fun Message.addReactionToQueue(emoji: String) = ReactionHandler.addReactionToQueue(com.afg.tess.handlers.ReactionHandler.EmojiToMessage(null, emoji, this))
fun Message.addReactionToQueue(emoji: CustomEmoji) = ReactionHandler.addReactionToQueue(com.afg.tess.handlers.ReactionHandler.EmojiToMessage(emoji, "", this))

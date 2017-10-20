package com.afg.tess.handlers

import de.btobastian.javacord.entities.message.Message
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
                val message = e.message.channel.getHistory(10).get().messages.firstOrNull { it.id == e.message.id }
                if (message != null) {
                    message.addReaction(e.unicodeEmoji).get()
                    Thread.sleep(250)
                    if (message.reactions.any { it.emoji.isUnicodeEmoji && it.emoji.asUnicodeEmoji().get() == e.unicodeEmoji }) emojiQueue.remove()
                } else {
                    emojiQueue.remove()
                }
            }
        }
    }

    val emojiQueue = LinkedList<EmojiToMessage>() as Queue<EmojiToMessage>

    class EmojiToMessage(var unicodeEmoji: String, var message: Message)

    fun addReactionToQueue(emoji: ReactionHandler.EmojiToMessage) {
        ReactionHandler.emojiQueue.add(emoji)
    }
}

fun Message.addReactionToQueue(emoji: String) = ReactionHandler.addReactionToQueue(com.afg.tess.handlers.ReactionHandler.EmojiToMessage(emoji, this))

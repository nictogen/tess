package com.afg.tess.handlers

import com.afg.tess.players.skills.CombatSkill
import com.afg.tess.players.skills.Skill
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
        if ((event.user == null || !event.user.isYourself) && TessUtils.isPlayerChannel(event.message.get().channel.id.toString()) && reactionMessageList.any { it.messageID == event.message.get().id.toString() }) {
            reactionMessageList.filter {it.messageID == event.message.get().id.toString() }.forEach {
                it.onReaction(TessUtils.getPlayer(event.channel.id.toString()), event.reaction.get())
            }
        }
    }

    val reactionMessageList = ArrayList<ReactionMessage>()

    abstract class ReactionMessage(message: Message, var messageID: String = message.id.toString()) {
        init {
            ReactionResponseHandler.reactionMessageList.add(this)
        }

        abstract fun onReaction(player: PlayerHandler.Player, reaction: Reaction)
    }

    class TravelMessage(channel: ServerTextChannel, var location: LocationHandler.Location,
                        message: Message = channel.sendEmbedMessage(Color.GREEN, "") {
                            setDescription("Where would you like to travel?")
                            location.nearbyLocations.forEach { addField(it.emojiString, it.name, true) }
                        }) : ReactionMessage(message) {
        init {
            location.nearbyLocations.forEach {
                message.addReactionToQueue(it.unicodeEmoji)
            }
        }

        override fun onReaction(player: PlayerHandler.Player, reaction: Reaction) {
            this.location.nearbyLocations.forEach {
                if ((reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == it.unicodeEmoji) || (reaction.emoji.isCustomEmoji && reaction.emoji.asCustomEmoji().get().mentionTag == it.emojiString)) {
                    player.location = it.uuid
                    player.saveData()
                    reaction.message.channel.sendMessage("You travelled to ${it.name}.")
                    reaction.message.delete()
                    reactionMessageList.remove(this)
                }
            }
        }
    }

    abstract class CombatMenuMessage(message: Message, var player: PlayerHandler.Player, var location: LocationHandler.Location) : ReactionMessage(message) {
        override fun onReaction(player: PlayerHandler.Player, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "\uD83D\uDD19") removeFromList(reaction.message)
        }

        fun removeFromList(message: Message) {
            reactionMessageList.remove(this)
            message.delete()
        }
    }

    abstract class CompletionMessage(message: Message, player: PlayerHandler.Player, location: LocationHandler.Location) : CombatMenuMessage(message, player, location) {
        abstract fun execute(): Recap
    }

    class RecapMessage(player: PlayerHandler.Player, location: LocationHandler.Location, var recaps: LinkedList<Recap>, message: Message = TessUtils.server.channels.first { it is ServerTextChannel && it.id.toString() == player.channelID }.asServerTextChannel().get().sendEmbedMessage(Color.RED, "") {
        setDescription(recaps[0].description)
        addField(recaps[0].name, recaps[0].emoji, true)
    }) : CombatMenuMessage(message, player, location) {
        init {
            message.addReactionToQueue("⏩")
        }

        override fun onReaction(player: PlayerHandler.Player, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    "⏩" -> { //Forward Arrow
                        val s = LinkedList<Recap>()
                        s.addAll(recaps.subList(1, recaps.size))
                        if (s.isNotEmpty())
                            RecapMessage(this.player, this.location, s)
                        removeFromList(reaction.message)
                    }
                }
            }
        }
    }

    class Recap(var description: String, var name: String, var emoji: String)

    class MainMenuMessage(player: PlayerHandler.Player, location: LocationHandler.Location, message: Message = TessUtils.server.channels.first { it.id.toString() == player.channelID }.asServerTextChannel().get().sendEmbedMessage(Color.RED, "") {
        setDescription(TessUtils.combatSquaresFromLocation(location))
        addField("Skills", "⚔", true)
        addField("Items", "\uD83D\uDCBC", true)
        addField("Move", "\uD83D\uDC5F", true)
    }) : CombatMenuMessage(message, player, location) {
        init {
            if (player.recovering) {
                RecoveryMessage(this.player, this.location)
                removeFromList(message)
            } else {
                message.addReactionToQueue("⚔")
                message.addReactionToQueue("\uD83D\uDCBC")
                message.addReactionToQueue("\uD83D\uDC5F")
            }
        }

        override fun onReaction(player: PlayerHandler.Player, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    "⚔" -> {
                        SkillMenuMessage(this.player, this.location)
                        removeFromList(reaction.message)
                    }
                    "\uD83D\uDCBC" -> { //Briefcase
                    }
                    "\uD83D\uDC5F" -> { //Shoe
                        MovementMenuMessage(this.player, this.location)
                        removeFromList(reaction.message)
                    }
                }
            }
        }
    }

    class MovementMenuMessage(player: PlayerHandler.Player, location: LocationHandler.Location, message: Message = TessUtils.server.channels.first { it.id.toString() == player.channelID }.asServerTextChannel().get().sendEmbedMessage(Color.RED) {
        setDescription(TessUtils.combatSquaresFromLocation(location))
        addField("Moving", "\uD83D\uDC5F", true)
    }) : CombatMenuMessage(message, player, location) {
        init {
            message.addReactionToQueue("\uD83D\uDD19") //Back arrow
            message.addReactionToQueue("\uD83C\uDDF3") //North
            message.addReactionToQueue("\uD83C\uDDEA") //East
            message.addReactionToQueue("\uD83C\uDDF8") //South
            message.addReactionToQueue("\uD83C\uDDFC") //West
            message.addReactionToQueue("✅") //Checkmark
        }

        override fun onReaction(player: PlayerHandler.Player, reaction: Reaction) {
            super.onReaction(player, reaction)
            if (reaction.emoji.isUnicodeEmoji) {
                when (reaction.emoji.asUnicodeEmoji().get()) {
                    "✅" -> { //Checkmark
                        val r = reaction.message.reactions.filter { it.emoji.isUnicodeEmoji && it.count > 1 }.map { it.emoji.asUnicodeEmoji().get() }
                        var x = 0
                        var y = 0
                        r.forEach {
                            when (it) {
                                "\uD83C\uDDF3" -> y-- //N
                                "\uD83C\uDDF8" -> y++ //S
                                "\uD83C\uDDEA" -> x++ //E
                                "\uD83C\uDDFC" -> x-- //W
                            }
                        }
                        x += player.combatLocationX
                        y += player.combatLocationY
                        val team = if (player.combatLocationX < 3) LocationHandler.Location.CombatSquare.BLACK else LocationHandler.Location.CombatSquare.WHITE
                        if ((x >= 3 && team == LocationHandler.Location.CombatSquare.BLACK) || (x < 3 && team == LocationHandler.Location.CombatSquare.WHITE)) x = player.combatLocationX //Check if out of bounds
                        MovementCompletionMessage(player, location, TessUtils.Target(x, y))
                        removeFromList(reaction.message)
                    }
                }
            }
        }
    }

    class MovementCompletionMessage(player: PlayerHandler.Player, location: LocationHandler.Location, var target: TessUtils.Target, message: Message = TessUtils.server.channels.first { it.id.toString() == player.channelID }.asServerTextChannel().get().sendEmbedMessage(Color.RED) {
        setDescription("Field: \n${TessUtils.combatSquaresFromLocation(location)}" +
                "\n\nMoving To: \n${TessUtils.combatSquaresFromLocation(location, arrayListOf(target))}")
        addField("Moving", "\uD83D\uDC5F", true)
    }) : CompletionMessage(message, player, location
    ) {
        override fun execute(): Recap {
            val pc = TessUtils.getPlayersInCombat(location)
            val desc = if (pc.any { it.combatLocationX == target.x && it.combatLocationY == target.y }) "${player.rpName} tried to move, but someone was already there."
            else {
                player.combatLocationY = target.y
                player.combatLocationX = target.x
                player.saveData()
                "${player.rpName} moved."
            }
            return Recap("${TessUtils.combatSquaresFromLocation(location)}\n\n$desc", "Movement", "\uD83D\uDC5F")
        }

        init {
            message.addReactionToQueue("\uD83D\uDD19") //Back arrow
        }
    }

    class RecoveryMessage(player: PlayerHandler.Player, location: LocationHandler.Location, message: Message = TessUtils.server.channels.first { it.id.toString() == player.channelID }.asServerTextChannel().get().sendEmbedMessage(Color.RED) {
        setDescription("Field: \n${TessUtils.combatSquaresFromLocation(location)}")
        addField("Recovering", "\uD83D\uDE34", true)
    }) : CompletionMessage(message, player, location
    ) {
        override fun execute(): Recap {
            player.recovering = false
            return Recap("${TessUtils.combatSquaresFromLocation(location)}\n\n${player.rpName} recovered from their last move.", "Recover", "\uD83D\uDE34")
        }
    }

    class SkillMenuMessage(player: PlayerHandler.Player, location: LocationHandler.Location, message: Message = TessUtils.server.channels.first { it.id.toString() == player.channelID }.asServerTextChannel().get().sendEmbedMessage(Color.RED) {
        setDescription(TessUtils.combatSquaresFromLocation(location) + "\n\n Select Skill:")
        player.skills.map { Skill.getSkill(it) }.filter { it is CombatSkill }.forEach {
            addField(it.getName(), it.getEmoji().mentionTag, true)
        }
    }) : CombatMenuMessage(message, player, location) {
        init {
            message.addReactionToQueue("\uD83D\uDD19") //Back arrow
            player.skills.map { Skill.getSkill(it) }.filter { it is CombatSkill }.forEach {
                message.addReactionToQueue(it.getEmoji())
            }
        }

        override fun onReaction(player: PlayerHandler.Player, reaction: Reaction) {
            super.onReaction(player, reaction)
            player.skills.map { Skill.getSkill(it) }.filter { it is CombatSkill }.forEach {
                if (reaction.emoji.isCustomEmoji && reaction.emoji.asCustomEmoji().get().name == it.getEmoji().name) {
                    (it as CombatSkill).getMessageResponse(player, location)
                    removeFromList(reaction.message)
                }
            }
        }
    }
}





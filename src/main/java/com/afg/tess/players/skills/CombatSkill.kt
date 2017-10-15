package com.afg.tess.players.skills

import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.handlers.addReactionToQueue
import com.afg.tess.util.TessUtils
import com.afg.tess.util.sendEmbedMessage
import de.btobastian.javacord.entities.message.Message
import java.awt.Color

/**
 * Created by AFlyingGrayson on 10/13/17
 */
abstract class CombatSkill : Skill() {

    abstract fun getMessageResponse(player: PlayerHandler.Player, location: LocationHandler.Location) : ReactionResponseHandler.CombatMenuMessage

    abstract fun execute(player: PlayerHandler.Player, location: LocationHandler.Location) : ReactionResponseHandler.Recap

    open class SkillMessage(player: PlayerHandler.Player, location: LocationHandler.Location, var skill: CombatSkill, description : String, message: Message = TessUtils.server.channels.first { it.id.toString() == player.channelID }.asServerTextChannel().get().sendEmbedMessage(Color.RED) {
        setDescription(description)
        addField(skill.getName(), skill.getEmoji().mentionTag, true)
    }) : ReactionResponseHandler.CompletionMessage(message, player, location
    ) {
        override fun execute() = skill.execute(player, location)
        init {
            message.addReactionToQueue("\uD83D\uDD19") //Back arrow
        }
    }
}
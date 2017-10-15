package com.afg.tess.players.skills.fighter

import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.players.skills.CombatSkill
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.entities.message.Reaction

/**
 * Created by AFlyingGrayson on 10/13/17
 */
object Block : CombatSkill() {

    override fun execute(player: PlayerHandler.Player, location: LocationHandler.Location): ReactionResponseHandler.Recap {
        return ReactionResponseHandler.Recap("${player.rpName} blocked with their shield.", getName(), getEmoji().mentionTag)
    }

    override fun getMessageResponse(player: PlayerHandler.Player, location: LocationHandler.Location): ReactionResponseHandler.CombatMenuMessage {
        player.blocking = true //TODO block handling
        return BlockMessage(player, location, this, "Field: \n${TessUtils.combatSquaresFromLocation(location)}" +
                "Preparing to block with a shield.")
    }

    override fun getID() = "823f1497-8ed3-43c8-aaf2-dc701241bb9f"
    override fun getName() = "Block"
    override fun getEmojiName() = "block"

    class BlockMessage(player: PlayerHandler.Player, location: LocationHandler.Location, skill: CombatSkill, description : String): SkillMessage(player, location, skill, description) {
        override fun onReaction(player: PlayerHandler.Player, reaction: Reaction) {
            if (reaction.emoji.isUnicodeEmoji && reaction.emoji.asUnicodeEmoji().get() == "\uD83D\uDD19"){
                player.blocking = false
                removeFromList(reaction.message)
            }
        }
    }
}
package com.afg.tess.players.skills.fighter

import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.players.skills.CombatSkill
import com.afg.tess.util.TessUtils

/**
 * Created by AFlyingGrayson on 10/13/17
 */
object Strike : CombatSkill() {

    override fun getMessageResponse(player: PlayerHandler.Player, location: LocationHandler.Location): ReactionResponseHandler.CombatMenuMessage {
        val target = if(TessUtils.getTeam(player) == LocationHandler.Location.CombatSquare.BLACK) TessUtils.Target(player.combatLocationX + 1, player.combatLocationY)
        else TessUtils.Target(player.combatLocationX - 1, player.combatLocationY)
        return SkillMessage(player, location, this, "Field: \n${TessUtils.combatSquaresFromLocation(location)}" +
                "Attacking: \n${TessUtils.combatSquaresFromLocation(location, arrayListOf(target))}")
    }

    override fun execute(player: PlayerHandler.Player, location: LocationHandler.Location): ReactionResponseHandler.Recap {
        var description = "${player.rpName} used Strike.\n"
        val target = if(TessUtils.getTeam(player) == LocationHandler.Location.CombatSquare.BLACK) TessUtils.Target(player.combatLocationX + 1, player.combatLocationY)
        else TessUtils.Target(player.combatLocationX - 1, player.combatLocationY)
        description += if(TessUtils.getPlayersInCombat(location).any { it.combatLocationX == target.x && it.combatLocationY == target.y}){
            player.attack(TessUtils.getPlayersInCombat(location).first { it.combatLocationX == target.x && it.combatLocationY == target.y}, 50.0, 80)
        } else "But no one was there to hit."
        return ReactionResponseHandler.Recap(description, getName(), getEmoji().mentionTag)
    }

    override fun getID() = "b3dbdffe-be53-4e17-a038-3cfafb15b59b"

    override fun getName() = "Strike"

    override fun getEmojiName() = "strike"
}
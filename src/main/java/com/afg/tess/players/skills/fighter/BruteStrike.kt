package com.afg.tess.players.skills.fighter

import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.players.skills.CombatSkill
import com.afg.tess.util.TessUtils

/**
 * Created by AFlyingGrayson on 10/13/17
 */
object BruteStrike : CombatSkill() {

    override fun getMessageResponse(player: PlayerHandler.Player, location: LocationHandler.Location): ReactionResponseHandler.CombatMenuMessage {
        val target = if(TessUtils.getTeam(player) == LocationHandler.Location.CombatSquare.BLACK) TessUtils.Target(player.combatLocationX + 1, player.combatLocationY)
        else TessUtils.Target(player.combatLocationX - 1, player.combatLocationY)
        return SkillMessage(player, location, this, "Field: \n${TessUtils.combatSquaresFromLocation(location)}" +
                "Attacking: \n${TessUtils.combatSquaresFromLocation(location, arrayListOf(target))}")
    }

    override fun execute(player: PlayerHandler.Player, location: LocationHandler.Location): ReactionResponseHandler.Recap {
        var description = "${player.rpName} used Brute Strike.\n"
        val target = if(TessUtils.getTeam(player) == LocationHandler.Location.CombatSquare.BLACK) TessUtils.Target(player.combatLocationX + 1, player.combatLocationY)
        else TessUtils.Target(player.combatLocationX - 1, player.combatLocationY)
        description += if(TessUtils.getPlayersInCombat(location).any { it.combatLocationX == target.x && it.combatLocationY == target.y}){
            player.attack(TessUtils.getPlayersInCombat(location).first { it.combatLocationX == target.x && it.combatLocationY == target.y}, 125.0, 90)
        } else "But no one was there to hit."
        description += "\nNext turn, ${player.rpName} will recover from their Brute Strike."
        player.recovering = true
        return ReactionResponseHandler.Recap(description, getName(), getEmoji().mentionTag)
    }

    override fun getID() = "0bfa28ee-42a8-4b33-9639-25ebe363c55a"
    override fun getName() = "Brute Strike"
    override fun getEmojiName() = "brutestrike"
}
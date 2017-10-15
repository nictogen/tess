package com.afg.tess.players.skills.fighter

import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.players.skills.CombatSkill

/**
 * Created by AFlyingGrayson on 10/13/17
 */
object Cleave : CombatSkill() {
    override fun getMessageResponse(player: PlayerHandler.Player, location: LocationHandler.Location): ReactionResponseHandler.CombatMenuMessage {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun execute(player: PlayerHandler.Player, location: LocationHandler.Location): ReactionResponseHandler.Recap {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getID() = "6b185885-424a-43dd-bcf0-ec385f96371d"
    override fun getName() = "Cleave"
    override fun getEmojiName() = "\uD83C\uDF19"

}
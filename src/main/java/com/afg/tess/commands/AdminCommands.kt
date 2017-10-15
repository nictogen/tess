package com.afg.tess.commands

import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.util.TessUtils

/**
 * Created by AFlyingGrayson on 10/7/17
 */
object AdminCommands {

    @Command(aliases = arrayOf("!cb"))
    fun onCreateBase(info: CommandHandler.MessageInfo, name: String, emoji: String, unicodeEmoji: String): String {
        return if (TessUtils.isAdmin(info.user)) {
            val loc = LocationHandler.createLocation(null, name)
            loc.emojiString = emoji.replace("`", "")
            loc.unicodeEmoji = unicodeEmoji.replace("\"", "")
            LocationHandler.travel(info.player, loc)
            loc.saveData()
            "Created location $name"
        } else "You aren't an admin."
    }

    @Command(aliases = arrayOf("!cl"))
    fun onCreateLocation(info: CommandHandler.MessageInfo, name: String, emoji: String, unicodeEmoji: String): String {
        return if (TessUtils.isAdmin(info.user)) {
            val parent = LocationHandler.locations.first { info.player.location == it.uuid }
            val location = LocationHandler.createLocation(parent, name)
            location.emojiString = emoji.replace("`", "")
            location.unicodeEmoji = unicodeEmoji.replace("\"", "")
            location.saveData()
            "Created location $name"
        } else "You aren't an admin."
    }

    @Command(aliases = arrayOf("!travel", "!t"))
    fun onTravel(info: CommandHandler.MessageInfo) {
        val location = LocationHandler.locations.first { it.uuid == info.player.location }
        if(location.combat) return
        ReactionResponseHandler.TravelMessage(info.message.channel.asServerTextChannel().get(), location)
    }
//
//    @Command(aliases = arrayOf("!combat", "!c"))
//    fun onCombat(info: CommandHandler.MessageInfo) {
//        val location = LocationHandler.locations.first { it.uuid == info.player.location }
//        if(location.combat) return
//        else location.combat = true
//        PlayerHandler.players.filter { it.location == location.uuid }.forEach { p ->
//            val pc = TessUtils.getPlayersInCombat(location)
//            val team = when {
//                pc.any { it.party == p.party} -> if(pc.first { it.party ==  p.party }.combatLocationX < 3) LocationHandler.Location.CombatSquare.BLACK else LocationHandler.Location.CombatSquare.WHITE
//                TessUtils.amountOfPartiesInCombat(location) == 0 -> LocationHandler.Location.CombatSquare.WHITE
//                TessUtils.amountOfPartiesInCombat(location) == 1 -> LocationHandler.Location.CombatSquare.BLACK
//                else -> return
//            }
//            val squares = arrayListOf(1, 2, 3, 4, 5, 6, 7, 8, 9)
//            Collections.shuffle(squares)
//            squares.forEach {
//                if(p.combatLocationX == -1 && p.combatLocationY == -1) {
//                    val y = it / 3
//                    val x = (it - y * 3) + if (team == LocationHandler.Location.CombatSquare.BLACK) 0 else 3
//                    if (!pc.any { it.combatLocationY == y && it.combatLocationX == x }) {
//                        p.combatLocationX = x
//                        p.combatLocationY = y
//                        p.saveData()
//                    }
//                }
//            }
//        }
//        location.saveData()
//    }
}
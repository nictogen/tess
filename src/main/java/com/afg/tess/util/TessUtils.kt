package com.afg.tess.util

import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.init.Tess
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.ServerTextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.embed.EmbedBuilder
import java.awt.Color
import java.util.*

/**
 * Created by AFlyingGrayson on 9/3/17
 */
object TessUtils {
    val server get() = Tess.api.servers.first { it.id == 366380096338264065 }
    val emojiServer get() = Tess.api.servers.first { it.id == 368480250608222208 }
    private val admins = arrayListOf("161882538514579466", //Grayson
            "332739616505462784", //tess
            "150541854029381632", //Sheriff
            "358972025433489419") //Orion

    fun getKey(s: String) = s.substring(0, s.indexOf('='))
    fun getValue(s: String) = s.substring(s.indexOf('=') + 1)
    fun isPlayerChannel(channelID: String) = PlayerHandler.players.any { channelID.contains(it.channelID) }
    fun getPlayer(channelID: String) = PlayerHandler.players.first { channelID.contains(it.channelID) }
    fun isAdmin(user: User) = admins.contains(user.id.toString())
    fun getTeam(player: PlayerHandler.Player) = if (player.combatLocationX < 3) LocationHandler.Location.CombatSquare.BLACK else LocationHandler.Location.CombatSquare.WHITE

    fun listFromString(string: String): List<String> {
        var s = string
        s = s.replace("\\[\\],", "")
        s = s.replace(",", "")
        s = s.replace("[", "")
        s = s.replace("]", "")
        return s.split(" ")
    }

    fun combatSquaresFromLocation(location: LocationHandler.Location): String {
        var s = ""
        var x = 0
        var y = 0
        location.combatSquares.forEach {
            s += when {
                PlayerHandler.players.filter { it.location == location.uuid }.any { it.combatLocationX == x && it.combatLocationY == y } -> server.customEmojis.first { it.name == PlayerHandler.players.filter { it.location == location.uuid }.first { it.combatLocationX == x && it.combatLocationY == y }.rpName.toLowerCase().replace(" ", "-") }.mentionTag
                it == LocationHandler.Location.CombatSquare.BLACK -> "⬛"
                else -> "⬜"
            }
            x++
            if (x > 5) {
                x = 0
                y++
                s += "\n\n"
            } else s += "    "
        }
        return s
    }

    fun combatSquaresFromLocation(location: LocationHandler.Location, targets: ArrayList<Target>): String {
        var s = ""
        var x = 0
        var y = 0
        location.combatSquares.forEach {
            s += when {
                targets.any { it.x == x && it.y == y } -> server.customEmojis.first { it.name == "redlargesquare" }.mentionTag
                it == LocationHandler.Location.CombatSquare.BLACK -> "⬛"
                else -> "⬜"
            }
            x++
            if (x > 5) {
                x = 0
                y++
                s += "\n\n"
            } else s += "    "
        }
        return s
    }

    class Target(var x: Int, var y: Int){
        init {
            if (x < 0) x = 0
            if( x > 5) x = 5
            if (y > 2) y = 2
            if(y < 0) y = 0
        }
    }

    fun getPlayersInCombat(location: LocationHandler.Location): ArrayList<PlayerHandler.Player> {
        val list = ArrayList<PlayerHandler.Player>()
        list.addAll(PlayerHandler.players.filter { it.location == location.uuid && it.combatLocationX != -1 && it.combatLocationY != -1 })
        return list
    }

    fun amountOfPartiesInCombat(location: LocationHandler.Location): Int {
        val parties = ArrayList<String>()
        PlayerHandler.players.filter { it.location == location.uuid && it.combatLocationX != -1 && it.combatLocationY != -1 }.forEach { p ->
            if (!parties.any { it == p.party }) parties.add(p.party)
        }
        return parties.size
    }

//    fun numberToEmoji(int : Int) : String{
//        val numbers = arrayOf("1️⃣", "2️⃣", "3️⃣", "4️⃣", "5️⃣", "6️⃣", "7️⃣", "8️⃣", "9️⃣")
//    }
}

inline fun ServerTextChannel.sendEmbedMessage(color: Color, content: String = "", func: EmbedBuilder.() -> Unit): Message = this.sendMessage(content, EmbedBuilder().setColor(color).apply(func)).get()
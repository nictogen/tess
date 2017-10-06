package com.afg.tess.util

import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.init.Tess
import de.btobastian.javacord.entities.User

/**
 * Created by AFlyingGrayson on 9/3/17
 */
object TessUtils {

    val server get() = Tess.api.servers.elementAt(0)!!
    private val admins = arrayListOf("161882538514579466", //Grayson
            "332739616505462784", //tess
            "150541854029381632", //Sheriff
            "358972025433489419", //Orion
            "187376695781752833", //Miss H
            "313166800479322123", //Latent
            "136936819060244480") //Havok

    fun getKey(s: String) = s.substring(0, s.indexOf('='))
    fun getValue(s: String) = s.substring(s.indexOf('=') + 1)
    fun getPlayer(playerID: String) = PlayerHandler.players.filter { playerID.contains(it.playerID)}[0]
    fun isAdmin(user: User) = admins.contains(user.id)
    fun getMember(player : PlayerHandler.Player) = server.members!!.first { player.playerID.contains(it.id) }!!

    fun listFromString(string: String) : List<String> {
        var s = string
        s = s.replace("\\[\\],", "")
        s = s.replace(",", "")
        s = s.replace("[", "")
        s = s.replace("]", "")
        return s.split(" ")
    }

    fun skillFromMagicType(type : PlayerHandler.MagicType, player: PlayerHandler.Player) : PlayerHandler.Skill{
        return when(type){
            PlayerHandler.MagicType.CONSTRUCTION -> player.skills.first { it.name == "Construction" }
            PlayerHandler.MagicType.PROJECTION -> player.skills.first { it.name == "Projection" }
            PlayerHandler.MagicType.CONTROL -> player.skills.first { it.name == "Control" }
            PlayerHandler.MagicType.ALCHEMY -> player.skills.first { it.name == "Alchemy" }
            PlayerHandler.MagicType.HEALING -> player.skills.first { it.name == "Healing" }
            PlayerHandler.MagicType.RUNE_MAGIC -> player.skills.first { it.name == "Rune_Magic" }
            PlayerHandler.MagicType.SUMMONING -> player.skills.first { it.name == "Summoning" }
            PlayerHandler.MagicType.TRANSFORMATION -> player.skills.first { it.name == "Transformation" }
            PlayerHandler.MagicType.MANA_EFFICIENCY -> player.skills.first { it.name == "Mana_Efficiency" }
        }
    }
}

val User.rpName : String
    get() = if (TessUtils.server.getNickname(this) != null) TessUtils.server.getNickname(this) else this.name

val PlayerHandler.Player.rpName : String
    get() = TessUtils.getMember(this).rpName

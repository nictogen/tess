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
            "358972025433489419") //Orion

    fun getKey(s: String) = s.substring(0, s.indexOf('='))
    fun getValue(s: String) = s.substring(s.indexOf('=') + 1)
    fun getPlayer(playerID: String) = PlayerHandler.players.first {playerID.contains(it.playerID)}
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
}

val User.rpName : String
    get() = if (TessUtils.server.getNickname(this) != null) TessUtils.server.getNickname(this) else this.name
val PlayerHandler.Player.rpName : String
    get() = if(TessUtils.server.members!!.any { playerID.contains(it.id) }) TessUtils.getMember(this).rpName else playerID

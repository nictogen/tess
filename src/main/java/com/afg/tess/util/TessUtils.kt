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
            "187376695781752833", //Miss H
            "313166800479322123", //Latent
            "136936819060244480") //Havok

    fun getKey(s: String) = s.substring(0, s.indexOf('='))
    fun getValue(s: String) = s.substring(s.indexOf('=') + 1)
    fun getPlayer(playerID: String) = PlayerHandler.players.filter { playerID.contains(it.playerID)}[0]
    fun isAdmin(user: User) = admins.contains(user.id)
    fun getMember(player : PlayerHandler.Player) = server.members!!.first { player.playerID.contains(it.id) }!!
}

val User.rpName : String
    get() = if (TessUtils.server.getNickname(this) != null) TessUtils.server.getNickname(this) else this.name

val PlayerHandler.Player.rpName : String
    get() = TessUtils.getMember(this).rpName

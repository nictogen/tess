package com.afg.tess.util

import com.afg.tess.handlers.PlayerHandler
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.ServerTextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.embed.EmbedBuilder
import de.btobastian.javacord.entities.permissions.PermissionType
import java.awt.Color

/**
 * Created by AFlyingGrayson on 9/3/17
 */
object TessUtils {
    fun getKey(s: String) = s.substring(0, s.indexOf('='))
    fun getValue(s: String) = s.substring(s.indexOf('=') + 1)
    fun isPlayerChannel(channelID: Long, server: Server) = PlayerHandler.players.any { channelID == it.channelID && server.id == it.serverID }
    fun getPlayer(channelID: Long, serverID: Long) = PlayerHandler.players.first { channelID == it.channelID && serverID == it.serverID }
    fun isAdmin(user: User, server: Server) = user.getRoles(server).any { it.allowedPermissions.contains(PermissionType.MANAGE_CHANNELS) }

    fun listFromString(string: String): List<String> {
        var s = string
        s = s.replace("\\[\\],", "")
        s = s.replace(",", "")
        s = s.replace("[", "")
        s = s.replace("]", "")
        return s.split(" ")
    }
}

inline fun ServerTextChannel.sendEmbedMessage(color: Color, content: String = "", func: EmbedBuilder.() -> Unit): Message = this.sendMessage(content, EmbedBuilder().setColor(color).apply(func)).get()
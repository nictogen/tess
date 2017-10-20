package com.afg.tess.util

import com.afg.tess.init.Tess
import com.mashape.unirest.http.HttpMethod
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.channels.ChannelCategory
import de.btobastian.javacord.entities.channels.ServerTextChannel
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.message.embed.EmbedBuilder
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import de.btobastian.javacord.entities.permissions.Permissions
import de.btobastian.javacord.entities.permissions.PermissionsBuilder
import de.btobastian.javacord.entities.permissions.impl.ImplPermissions
import de.btobastian.javacord.utils.rest.RestEndpoint
import de.btobastian.javacord.utils.rest.RestRequest
import org.json.JSONObject
import java.awt.Color
import java.util.concurrent.CompletableFuture



/**
 * Created by AFlyingGrayson on 9/3/17
 */
object TessUtils {
    fun getKey(s: String) = s.substring(0, s.indexOf('='))
    fun getValue(s: String) = s.substring(s.indexOf('=') + 1)
    fun isAdmin(user: User, server: Server) = user.getRoles(server).any { it.allowedPermissions.contains(PermissionType.MANAGE_CHANNELS) }

    fun listFromString(string: String): List<String> {
        var s = string
        s = s.replace("[", "")
        s = s.replace("]", "")
        var list = s.split(",")
        list = list.map { if(it.isNotEmpty() && it[0] == ' ') it.substring(1, it.length) else it }
        list = list.map { it.replace(",", "") }
        return list
    }

}

fun User.getRpName(server: Server) : String{
    return if(this.getNickname(server).isPresent) this.getNickname(server).get()
    else this.name
}

inline fun ServerTextChannel.sendEmbedMessage(color: Color, content: String = "", func: EmbedBuilder.() -> Unit): Message = this.sendMessage(content, EmbedBuilder().setColor(color).apply(func)).get()

fun ChannelCategory.setOverwrittenPermissions(user: User, perms: Permissions): CompletableFuture<Void> {
    val body = JSONObject()
    body.put("type", "member")
    body.put("id", user.id)
    body.put("allow", (perms as ImplPermissions).allowed)
    body.put("deny", perms.denied)
    return RestRequest<Void>(Tess.api, HttpMethod.PUT, RestEndpoint.CHANNEL)
            .setUrlParameters("$id/permissions/${user.id}").setBody(body).execute { null }
}

fun ChannelCategory.setOverwrittenPermissions(permissionType: PermissionType, state : PermissionState): CompletableFuture<Void> {
    val body = JSONObject()
    val everyone = this.server.roles.first { it.name == "@everyone" }
    val builder = PermissionsBuilder(this.getOverwrittenPermissions(everyone))
    builder.setState(permissionType, state)
    val perms = builder.build() as ImplPermissions
    body.put("type", "role")
    body.put("id", everyone.id)
    body.put("allow", perms.allowed)
    body.put("deny", perms.denied)
    return RestRequest<Void>(Tess.api, HttpMethod.PUT, RestEndpoint.CHANNEL)
            .setUrlParameters("$id/permissions/${everyone.id}").setBody(body).execute { null }
}
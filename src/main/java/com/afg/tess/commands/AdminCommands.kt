package com.afg.tess.commands

import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.util.TessUtils


/**
 * Created by AFlyingGrayson on 9/5/17
 */
object AdminCommands {

    val admins = ArrayList<PlayerHandler.Player>()

    @Command(aliases = arrayOf("!adminmode", "!a"))
    fun onAdminMode(info: CommandHandler.MessageInfo) {
        if (TessUtils.isAdmin(info.user)) {
            if (!admins.contains(info.player)) {
                admins.add(info.player)
                LocationHandler.unlockAllChannels(info.player, info.user)
            } else {
                admins.remove(info.player)
                LocationHandler.lockAllOtherChannels(info.player, info.user)
            }
        }
    }

    @Command(aliases = arrayOf("!rl"))
    fun onReloadLocations(info: CommandHandler.MessageInfo): String {
        return if (TessUtils.isAdmin(info.user)) {
            LocationHandler.loadLocations()
            "Reloaded Locations"
        } else "You are not an admin."
    }
}
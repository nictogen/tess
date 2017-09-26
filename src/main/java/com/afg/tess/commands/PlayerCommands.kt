package com.afg.tess.commands

import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.LocationHandler
import com.afg.tess.util.TessUtils
import com.afg.tess.util.rpName

/**
 * Created by AFlyingGrayson on 9/5/17
 */
object PlayerCommands {

    @Command(aliases = arrayOf("!travel", "!t"))
    fun onTravel(info: CommandHandler.MessageInfo, location: String) {
        if (location.length > 1) {
            if (TessUtils.isAdmin(info.user))
                LocationHandler.travelToLocationAnywhere(info.user, info.player, location)
            else
                LocationHandler.travelToLocation(info.player, location, info.message)
        } else
            LocationHandler.travelToLocation(info.player, Integer.parseInt(location), info.message)
    }

    @Command(aliases = arrayOf("!playerinfo", "!p"))
    fun onPlayerInfo(info: CommandHandler.MessageInfo): String {

        if (!info.message.isPrivateMessage && (info.message.channelReceiver != null && !info.message.channelReceiver.name.contains("spam")))
            return ""

        var string = "#${info.player.rpName} Player Info:\n"

        return "```md\n$string```"
    }
}

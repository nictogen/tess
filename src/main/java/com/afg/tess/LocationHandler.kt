package com.afg.tess

import com.afg.tess.commands.AdminCommands
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageAuthor
import org.javacord.api.entity.permission.PermissionState
import org.javacord.api.entity.permission.PermissionType
import org.javacord.api.entity.permission.PermissionsBuilder
import org.javacord.api.entity.user.User

/**
 * Created by AFlyingGrayson on 9/9/17
 */
object LocationHandler {

    val locationList = ArrayList<Location>()

    fun loadLocations() {
        locationList.clear()
        TessUtils.getServer().channels?.forEach {
            if (it is ServerTextChannel && it.topic != null) {
                if (it.topic.toLowerCase().contains("nearby")) {
                    locationList.add(Location(it))
                }
            }
        }

        locationList.forEach {
            it.nearbyLocations.clear()
            val info = it.channel.asServerTextChannel().get().topic.split(" ")
            info.forEach { i ->
                val channel = getLocationFromName(i)
                if (channel != null) it.nearbyLocations.add(channel)
                if(i.contains("$")){
                    try {
                        it.quickTravelCost = Integer.parseInt(i.substring(1, i.length))
                    } catch (e : Exception){}
                }
                if(i.contains("#")) it.combatZone = true
                if(i.contains("<")) it.bar = true
                if(i.contains("!")) it.market = true
            }
        }
    }

    fun travelToLocation(player: PlayerData.Player, nameOfLocation: String, message: Message) {
        val playerLocation = getLocationFromName(player.location)
        val channelTarget = getLocationFromName(nameOfLocation)
        if(TessUtils.getCombat(player) != null){
            message.reply("You can't travel while in combat.")
            return
        }
        if (channelTarget != null) {
            if(channelTarget.combatZone && TessUtils.getClaimingFaction(channelTarget) != null && TessUtils.getClaimingFaction(channelTarget) != TessUtils.getFaction(player)){
                message.reply("That area is a combat zone, owned by ${TessUtils.getClaimingFaction(channelTarget)!!.name}, use !attack <location> to fight your way in.")
                return
            }
            if (playerLocation != null) {
                if (playerLocation.nearbyLocations.contains(channelTarget)) {
                    player.location = channelTarget.channel.name
                    lockAllOtherChannels(player, message.author)
                } else message.reply("You can't get to that location from here.")
            } else {
                player.location = channelTarget.channel.name
                lockAllOtherChannels(player, message.author)
            }
        } else message.reply("That is not an available location")

    }

    fun travelToLocation(player: PlayerData.Player, integer: Int, message: Message) {
        val playerLocation = getLocationFromName(player.location)
        if(TessUtils.getCombat(player) != null){
            message.reply("You can't travel while in combat.")
            return
        }
        if (playerLocation != null) {
            if (playerLocation.nearbyLocations.size >= integer) {
                val channelTarget = playerLocation.nearbyLocations[integer - 1]

                if(channelTarget.combatZone && TessUtils.getClaimingFaction(channelTarget) != null && TessUtils.getClaimingFaction(channelTarget) != TessUtils.getFaction(player)){
                    message.reply("That area is a combat zone, owned by ${TessUtils.getClaimingFaction(channelTarget)!!.name}, use !attack <location> to fight your way in.")
                    return
                }

                player.location = channelTarget.channel.name
                lockAllOtherChannels(player, message.author)
            } else message.reply("That is not an available location")
        }
    }

    fun attackLocation(player: PlayerData.Player, nameOfLocation: String, message: Message) {
        val playerLocation = getLocationFromName(player.location)
        val channelTarget = getLocationFromName(nameOfLocation)
        if(TessUtils.getCombat(player) != null){
            message.reply("You can't travel while in combat.")
            return
        }
        if (channelTarget != null) {
            if(channelTarget.combatZone && TessUtils.getClaimingFaction(channelTarget) != null && TessUtils.getClaimingFaction(channelTarget) != TessUtils.getFaction(player)){ } else return
            if (playerLocation != null) {
                if (playerLocation.nearbyLocations.contains(channelTarget)) {
                    if(Factions.attackControlledLocation(channelTarget, player, message)) {
                        player.location = channelTarget.channel.name
                        lockAllOtherChannels(player, message.author)
                    }
                } else message.reply("You can't get to that location from here.")
            } else {
                player.location = channelTarget.channel.name
                lockAllOtherChannels(player, message.author)
            }
        } else message.reply("That is not an available location")
    }

    fun attackLocation(player: PlayerData.Player, integer: Int, message: Message) {
        val playerLocation = getLocationFromName(player.location)
        if(TessUtils.getCombat(player) != null){
            message.reply("You can't travel while in combat.")
            return
        }
        if (playerLocation != null) {
            if (playerLocation.nearbyLocations.size >= integer) {
                val channelTarget = playerLocation.nearbyLocations[integer - 1]
                if(channelTarget.combatZone && TessUtils.getClaimingFaction(channelTarget) != null && TessUtils.getClaimingFaction(channelTarget) != TessUtils.getFaction(player)){ } else return
                if(channelTarget.combatCooldown){
                    message.reply("That location was recently attacked, and can't be attacked again for a while.")
                    return
                }
                if(Factions.attackControlledLocation(channelTarget, player, message)) {
                    player.location = channelTarget.channel.name
                    lockAllOtherChannels(player, message.author)
                }
            } else message.reply("That is not an available location")
        }
    }

    fun travelToLocationAnywhere(user : User, player: PlayerData.Player, nameOfLocation: String, message: Message?) {
        val channelTarget = getLocationFromName(nameOfLocation)
        if(TessUtils.getCombat(player) != null){
            message?.reply("You can't travel while in combat.")
            return
        }
        if (channelTarget != null) {
            player.location = channelTarget.channel.name
            lockAllOtherChannels(player, user)
        } else message?.reply("That is not an available location")
    }

    fun lockAllOtherChannels(player: PlayerData.Player, user: User) {
        locationList.forEach {
            val permission =  PermissionsBuilder(it.channel.getOverwrittenPermissions(user)).setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).setState(PermissionType.SEND_MESSAGES, PermissionState.ALLOWED).build()
            val permission2 = if(AdminCommands.admins.contains(player)) permission else  PermissionsBuilder(it.channel.getOverwrittenPermissions(user)).setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).setState(PermissionType.SEND_MESSAGES, PermissionState.DENIED).build()
            if (it.channel.name != "general" && it != getLocationFromName(player.location)) {
                it.channel.createUpdater().addPermissionOverwrite(user, permission2)
            } else {
                it.channel.createUpdater().addPermissionOverwrite(user, permission)
            }
        }
        player.saveData()
    }

    fun lockAllOtherChannels(player: PlayerData.Player, user: MessageAuthor) {
        if(user.isUser)
            lockAllOtherChannels(player, user.asUser().get())
    }

    fun unlockAllChannels(player: PlayerData.Player, user: User) {
        locationList.forEach {
            val permission = PermissionsBuilder(it.channel.getOverwrittenPermissions(user)).setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).setState(PermissionType.SEND_MESSAGES, PermissionState.ALLOWED).build()
            it.channel.createUpdater().addPermissionOverwrite(user, permission)
        }
        player.saveData()
    }

    fun getQuickTravelLocations() : List<Location>{
        return locationList.filter { it.quickTravelCost > 0 }
    }

    fun getLocationFromName(string: String): Location? {
        locationList.forEach {
            if (it.channel.name == string)
                return it
        }
        return null
    }

    class Location(val channel: ServerTextChannel) {
        val nearbyLocations = ArrayList<Location>()
        var combatZone = false
        var bar = false
        var combatCooldown = false
        var quickTravelCost = 0
        var erobait = 0
        var market = false
    }


}

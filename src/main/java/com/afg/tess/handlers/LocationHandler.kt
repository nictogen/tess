package com.afg.tess.handlers

import com.afg.tess.commands.AdminCommands
import com.afg.tess.commands.PlayerCommands
import com.afg.tess.init.Tess
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType

/**
 * Created by AFlyingGrayson on 9/9/17
 */
object LocationHandler {

    private val locationList = ArrayList<Location>()

    /**
     * Loads (or reloads) locations based on channel topics on the server
     */
    fun loadLocations() {
        locationList.clear()

        //Create locations from channels that have "Nearby" in the topic
        TessUtils.server.channels?.filter { it.topic != null && it.topic.toLowerCase().contains("nearby") }?.forEach { locationList.add(Location(it)) }

        //Add every channel in the topic to the nearby location list
        locationList.forEach {
            it.channel.topic.split(" ").filter { getLocationFromName(it) != null }.forEach { i ->
                it.nearbyLocations.add(getLocationFromName(i)!!)
            }
        }
    }


    /**
     * Travels to a location from a nearby location using the name (Bridge Method)
     */
    fun travelToLocation(player: PlayerHandler.Player, nameOfLocation: String, message: Message) {
        travelToLocation(player, getLocationFromName(player.location)!!.nearbyLocations.indexOf(getLocationFromName(nameOfLocation)), message)
    }

    /**
     * Travels to a nearby location
     */
    fun travelToLocation(player: PlayerHandler.Player, integer: Int, message: Message) {
        player.location = getLocationFromName(player.location)!!.nearbyLocations[integer].channel.name
        if(PlayerCommands.pickupMap.containsKey(player)){
            PlayerCommands.pickupMap[player]!!.location = getLocationFromName(player.location)!!.nearbyLocations[integer - 1].channel.name
            lockAllOtherChannels( PlayerCommands.pickupMap[player]!!, TessUtils.getMember( PlayerCommands.pickupMap[player]!!))
        }
        lockAllOtherChannels(player, message.author)
    }

    /**
     * Travels to any location
     */
    fun travelToLocationAnywhere(user : User, player: PlayerHandler.Player, nameOfLocation: String) {
        if(PlayerCommands.pickupMap.containsKey(player)){
            PlayerCommands.pickupMap[player]!!.location = getLocationFromName(nameOfLocation)!!.channel.name
            lockAllOtherChannels( PlayerCommands.pickupMap[player]!!, TessUtils.getMember( PlayerCommands.pickupMap[player]!!))
        }
        player.location = getLocationFromName(nameOfLocation)!!.channel.name
        lockAllOtherChannels(player, user)
    }

    /**
     * Blocks the player from seeing every location that they aren't in (unless in adminmode)
     */
    fun lockAllOtherChannels(player: PlayerHandler.Player, user: User) {
        locationList.forEach {
            val permission = Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).setState(PermissionType.SEND_MESSAGES, PermissionState.ALLOWED).build()
            val permission2 = if(AdminCommands.admins.contains(player)) permission else Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).setState(PermissionType.SEND_MESSAGES, PermissionState.DENIED).build()
            if (it != getLocationFromName(player.location)) {
                it.channel.updateOverwrittenPermissions(user, permission2)
            } else {
                it.channel.updateOverwrittenPermissions(user, permission)
            }
        }
        PlayerHandler.saveData(player)
    }


    /**
     * Allows the player to see every location, even if they aren't there (for use in !adminmode)
     */
    fun unlockAllChannels(player: PlayerHandler.Player, user: User) {
        locationList.forEach {
            val permission = Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).setState(PermissionType.SEND_MESSAGES, PermissionState.ALLOWED).build()
            it.channel.updateOverwrittenPermissions(user, permission)
        }
        PlayerHandler.saveData(player)
    }

    fun getLocationFromName(string: String): Location? = if(locationList.any { it.channel.name == string }) locationList.first{ it.channel.name == string } else locationList.first { it.channel.name == "dock" }
    class Location(val channel: Channel) { val nearbyLocations = ArrayList<Location>() }
}
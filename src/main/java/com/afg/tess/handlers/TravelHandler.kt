package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.util.ISaveable
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.entities.permissions.PermissionState
import de.btobastian.javacord.entities.permissions.PermissionType
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by AFlyingGrayson on 10/7/17
 */
object TravelHandler {

    val locations = ArrayList<Location>()

    val planetDocks : ArrayList<Location>
    get() {
        val list = ArrayList<Location>()
        list.addAll(locations.filter { it.spaceCoordX != 0 || it.spaceCoordY != 0 || it.spaceCoordZ != 0 })
        return list
    }

    fun createLocation(parentLocation: Location?, name: String): Location {
        val location = Location()
        if (parentLocation != null) {
            location.nearby.add(parentLocation.uuid)
            parentLocation.nearby.add(location.uuid)
        }
        location.name = name
        location.saveData()
        locations.add(location)
        return location
    }

    fun travel(player: PlayerHandler.Player, name: String) {
        val location = locations.first { it.uuid == player.location }
        player.location = location.nearbyLocations.first { it.name.toLowerCase().contains(name.toLowerCase()) }.uuid
        player.saveData()
        updateChannels()
    }

    fun travel(player: PlayerHandler.Player, location: Location) {
        player.location = location.uuid
        player.saveData()
        updateChannels()
    }

    fun updateChannels() {

        ShipHandler.ships.forEach { ship ->
            if(locations.any { it.uuid == ship.dock }){
                val location = locations.first { it.uuid == ship.dock }
                val bridge = ship.bays.first { it.name == ship.name }
                if(!bridge.nearbyLocations.contains(location)) {
                    bridge.nearby.add(location.uuid)
                    bridge.saveData()
                }
                if(!location.nearbyLocations.contains(bridge)) {
                    location.nearby.add(ship.bays.first { it.name == ship.name }.uuid)
                    location.saveData()
                }
            } else {
                val bridge = ship.bays.first { it.name == ship.name }
                bridge.nearby.removeAll(planetDocks.map { it.uuid })
                bridge.saveData()
                planetDocks.forEach { l -> l.nearby.remove(bridge.uuid); l.saveData()}
            }
        }

        locations.forEach { location ->
            if (PlayerHandler.players.any { it.location == location.uuid } && !TessUtils.server.channels.any { it.id == location.channelID }) {
                val channel = TessUtils.server.createChannel(location.name).get()
                location.channelID = channel.id
                location.saveData()
            }
            if (TessUtils.server.channels.any { it.id == location.channelID }) {
                val channel = TessUtils.server.channels.first { it.id == location.channelID }
                var s = "Nearby:   "
                location.nearbyLocations.forEach {
                    s += "${it.name}, "
                }
                s = s.substring(0, s.length - 2)
                channel.updateTopic(s)
            }
        }

        val permission = Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.ALLOWED).build()
        val permission2 = Tess.api.permissionsBuilder.setState(PermissionType.READ_MESSAGES, PermissionState.DENIED).build()
        Tess.api.channels.forEach { c ->
            if (c.topic != null && c.topic.toLowerCase().contains("nearby")) {
                if(locations.any { it.channelID == c.id }) {
                    val location = locations.first { it.channelID == c.id }
                    PlayerHandler.players.forEach { p ->
                        if (p.location == location.uuid)
                            c.updateOverwrittenPermissions(TessUtils.getMember(p), permission)
                        else c.updateOverwrittenPermissions(TessUtils.getMember(p), permission2)
                    }
                } else c.delete()
            }
        }
    }

    open class Location : ISaveable {
        var uuid = UUID.randomUUID().toString()
        var channelID = ""
        var name = ""
        var spaceCoordX = 0
        var spaceCoordY = 0
        var spaceCoordZ = 0
        var nearby = ArrayList<String>()
        val nearbyLocations: ArrayList<Location>
            get() {
                val list = ArrayList<Location>()
                nearby.forEach { try { list.add(locations.first { l -> l.uuid == it }) } catch (e: Exception) { } }
                return list
            }

        override fun getFileName() = uuid

        override fun getFolderPath() = Tess.locationFolderPath!!
    }
}
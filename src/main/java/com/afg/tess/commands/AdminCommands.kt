package com.afg.tess.commands

import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ShipHandler
import com.afg.tess.handlers.TravelHandler
import com.afg.tess.util.TessUtils
import com.afg.tess.util.rpName

/**
 * Created by AFlyingGrayson on 10/7/17
 */
object AdminCommands {

    @Command(aliases = arrayOf("!cb"))
    fun onCreateBase(info: CommandHandler.MessageInfo, name : String) : String {
        return if(TessUtils.isAdmin(info.user)){
            TravelHandler.travel(info.player, TravelHandler.createLocation(null, name))
            "Created location $name"
        } else "You aren't an admin."
    }

    @Command(aliases = arrayOf("!u"))
    fun onUpdate(info: CommandHandler.MessageInfo) : String {
        return if(TessUtils.isAdmin(info.user)){
            TravelHandler.updateChannels()
            return ""
        } else "You aren't an admin."
    }

    @Command(aliases = arrayOf("!setdock"))
    fun onSetDock(info: CommandHandler.MessageInfo, x : Int, y : Int, z : Int) : String {
        return if(TessUtils.isAdmin(info.user)){
            val location = TravelHandler.locations.first{info.player.location == it.uuid}
            location.spaceCoordX = x
            location.spaceCoordY = y
            location.spaceCoordZ = z
            location.saveData()
            return ""
        } else "You aren't an admin."
    }

    @Command(aliases = arrayOf("!cl"))
    fun onCreateLocation(info: CommandHandler.MessageInfo, name : String) : String {
        return if(TessUtils.isAdmin(info.user)){
            val parent = TravelHandler.locations.first{info.player.location == it.uuid}
            TravelHandler.createLocation(parent, name)
            TravelHandler.updateChannels()
            "Created location $name"
        } else "You aren't an admin."
    }

    @Command(aliases = arrayOf("!giveship"))
    fun onGiveShip(info: CommandHandler.MessageInfo, player : PlayerHandler.Player, name : String, dock : String) : String {
        return if(TessUtils.isAdmin(info.user)){
            ShipHandler.createShip(player, name, dock)
            "Created ship: $name owned by ${player.rpName} at $dock."
        } else "You aren't an admin."
    }

    @Command(aliases = arrayOf("!travel", "!t"))
    fun onTravel(info: CommandHandler.MessageInfo, name: String){
        TravelHandler.travel(info.player, name)
    }

    @Command(aliases = arrayOf("!jumptowards", "!jt"))
    fun onJumpTowards(info: CommandHandler.MessageInfo, dockName: String) : String {
        val location = TravelHandler.locations.first { it.uuid == info.player.location }
        val ship = ShipHandler.ships.first { it.crew.contains(info.player) }
        if(ship.dock != "") return "You can't jump while you're docked."
        val bridge = ship.bays.first { it.name.toLowerCase() == ship.name.toLowerCase() }
        val dock = TravelHandler.planetDocks.first{ it.name.toLowerCase() == dockName.toLowerCase() }
        return if(location == bridge){
            if(ship.spaceCoordX != dock.spaceCoordX)
                ship.spaceCoordX += if(ship.spaceCoordX > dock.spaceCoordX) -1 else 1
            if(ship.spaceCoordY != dock.spaceCoordY)
                ship.spaceCoordY += if(ship.spaceCoordY > dock.spaceCoordY) -1 else 1
            if(ship.spaceCoordZ != dock.spaceCoordZ)
                ship.spaceCoordZ += if(ship.spaceCoordZ > dock.spaceCoordZ) -1 else 1
            ship.saveData()
            "Jumped to space sector ${ship.spaceCoordX}, ${ship.spaceCoordY}, ${ship.spaceCoordZ}."
        } else "You aren't on the bridge of your ship."
    }

    @Command(aliases = arrayOf("!dock", "!d"))
    fun onDock(info: CommandHandler.MessageInfo, dockName: String) : String {
        val location = TravelHandler.locations.first { it.uuid == info.player.location }
        val ship = ShipHandler.ships.first { it.crew.contains(info.player) }
        val bridge = ship.bays.first { it.name.toLowerCase() == ship.name.toLowerCase() }
        val dock = TravelHandler.planetDocks.first{ it.name.toLowerCase() == dockName.toLowerCase() }
        return if(location == bridge){
            if(ship.spaceCoordX == dock.spaceCoordX && ship.spaceCoordY == dock.spaceCoordY && ship.spaceCoordZ == dock.spaceCoordZ) {
                ship.dock = dock.uuid
                ship.saveData()
                TravelHandler.updateChannels()
                "Docked ${ship.name} with ${dock.name}"
            } else "You aren't in the same space sector as that dock."
        } else "You aren't on the bridge of your ship."
    }

    @Command(aliases = arrayOf("!undock", "!ud"))
    fun onUnDock(info: CommandHandler.MessageInfo) : String {
        val location = TravelHandler.locations.first { it.uuid == info.player.location }
        val ship = ShipHandler.ships.first { it.crew.contains(info.player) }
        val bridge = ship.bays.first { it.name.toLowerCase() == ship.name.toLowerCase() }
        val dock = TravelHandler.planetDocks.first{ it.uuid == ship.dock }
        return if(location == bridge){
            ship.dock = ""
            ship.saveData()
            TravelHandler.updateChannels()
            "Undocked ${ship.name} from ${dock.name}"
        } else "You aren't on the bridge of your ship."
    }
}
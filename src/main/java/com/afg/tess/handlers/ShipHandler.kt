package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.util.ISaveable
import com.afg.tess.util.TessUtils
import java.util.*
import kotlin.collections.ArrayList

/**
 * Created by AFlyingGrayson on 10/7/17
 */
object ShipHandler {

    val ships = ArrayList<Spaceship>()

    fun createShip(owner : PlayerHandler.Player, name : String, dockedAt : String){
        val ship = Spaceship()
        ship.owner = owner.playerID
        ship.crewIds.add(owner.playerID)
        ship.name = name
        val dock = TravelHandler.planetDocks.first{ it.name.toLowerCase() == dockedAt.toLowerCase() }
        ship.dock = dock.uuid
        ship.spaceCoordX = dock.spaceCoordX
        ship.spaceCoordY = dock.spaceCoordY
        ship.spaceCoordZ = dock.spaceCoordZ

        val bridge = TravelHandler.createLocation(dock, name)
        ship.bayIds.add(bridge.uuid)
        ship.bayIds.add(TravelHandler.createLocation(bridge, "engine-bay").uuid)
        ship.bayIds.add(TravelHandler.createLocation(bridge, "shield-bay").uuid)
        ship.bayIds.add(TravelHandler.createLocation(bridge, "living-quarters").uuid)
        ship.bayIds.add(TravelHandler.createLocation(bridge, "open-bay").uuid)
        ship.bayIds.add(TravelHandler.createLocation(bridge, "open-bay").uuid)
        ship.bayIds.add(TravelHandler.createLocation(bridge, "open-bay").uuid)

        ships.add(ship)
        ship.saveData()
        TravelHandler.updateChannels()
    }

    class Spaceship : ISaveable {
        var id = UUID.randomUUID().toString()
        var owner = ""
        var name = ""
        var dock = ""
        var bayIds = ArrayList<String>()
        var crewIds = ArrayList<String>()
        var spaceCoordX = 0
        var spaceCoordY = 0
        var spaceCoordZ = 0
        val bays : ArrayList<TravelHandler.Location>
        get() {
            val list = ArrayList<TravelHandler.Location>()
            bayIds.forEach { id ->
                if(TravelHandler.locations.any { it.uuid == id })
                list.add(TravelHandler.locations.first { it.uuid == id })
            }
            return list
        }
        val crew : ArrayList<PlayerHandler.Player>
        get() {
            val list = ArrayList<PlayerHandler.Player>()
            crewIds.forEach { list.add(TessUtils.getPlayer(it)) }
            return list
        }
        override fun getFileName() = id
        override fun getFolderPath() = Tess.shipFolderPath!!
    }
}
package com.afg.tess.handlers

import com.afg.tess.util.ISaveable
import de.btobastian.javacord.entities.Server

/**
 * Created by AFlyingGrayson on 10/7/17
 */
object LocationHandler {

    val locations = ArrayList<Location>()

    fun createLocation(name: String, server: Server): Location {
        val location = Location()
        location.name = name
        location.serverID = server.id
        location.saveData()
        locations.add(location)
        return location
    }

    open class Location : ISaveable {
        var unicodeEmoji = ""
        var name = ""
        var channelID : Long = 0
        var nearby = ArrayList<String>()
        var serverID : Long = 0

        override fun getFolderPath() = "tessData/$serverID"
        override fun getFileName() = name
    }
}
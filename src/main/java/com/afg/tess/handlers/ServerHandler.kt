package com.afg.tess.handlers

import com.afg.tess.init.Tess
import com.afg.tess.util.ISaveable
import java.util.*

/**
 * Created by AFlyingGrayson on 10/15/17
 */
object ServerHandler{

    val serverList = ArrayList<Server>()

    class Server : ISaveable{

        var id : Long = 0
        var defaultLocation = UUID.randomUUID().toString()

        override fun getFolderPath() = Tess.serverFolderPath
        override fun getFileName() = id.toString()

    }
}
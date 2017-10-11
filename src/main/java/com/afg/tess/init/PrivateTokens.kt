package com.afg.tess.init

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.Javacord

/**
 * Created by AFlyingGrayson on 9/12/17
 */
object PrivateTokens {

    fun getAPI() : DiscordAPI{
//       return Javacord.getApi("MzU1NDQzMDU4MjQzNzk2OTkz.DJM3rw.NbYbY7m8S1AdcAoJC1xBsSzO3Ac", true)
        return Javacord.getApi("MzY2Mzc5OTE3ODQ1MjAwODk3.DLsDIg.0zeFAqkZkBC2KQ1I2Ekw2hQPyuk", true)
    }

    fun getNAPI() : DiscordAPI {
        return Javacord.getApi("rhonedrake@gmail.com", "Draker500?")
    }

}
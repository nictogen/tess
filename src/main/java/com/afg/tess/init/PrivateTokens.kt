package com.afg.tess.init

import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.Javacord

/**
 * Created by AFlyingGrayson on 9/12/17
 */
object PrivateTokens {

    fun getAPI() : DiscordAPI{
//       return Javacord.getApi("MzU1NDQzMDU4MjQzNzk2OTkz.DJM3rw.NbYbY7m8S1AdcAoJC1xBsSzO3Ac", true)
        return Javacord.getApi("MzU0MDM2NTk3MDE4MTk4MDE3.DJuBWA.YWLbkKi8fmcdQOck3mUqvYEloNU", true)
    }

    fun getNAPI() : DiscordAPI {
        return Javacord.getApi("rhonedrake@gmail.com", "Draker500?")
    }

}
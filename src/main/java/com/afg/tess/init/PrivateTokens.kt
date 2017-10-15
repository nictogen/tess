package com.afg.tess.init

import de.btobastian.javacord.DiscordApi
import de.btobastian.javacord.DiscordApiBuilder

/**
 * Created by AFlyingGrayson on 9/12/17
 */
object PrivateTokens {

    fun getAPI(): DiscordApi{
//       return Javacord.getApi("MzU1NDQzMDU4MjQzNzk2OTkz.DJM3rw.NbYbY7m8S1AdcAoJC1xBsSzO3Ac", true)
        return DiscordApiBuilder().setToken("MzY2Mzc5OTE3ODQ1MjAwODk3.DLsDIg.0zeFAqkZkBC2KQ1I2Ekw2hQPyuk").login().join()
    }

//    fun getNAPI() : DiscordApi {
//        return DiscordApiBuilder().setAccountType(AccountType.CLIENT).
//        return Javacord.getApi("rhonedrake@gmail.com", "Draker500?")/
//    }

}
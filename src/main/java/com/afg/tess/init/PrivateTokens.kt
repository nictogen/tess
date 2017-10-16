package com.afg.tess.init

import de.btobastian.javacord.DiscordApi
import de.btobastian.javacord.DiscordApiBuilder

/**
 * Created by AFlyingGrayson on 9/12/17
 */
object PrivateTokens {

    fun getAPI(): DiscordApi{
        return DiscordApiBuilder().setToken("MzY5Mjc2NDcyMDQ2OTExNTE1.DMWNOA.CylUU5qZ_EvRCgdUJwSe78cxaMU").login().join()
    }
    //https://discordapp.com/api/oauth2/authorize?client_id=369276472046911515&scope=bot&permissions=0
}
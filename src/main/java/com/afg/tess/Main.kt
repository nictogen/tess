package com.afg.tess

import com.afg.tess.commands.AdminCommands
import com.afg.tess.commands.PlayerCommands
import de.btobastian.javacord.listener.message.MessageCreateListener
import de.btobastian.sdcf4j.handler.JavacordHandler
import java.util.*

/**
 * Created by AFlyingGrayson on 9/7/17
 */
class Main {

    companion object {

        var dayChanged = false
        var ticks = 0

        @JvmStatic
        fun main() {
            Tess.api = PrivateTokens.getAPI()
            Tess.api.connectBlocking()
            Tess.api.registerListener(MessageCreateListener { _, message -> if (message.channelReceiver != null && message.content.contains("make") && message.content.contains("a player")) message.channelReceiver.server.members.forEach { if (message.content.contains(it.id)) PlayerData.createPlayer(it, message) } })
            val cmdHandler = JavacordHandler(Tess.api)
            cmdHandler.registerCommand(AdminCommands)
            cmdHandler.registerCommand(PlayerCommands)
            PlayerData.loadData()
            LocationHandler.loadLocations()
            Factions.loadData()

            while (true) {
                val calendar = Calendar.getInstance()
                val minutes = calendar.get(Calendar.MINUTE)
                val hours = calendar.get(Calendar.HOUR_OF_DAY)

                if (minutes == 0) {
                    if (!dayChanged) {
                        dayChanged = true
                        if (hours % 2 == 0) {

                            val weatherForecast = when (Tess.rand.nextInt(100)) {
                                in 50..90 -> "It's very sunny."
                                in 80..95 -> "It's raining."
                                in 95..100 -> "It's snowing"
                                else -> "The skies are clear."
                            }
                            TessUtils.getChannelFromName("announcements")?.sendMessage("A new day has begun. $weatherForecast")
                        }
                        if (hours % 6 == 0) {
                            TessUtils.getServer()?.members?.forEach {
                                it.getRoles(TessUtils.getServer()).forEach { role ->
                                    val player = TessUtils.getPlayer(it.mentionTag)
                                    if (player != null) {
                                        when (role.name) {
                                            "slave" -> player.money += 5
                                            "low-income" -> player.money += 50
                                            "medium-income" -> player.money += 100
                                            "high-income" -> player.money += 250
                                            "noble" -> player.money += 500
                                        }
                                        player.saveData()
                                    }
                                }
                            }
                            TessUtils.getChannelFromName("announcements")?.sendMessage("A new week has turned over, and everyone has gained their weekly income.")
                        }
                    }
                } else if (minutes == 15) {
                    if (!dayChanged) {
                        dayChanged = true
                        LocationHandler.locationList.forEach { it.combatCooldown = false }
                        TessUtils.spawnEroAtBoundary()
                    }
                } else if (minutes == 45) {
                    if (!dayChanged) {
                        dayChanged = true
                        LocationHandler.locationList.forEach { it.combatCooldown = false }
                        TessUtils.spawnEroAtBoundary()
                    }
                } else dayChanged = false

                if(ticks == 20) {
                    PlayerData.players.forEach {
                        val location = LocationHandler.getLocationFromName(it.location)
                        val channel = TessUtils.getLocation(it)
                        val user = TessUtils.getRpMember(it.playerID)
                        if (location != null && channel != null && user != null) {
                            if (TessUtils.getCombat(channel) == null || TessUtils.getCombat(it) == null) {
                                val channelFaction = TessUtils.getClaimingFaction(location)
                                val playerFaction = TessUtils.getFaction(it)
                                if (location.combatZone && channelFaction != null && playerFaction != channelFaction) {
                                    try {
                                        val channelTarget = location.nearbyLocations[0]
                                        LocationHandler.travelToLocationAnywhere(user, it, channelTarget.channel.name, null)
                                    } catch (e: Exception) {
                                    }

                                }
                            }
                        }
                    }
                    ticks = 0
                } else ticks++
            }
        }


    }


}
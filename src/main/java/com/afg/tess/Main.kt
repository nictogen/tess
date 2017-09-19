package com.afg.tess

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.moves.NothingMove
import com.afg.tess.commands.AdminCommands
import com.afg.tess.commands.PlayerCommands
import com.afg.tess.commands.api.CommandHandler
import de.btobastian.javacord.listener.message.MessageCreateListener
import java.util.*

/**
 * Created by AFlyingGrayson on 9/7/17
 */
class Main {

    companion object {

        @JvmStatic
        fun main() {
            Tess.api = PrivateTokens.getAPI()
            Tess.api.connectBlocking()
            Tess.api.registerListener(MessageCreateListener { _, message -> if (message.channelReceiver != null && message.content.contains("make") && message.content.contains("a player")) message.channelReceiver.server.members.forEach { if (message.content.contains(it.id)) PlayerData.createPlayer(it, message) } })
            Tess.api.registerListener(AlcoholHandler)
            Tess.api.registerListener(CommandHandler)
            CommandHandler.loadCommands(AdminCommands)
            CommandHandler.loadCommands(PlayerCommands)
            PlayerData.loadData()
            LocationHandler.loadLocations()
            Factions.loadData()


            var lastSecond = 0
            var lastMinute = 0

            while (true) {
                val calendar = Calendar.getInstance()
                val hour = calendar.get(Calendar.HOUR_OF_DAY)
                val minute = calendar.get(Calendar.MINUTE)
                val second = calendar.get(Calendar.SECOND)

                if (second != lastSecond) {

                    //Combat Timer
                    CombatHandler.combatList.forEach {
                        if (minute == it.nextRoundMinutes && second == it.nextRoundSeconds) {
                            if (it.participants.any { it is CombatHandler.Player })
                                it.participants.forEach { p ->
                                    if (p.nextMove == null)
                                        it.decideMove(NothingMove(), p)
                                }
                            it.setRoundTimer()
                        }
                    }

                    //Kick out of controlled combat zones
                    PlayerData.players.forEach {
                        val location = LocationHandler.getLocationFromName(it.location)
                        val channel = TessUtils.getLocation(it)
                        val user = TessUtils.getMember(it)
                        if (location != null && channel != null && user != null) {
                            if (TessUtils.getCombat(channel) == null || TessUtils.getCombat(it) == null) {
                                val channelFaction = TessUtils.getClaimingFaction(location)
                                val playerFaction = TessUtils.getFaction(it)
                                if (location.combatZone && channelFaction != null && playerFaction != channelFaction) {
                                    try {
                                        val channelTarget = location.nearbyLocations[0]
                                        LocationHandler.travelToLocationAnywhere(user, it, channelTarget.channel.name, null)
                                    } catch (e: Exception) { }
                                }
                            }
                        }
                    }

                    //Time mechanics
                    if (minute != lastMinute) {
                        if(minute == 0) {
                            if (hour % 2 == 0) {
                                PlayerData.players.forEach {
                                    it.drunkness = 0
                                    it.saveData()
                                }
                                val weatherForecast = when (Tess.rand.nextInt(100)) {
                                    in 50..90 -> "It's very sunny."
                                    in 80..95 -> "It's raining."
                                    in 95..100 -> "It's snowing"
                                    else -> "The skies are clear."
                                }
                                TessUtils.getChannelFromName("absol-announcements")?.sendMessage("A new day has begun. $weatherForecast")
                                TessUtils.getChannelFromName("cana-announcements")?.sendMessage("A new day has begun. $weatherForecast")
                            }
                            if (hour % 6 == 0) {
                                TessUtils.getServer().members?.forEach {
                                    val player = TessUtils.getPlayer(it.mentionTag)
                                    if (player != null) {
                                        player.money += player.income
                                        player.saveData()
                                    }
                                }
                                TessUtils.getChannelFromName("absol-announcements")?.sendMessage("A new week has turned over, and everyone has gained their weekly income.")
                                TessUtils.getChannelFromName("cana-announcements")?.sendMessage("A new week has turned over, and everyone has gained their weekly income.")
                            }
                        }

                        if (minute == 15 || minute == 45) {
                            LocationHandler.locationList.forEach { it.combatCooldown = false }
                            TessUtils.spawnEroAtBoundary()
                        }
                        lastMinute = minute
                    }
                    lastSecond = second
                }
            }
        }


    }


}
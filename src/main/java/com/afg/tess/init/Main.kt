package com.afg.tess.init

import com.afg.tess.commands.AdminCommands
import com.afg.tess.commands.api.CommandHandler
import com.afg.tess.handlers.LocationHandler
import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.handlers.ReactionHandler
import com.afg.tess.handlers.ReactionResponseHandler
import com.afg.tess.util.ISaveable
import com.afg.tess.util.TessUtils


/**
 * Created by AFlyingGrayson on 9/7/17
 */
class Main {

    companion object {

        @JvmStatic
        fun main() {
            var lastSecond = 0
            //Connect to ARP Server Account


            Tess.api = PrivateTokens.getAPI()

            //Connect to Current Arc Account
//            Tess.arcApi = PrivateTokens.getNAPI()
//            Tess.arcApi.connectBlocking()

            //Load the saveable things
//            Skill.loadSkills()
            ISaveable.loadData(Tess.playerDataFolderPath, PlayerHandler.players, true)
            ISaveable.loadData(Tess.locationFolderPath, LocationHandler.locations, true)

            //Add all created commands to the handler
            CommandHandler.loadCommands(AdminCommands)
            //            CommandHandler.loadCommands(PlayerCommands)

            Tess.api.addMessageCreateListener({ t ->
                val message = t.message
                if (message.channel != null && message.content.contains("new player") && TessUtils.isAdmin(message.author.get())){
                    val args = message.content.split(" ")
                    PlayerHandler.createPlayer(message, args[2])
                }
            })
            Tess.api.addMessageCreateListener(LocationHandler)
            Tess.api.addReactionAddListener(ReactionResponseHandler)
            Tess.api.addMessageCreateListener(CommandHandler)
            Tess.api.addMessageEditListener(CommandHandler)

            ReactionHandler.start()

//            while (true) {
//                val calendar = Calendar.getInstance()
//                val minute = calendar.get(Calendar.MINUTE)
//                val second = calendar.get(Calendar.SECOND)
//                if (lastSecond != second) {
//                    LocationHandler.locations.forEach { l ->
//                        if (l.combat && l.combatSecond == second && l.nextCombatMinute == minute) l.nextCombatMinute++
//                        else if(l.combat){
//                            TessUtils.getPlayersInCombat(l).forEach { p ->
//                                if (!ReactionResponseHandler.reactionMessageList.any { it is ReactionResponseHandler.CombatMenuMessage && it.player == p })
//                                    ReactionResponseHandler.MainMenuMessage(p, l)
//                            }
//                            if(!TessUtils.getPlayersInCombat(l).any { p -> !ReactionResponseHandler.reactionMessageList.any { it is ReactionResponseHandler.CompletionMessage && it.player == p} }){
//                                val order = TessUtils.getPlayersInCombat(l)
////                                order.sortByDescending { it.stats.first { it.type == PlayerHandler.StatType.COORDINATION }.value }
//                                val recaps = LinkedList<ReactionResponseHandler.Recap>()
//                                order.forEach { p ->
//                                    val c = ReactionResponseHandler.reactionMessageList.first { it is ReactionResponseHandler.CompletionMessage && it.player == p} as ReactionResponseHandler.CompletionMessage
//                                    recaps.add(c.execute())
//                                    c.removeFromList(TessUtils.server.channels.first { it.id.toString() == p.channelID }.asServerTextChannel().get().getMessageById(c.messageID).get())
//                                }
//                                order.forEach { p ->
//                                    ReactionResponseHandler.RecapMessage(p, l, recaps)
//                                }
//                            }
//                        } else {
//                            PlayerHandler.players.filter { it.location == l.uuid }.forEach { p ->
//                                p.combatLocationX = -1
//                                p.combatLocationY = -1
//                            }
//                        }
//                    }
//                    lastSecond = second
//                }
//
//            }

        }


    }


}
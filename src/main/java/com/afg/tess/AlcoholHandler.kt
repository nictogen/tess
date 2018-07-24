package com.afg.tess

import com.afg.tess.combat.CombatHandler
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.listener.message.MessageCreateListener

/**
 * Created by AFlyingGrayson on 9/15/17
 */
object AlcoholHandler : MessageCreateListener {

    override fun onMessageCreate(p0: MessageCreateEvent) {
        val message = p0.message
        val player = TessUtils.getPlayer(message.author.asUser().get().mentionTag)

        if(player != null){
            val location = TessUtils.getLocation(player)
            if(message.channel == location && message.content.isNotEmpty() && message.content[0] != '!' && message.content[0] != '['){
                when(player.drunkness){
                    in 20..30 -> {
                        if(Tess.rand.nextInt(10) <= 2){
                            var messageContent = ""
                            message.content.split(" ").forEach { words ->
                                var word = ""
                                if(Tess.rand.nextInt(10) <= 1)
                                    word = "*hic*"
                                else
                                    words.forEach { letter ->
                                        if(Tess.rand.nextInt(10) <= 2) {
                                            val times = Tess.rand.nextInt(5) + 2
                                            for(i in 0..times)
                                                word += letter
                                        } else word += letter
                                    }
                                messageContent += "$word "
                            }
                            message.delete()
                            message.reply("${message.author.rpName} tried to speak but it sounded like: $messageContent")
                        }
                    }
                    in 30..1000 -> {
                        var messageContent = ""
                        message.content.split(" ").forEach { words ->
                            var word = ""
                            if(Tess.rand.nextInt(10) <= 1)
                                word = "*hic*"
                            else
                                words.forEach { letter ->
                                    if(Tess.rand.nextInt(10) <= 4) {
                                        val times = Tess.rand.nextInt(10) + 2
                                        for(i in 0..times)
                                            word += letter
                                    } else word += letter
                                }
                            messageContent += "$word "
                        }
                        message.delete()
                        message.reply("${message.author.rpName} tried to speak but it sounded like: $messageContent")
                    }
                }
            }
        }
    }

    fun editStats(participant: CombatHandler.CombatParticipant, drunkness : Int){
        participant.strength += drunkness/4
        participant.intelligence -= drunkness/4
        participant.accuracy -= drunkness/4
    }

}
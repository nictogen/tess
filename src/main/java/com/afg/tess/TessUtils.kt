package com.afg.tess

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.npcs.Ero
import org.javacord.api.entity.channel.Channel
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.message.MessageAuthor
import org.javacord.api.entity.server.Server
import org.javacord.api.entity.user.User

/**
 * Created by AFlyingGrayson on 9/3/17
 */
object TessUtils {

    private val messageMap = HashMap<Channel, Message>()

    fun sendMessage(channel: ServerTextChannel, string: String) {
        if (messageMap.containsKey(channel)) {
            messageMap[channel]?.delete()
            messageMap.remove(channel)
        }
        messageMap[channel] = channel.sendMessage(string).get()
    }

    fun getKey(s: String): String {
        val divide = s.indexOf('=')
        return s.substring(0, divide)
    }

    fun getValue(s: String): String {
        val divide = s.indexOf('=')
        return s.substring(divide + 1)
    }

    fun numberToLetter(number: Int): Char {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return letters[number]
    }

    fun letterToNumber(letter: Char): Int {
        val letters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
        return letters.indexOf(letter.toUpperCase())
    }


    fun getPlayer(mentionTag: String): PlayerData.Player? {
        PlayerData.players.forEach { player ->
            if (mentionTag.contains(player.playerID.toString()))
                    return player
        }
        return null
    }

    fun getPlayer(messageAuthor: MessageAuthor): PlayerData.Player? {
        if(messageAuthor.isUser) return getPlayer(messageAuthor.asUser().get().mentionTag)
        return null
    }

    fun getServer(): Server {
        return Tess.api.servers.elementAt(0)
    }

    fun getChannelFromName(name: String): ServerTextChannel? {
        val server = getServer()
        server.channels?.forEach {
            if (it is ServerTextChannel && name == it.name)
                return it
        }
        return null
    }

    fun getLocation(player: PlayerData.Player): ServerTextChannel? {
        val server = getServer()
        server.channels?.forEach {
            if (it is ServerTextChannel && it.name == player.location)
                return it
        }
        return null
    }

    fun getCombat(channel: Channel): Combat? {
        CombatHandler.combatList.forEach { if (it.location == channel) return it }
        return null
    }

    fun getCombat(player: PlayerData.Player): Combat? {
        CombatHandler.combatList.forEach { if (it.participants.any { it is CombatHandler.Player && it.player == player }) return it }
        return null
    }

    fun getFaction(player: PlayerData.Player): Factions.Faction {
        Factions.factionList.forEach {
            if (it.admins.contains(player) || it.grunts.contains(player))
                return it
        }
        return Factions.factionList.filter { it.name == "factionless" }[0]
    }

    fun getClaimingFaction(location: LocationHandler.Location): Factions.Faction? {
        if (location.combatZone)
            Factions.factionList.forEach {
                if (it.controlledLocations.containsKey(location))
                    return it
            }
        return null
    }

    fun spawnEroAtBoundary() {
        LocationHandler.locationList.forEach {
            if(it.channel.name.contains("boundary")){
                val rank = Tess.rand.nextInt(5) + 1 + it.erobait
                val combat = getCombat(it.channel)
                if(combat == null){
                    Ero.spawnMonster(it, null, rank, false)
                    if(it.channel.name.contains("absol"))
                        getChannelFromName("absol-announcements")?.sendMessage("ALERT: A rank $rank Ero has appeared in ${it.channel.name}.")
                    else if(it.channel.name.contains("cana"))
                        getChannelFromName("cana-announcements")?.sendMessage("ALERT: A rank $rank Ero has appeared in ${it.channel.name}.")
                }
                it.erobait = 0
            }
        }
    }

    fun isAdmin(user: User): Boolean {
        return user.id == 150541854029381632 || user.id == 161882538514579466 || user.id == 332739616505462784
    }

    fun isArcLeader(user: User) : Boolean {
        val player = TessUtils.getPlayer(user.mentionTag)
        return player != null && player.arcleader == 1
    }

    fun isModerator(user: User): Boolean {
        return isAdmin(user)
    }

    fun getMember(player : PlayerData.Player): User? {
        getServer().members?.forEach { member -> if (player.playerID == member.id) return member }
        return null
    }
}

val MessageAuthor.rpName : String
    get() = if (this.isUser && TessUtils.getServer().getNickname(this.asUser().get()).isPresent) TessUtils.getServer().getNickname(this.asUser().get()).get() else this.name

val User.rpName : String
    get() = if (TessUtils.getServer().getNickname(this).isPresent) TessUtils.getServer().getNickname(this).get() else this.name

val PlayerData.Player.rpName : String
    get() = if(TessUtils.getMember(this) != null) TessUtils.getMember(this)!!.rpName else this.name

fun Message.reply(string : String) {
    this.channel.sendMessage(string)
}

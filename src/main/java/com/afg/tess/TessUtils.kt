package com.afg.tess

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.npcs.Ero
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message

/**
 * Created by AFlyingGrayson on 9/3/17
 */
object TessUtils {

    private val messageMap = HashMap<Channel, Message>()

    fun sendMessage(channel: Channel, string: String) {
        if (messageMap.containsKey(channel)) {
            messageMap[channel]?.delete()
            messageMap.remove(channel)
        }
        messageMap.put(channel, channel.sendMessage(string).get())
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
            if (player.playerID.length > 3)
                if (mentionTag.contains(player.playerID.substring(3)))
                    return player
        }
        return null
    }

    fun getServer(): Server {
        return Tess.api.servers.elementAt(0)
    }

    fun getChannelFromName(name: String): Channel? {
        val server = getServer()
        server?.channels?.forEach {
            if (name == it.name)
                return it
        }
        return null
    }

    fun getLocation(player: PlayerData.Player): Channel? {
        val server = getServer()
        server?.channels?.forEach {
            if (it.name == player.location)
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
        return user.id.contains("150541854029381632") || user.id.contains("161882538514579466") || user.id.contains("332739616505462784")
    }

    fun isArcLeader(user: User) : Boolean {
        val player = TessUtils.getPlayer(user.mentionTag)
        return player != null && player.arcleader == 1
    }

    fun isModerator(user: User): Boolean {
        return isAdmin(user) || user.id.contains("136936819060244480")
    }

    fun getMember(player : PlayerData.Player): User? {
        getServer().members?.forEach { member -> if (player.playerID.contains(member.id)) return member }
        return null
    }


}

val User.rpName : String
    get() = if (TessUtils.getServer().getNickname(this) != null) TessUtils.getServer().getNickname(this) else this.name


val PlayerData.Player.rpName : String
    get() = if(TessUtils.getMember(this) != null) TessUtils.getMember(this)!!.rpName else this.name

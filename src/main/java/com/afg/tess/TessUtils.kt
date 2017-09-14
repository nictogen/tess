package com.afg.tess

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.npcs.Ero
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.Server
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.entities.permissions.Role

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

    fun getRpMember(mentionTag: String): User? {
        val server = getServer()
        server?.members?.forEach { member -> if (mentionTag.contains(member.mentionTag.substring(3))) return member }
        return null
    }

    fun getPlayer(mentionTag: String): PlayerData.Player? {
        PlayerData.players.forEach { player ->
            if (player.playerID.length > 3)
                if (mentionTag.contains(player.playerID.substring(3)))
                    return player
        }
        return null
    }

    fun getRace(mentionTag: String): PlayerData.Race {
        if (mentionTag.contains("161882538514579466"))
            return PlayerData.Race.ADAPTOR
        getRpMember(mentionTag)?.getRoles(getServer())?.forEach { r ->
            try {
                return PlayerData.Race.valueOf(r.name.toUpperCase())
            } catch (e: Exception) {
            }
        }
        return PlayerData.Race.HUMAN
    }

    fun getServer(): Server? {
        return Tess.api.servers.elementAt(0)
    }

    fun getName(user: User): String {
        val server = getServer()
        return if (server?.getNickname(user) != null) server.getNickname(user) else user.name
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

    fun getRole(string: String): Role? {
        val server = getServer()
        server?.roles?.forEach {
            if (it.name == string) return it
        }
        return null
    }

    fun getCombat(channel: Channel): Combat? {
        CombatHandler.combatList.forEach { if (it.location == channel) return it }
        return null
    }

    fun getCombat(player: PlayerData.Player): Combat? {
        CombatHandler.combatList.forEach { if (it.participants.any { it is CombatHandler.Player && it.id == player.playerID }) return it }
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
        var location = LocationHandler.getLocationFromName("outside-the-boundary-area-3")
        var rank = Tess.rand.nextInt(15) + 1
        var combat = getCombat(location!!.channel)
        var add = false
        if (combat != null) {
            location = LocationHandler.getLocationFromName("outside-the-boundary-area-2")
            rank = Tess.rand.nextInt(10) + 1
            combat = getCombat(location!!.channel)
            if (combat != null) {
                location = LocationHandler.getLocationFromName("outside-the-boundary-area-1")
                rank = Tess.rand.nextInt(5) + 1
                combat = getCombat(location!!.channel)
                add = combat != null
            }
        }

        if (Ero.spawnMonster(location, null, rank, add))
            getChannelFromName("announcements")?.sendMessage("ALERT: A rank $rank Ero has appeared in ${location.channel.name}.")

    }
}
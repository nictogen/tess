package com.afg.tess.combat.combats

import com.afg.tess.TessUtils
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.npcs.Ero
import com.afg.tess.combat.npcs.Guard
import com.afg.tess.rpName
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.user.User

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class PvpCombat(location : ServerTextChannel) : Combat(location) {
    init {
        maxPlayers = 2
    }
    override fun checkForEndFight() {}

    override fun fleeBehavior() {
        fleeingParticipants.forEach {
            participants.remove(it)
        }
    }
    override fun addPlayer(user: User) : CombatHandler.CombatParticipant? {
        val name = user.rpName
        val player = TessUtils.getPlayer(user.mentionTag)
        val otherPlayer = participants.any { it is CombatHandler.Player }
        if (player != null) {
            val combatPlayer = CombatHandler.Player(name, player)
            combatPlayer.speed = player.speed
            combatPlayer.accuracy = player.accuracy
            combatPlayer.defense = player.defense
            combatPlayer.power = player.power
            combatPlayer.strength = player.strength
            combatPlayer.intelligence = player.intelligence
            combatPlayer.health = player.health*3.0

            combatPlayer.ogSpeed = player.speed
            combatPlayer.ogAccuracy = player.accuracy
            combatPlayer.ogDefense = player.defense
            combatPlayer.ogPower = player.power
            combatPlayer.ogStrength = player.strength
            combatPlayer.ogIntelligence = player.intelligence
            combatPlayer.ogHealth = player.maxHealth.toDouble()*3.0

            combatPlayer.faction = TessUtils.getFaction(player)

            combatPlayer.area = if(otherPlayer) 5 else 0

            participants.add(combatPlayer)

            participants.forEach {
                (it as? Ero)?.decideMove(this)
                (it as? Guard)?.decideMove(this)
            }
            CombatHandler.printCombatInfo(this)
            return combatPlayer
        } else return null
    }
}
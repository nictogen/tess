package com.afg.tess.combat.combats

import com.afg.tess.TessUtils
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.npcs.Ero
import com.afg.tess.combat.npcs.Guard
import de.btobastian.javacord.entities.Channel
import de.btobastian.javacord.entities.User

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class PvpCombat(location : Channel) : Combat(location) {
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
        val name = TessUtils.getName(user)
        val player = TessUtils.getPlayer(user.mentionTag)
        val otherPlayer = participants.any { it is CombatHandler.Player }
        if (player != null) {
            val combatPlayer = CombatHandler.Player(name)
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

            combatPlayer.id = user.mentionTag

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
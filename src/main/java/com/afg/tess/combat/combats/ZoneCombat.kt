package com.afg.tess.combat.combats

import com.afg.tess.Factions
import com.afg.tess.LocationHandler
import com.afg.tess.combat.CombatHandler
import org.javacord.api.entity.channel.ServerTextChannel
import java.util.*

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class ZoneCombat(location: ServerTextChannel, private var defendingFaction: Factions.Faction, private var attackingFaction: Factions.Faction) : Combat(location) {

    init {
        maxPlayers = 10
    }

    override fun checkForEndFight() {
        val attackersLeft = participants.any { !it.dead && it.faction == attackingFaction }
        val defendersLeft = participants.any { !it.dead && it.faction == defendingFaction }

        if (attackersLeft && defendersLeft) return
        else if (attackersLeft) {
            CombatHandler.combatList.remove(this)
            LocationHandler.getLocationFromName(location.name)?.combatCooldown = true
            defendingFaction.controlledLocations.remove(LocationHandler.getLocationFromName(location.name))
            addLineToInfo("The attackers have won, and ${defendingFaction.name} has been kicked out of the combat zone.")

            if (attackingFaction != Factions.factionList.filter { it.name == "factionless" }[0])
                attackingFaction.controlledLocations.put(LocationHandler.getLocationFromName(location.name)!!, ArrayList())

            participants.filter { it.faction == defendingFaction }.forEach {
                if (it is CombatHandler.Player) {
                    val player = it.player
                    player.health = it.health / 3.0
                    if (player.health < 0.1) player.health = 0.1
                }
            }
            defendingFaction.saveData()
            attackingFaction.saveData()
        } else if (defendersLeft) {
            CombatHandler.combatList.remove(this)
            LocationHandler.getLocationFromName(location.name)?.combatCooldown = true
            addLineToInfo("The defenders have won, and ${attackingFaction.name} has been kicked out of the combat zone.")
            participants.filter { it.faction == attackingFaction }.forEach {
                if (it is CombatHandler.Player) {
                    val player = it.player
                    player.health = it.health / 3.0
                    if (player.health < 0.1) player.health = 0.1
                }
            }
            defendingFaction.saveData()
            attackingFaction.saveData()
        }
    }

    override fun fleeBehavior() {
        fleeingParticipants.forEach {
            participants.remove(it)
            if (it is CombatHandler.Player) {
                val player = it.player
                player.health = it.health / 3.0
                if (player.health < 0.1) player.health = 0.1
            }
        }
    }
}

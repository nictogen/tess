package com.afg.tess.combat.npcs

import com.afg.tess.Factions
import com.afg.tess.LocationHandler
import com.afg.tess.Tess
import com.afg.tess.TessUtils
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.moves.BasicDamageMove
import com.afg.tess.combat.moves.Move

/**
 * Created by AFlyingGrayson on 9/12/17
 */
class Guard(name: String, rank: Int, startingArea: Int, faction: Factions.Faction) : CombatHandler.CombatParticipant(name) {

    companion object {
        fun spawnGuard(location: LocationHandler.Location, name: String, rank: Int, area: Int, faction: Factions.Faction) {
            val combat = TessUtils.getCombat(location.channel)
            if (combat != null) {
                combat.participants.add(Guard(name, rank, area, faction))
                CombatHandler.printCombatInfo(combat)
            }
        }
    }

    init {
        speed = 10 * rank
        accuracy = 10 * rank
        defense = 10 * rank
        power = 10 * rank
        strength = 10 * rank
        intelligence = 2
        health = 25.0 * rank.toDouble()

        this.ogSpeed = speed
        this.ogAccuracy = accuracy
        this.ogDefense = defense
        this.ogPower = power
        this.ogStrength = strength
        this.ogIntelligence = intelligence
        this.ogHealth = health
        this.faction = faction
        area = if ((0..5).contains(startingArea)) startingArea else 0
    }

    fun decideMove(combat: Combat) {
        var targets = combat.participants.filter { it.faction != faction }.filter { !it.dead }.filter { Math.abs(it.area - this.area) <= 1 }
        if (targets.isEmpty()) {
            targets = combat.participants.filter { it.faction != faction }.filter { !it.dead }.filter { Math.abs(it.area - this.area) >= 2 }
            this.nextMove = BasicDamageMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Bullet Spray")
            this.nextMove!!.targets.addAll(targets)
        } else {
            this.nextMove = BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Pistol Whip")
            this.nextMove!!.targets.add(targets[Tess.rand.nextInt(targets.size)])
        }
    }

}
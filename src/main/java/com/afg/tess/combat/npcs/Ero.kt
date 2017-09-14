package com.afg.tess.combat.npcs

import com.afg.tess.LocationHandler
import com.afg.tess.Tess
import com.afg.tess.TessUtils
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.combats.EroCombat
import com.afg.tess.combat.moves.BasicDamageMove
import com.afg.tess.combat.moves.Move
import de.btobastian.javacord.entities.message.Message

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class Ero(name: String, val rank: Int) : CombatHandler.CombatParticipant(name) {

    companion object {
        fun spawnMonster(location: LocationHandler.Location, message: Message?, rank: Int, addToExisting: Boolean): Boolean {
            var combat = TessUtils.getCombat(location.channel)
            if (combat == null) {
                combat = EroCombat(location.channel)
                message?.reply("Creating new combat.")
                CombatHandler.combatList.add(combat)
            } else if (!addToExisting) return false

            combat.participants.add(Ero("Ero", rank))

            CombatHandler.printCombatInfo(combat)
            return true
        }
    }

    init {
        val powerFocused = Tess.rand.nextBoolean()
        speed = 2 * rank + Tess.rand.nextInt(rank)
        accuracy = 2 * rank + Tess.rand.nextInt(rank)
        defense = 2 * rank + Tess.rand.nextInt(rank) + 5
        power = 2 * rank + Tess.rand.nextInt(rank) + if (powerFocused) Tess.rand.nextInt(rank) else 0
        strength = 2 * rank + Tess.rand.nextInt(rank) + if (!powerFocused) Tess.rand.nextInt(rank) else 0
        intelligence = 2
        health = 5.0 + 10.0 * rank + Tess.rand.nextInt(rank).toDouble()

        this.ogSpeed = speed
        this.ogAccuracy = accuracy
        this.ogDefense = defense
        this.ogPower = power
        this.ogStrength = strength
        this.ogIntelligence = intelligence
        this.ogHealth = health

        area = 5
    }

    fun decideMove(combat: Combat) {
        var targets = combat.participants.filter { it !is Ero }.filter { !it.dead }.filter { Math.abs(it.area - this.area) <= 1 }
        if (targets.isEmpty()) {
            targets = combat.participants.filter { it !is Ero }.filter { !it.dead }.filter { Math.abs(it.area - this.area) >= 2 }
            this.nextMove = BasicDamageMove(Move.MainStat.POWER, Move.Type.RANGE, Move.Source.POWER, "Energy Blast")
            this.nextMove!!.targets.addAll(targets)
        } else {
            this.nextMove = BasicDamageMove(Move.MainStat.STRENGTH, Move.Type.MELEE, Move.Source.PHYSICAL, "Bite")
            this.nextMove!!.targets.add(targets[Tess.rand.nextInt(targets.size)])
        }
    }

}
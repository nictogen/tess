package com.afg.tess.combat

import com.afg.tess.Factions
import com.afg.tess.PlayerData
import com.afg.tess.TessUtils
import com.afg.tess.combat.combats.Combat
import com.afg.tess.combat.moves.IOngoingMove
import com.afg.tess.combat.moves.Move
import com.afg.tess.combat.npcs.Ero
import java.util.*
import kotlin.collections.ArrayList


/**
 * Created by AFlyingGrayson on 9/7/17
 */
object CombatHandler {

    val combatList = ArrayList<Combat>()

    abstract class CombatParticipant(var name: String) {
        var speed = 0
        var accuracy = 0
        var defense = 0
        var power = 0
        var strength = 0
        var intelligence = 0
        var health = 0.0

        var ogSpeed = 0
        var ogAccuracy = 0
        var ogDefense = 0
        var ogPower = 0
        var ogStrength = 0
        var ogIntelligence = 0
        var ogHealth = 0.0

        var faction : Factions.Faction? = null

        var nextMove: Move? = null
        var ongoingEffects = ArrayList<OngoingEffect>()
        var area = 3
        var dead = false

        var killer: CombatParticipant? = null

        abstract fun getMoves() : List<Move>
    }

    class OngoingEffect(val user: CombatParticipant, var roundsLeft : Int, var move : IOngoingMove)

    class Player(name: String, var player : PlayerData.Player) : CombatParticipant(name){
        override fun getMoves(): List<Move> {
            return player.moves
        }
    }

    fun getCombatInfo(combat: Combat) : String {
        var info = ""

        val residents = LinkedList<CombatHandler.CombatParticipant>()
        residents.addAll(combat.participants)
        residents.sortBy { it.area }
        val areaResidents = java.util.HashMap<Int, String>()
        combat.participants.forEach {
            if (!it.dead) {
                if (areaResidents.containsKey(it.area))
                    areaResidents[it.area] = areaResidents[it.area] + TessUtils.numberToLetter(combat.participants.indexOf(it))
                else
                    areaResidents.put(it.area, "" + TessUtils.numberToLetter(combat.participants.indexOf(it)))
            }
        }

        var participantLocations = ""
        (0..5)
                .forEach {
                    participantLocations += if (areaResidents[it] != null) areaResidents[it] else ""
                    var progress = if (areaResidents[it] != null) 8 - areaResidents[it]!!.length else 8
                    if (it == 5)
                        progress = 0
                    (1..progress).forEach {
                        participantLocations += " "
                    }
                }
        combat.participants.forEach {
            val name = if (it is Ero) it.name + ", Rank: ${it.rank}" else it.name
            info += "\n $name (${TessUtils.numberToLetter(combat.participants.indexOf(it))}) "
            info += if(it.dead) "Dead" else "<${it.health}> hp"
            if(!it.dead && it.nextMove != null) {
                info += " Loaded: ${it.nextMove!!.name}"
                if(it.nextMove!!.targets.isNotEmpty()) {
                    info += " on: ${it.nextMove!!.targets[0].name}"
                    if(it.nextMove!!.targets.size > 1){
                        (1 until it.nextMove!!.targets.size).forEach { n ->
                            info += ", ${it.nextMove!!.targets[n].name}"
                        }

                    }
                }
            }
        }
        info += "\n"
        info += "\n#Ground Map:\n"
        info += "\n"
        info += "\n$participantLocations"
        info += "\n-0-------1------2-------3-------4-------5-"
        return "\n$info"
    }

    fun printCombatInfo(combat: Combat){
        TessUtils.sendMessage(combat.location, "```md\n${getCombatInfo(combat)}```")
    }


}
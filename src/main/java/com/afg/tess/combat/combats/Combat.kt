package com.afg.tess.combat.combats

import com.afg.tess.AlcoholHandler
import com.afg.tess.TessUtils
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.moves.Move
import com.afg.tess.combat.npcs.Ero
import com.afg.tess.combat.npcs.Guard
import com.afg.tess.rpName
import org.javacord.api.entity.channel.ServerTextChannel
import org.javacord.api.entity.user.User
import java.util.*

/**
 * Created by AFlyingGrayson on 9/13/17
 */
abstract class Combat(var location: ServerTextChannel) {
    val participants = LinkedList<CombatHandler.CombatParticipant>()
    val fleeingParticipants = ArrayList<CombatHandler.CombatParticipant>()
    var maxPlayers = 3
    var infoToPrint = "Combat Round: "
    var nextRoundSeconds = 0
    var nextRoundMinutes = 0

    fun decideMove(move: Move, participant: CombatHandler.CombatParticipant) {
        if (!participant.dead)
            participant.nextMove = move
        else return
        participants.forEach {
            if (it.nextMove == null && !it.dead) {
                CombatHandler.printCombatInfo(this)
                return
            }
        }
        proceedWithRound()
        checkForEndFight()
        TessUtils.sendMessage(location, "```md\n$infoToPrint```")
        infoToPrint = "Combat Round: "
    }

    abstract fun checkForEndFight()
    abstract fun fleeBehavior()

    private fun proceedWithRound() {
        participants.sortByDescending { it.speed }
        participants.forEach {
            if (!it.dead) {
                val name = if (it is Ero) it.name + ", Rank: ${it.rank}" else it.name
                this.addLineToInfo("\n#$name's turn: (${TessUtils.numberToLetter(participants.indexOf(it))})\n")
                if (it.nextMove != null)
                    it.nextMove?.performMove(it, this)
                else this.addLineToInfo("\n${it.name}'s move was cancelled.")
                it.ongoingEffects.forEach { e ->
                    e.roundsLeft--
                    e.move.ongoingEffect(e.user, it, this)
                }
                val effectsToRemove = it.ongoingEffects.filter { it.roundsLeft <= 0 }
                effectsToRemove.forEach { e ->
                    it.ongoingEffects.remove(e)
                    this.addLineToInfo("${e.move.getOngoingName()} wore off ${it.name}")
                }
            } else this.addLineToInfo("\n${it.name} can't take their turn because they're dead af.")
            it.nextMove?.targets?.clear()
            it.nextMove = null
        }

        fleeingParticipants.clear()
        this.addLineToInfo(CombatHandler.getCombatInfo(this))
        participants.forEach { (it as? Ero)?.decideMove(this) }
        participants.forEach { (it as? Guard)?.decideMove(this) }

        setRoundTimer()
    }

    fun addLineToInfo(line: String) {
        infoToPrint += "\n$line"
    }

    open fun addPlayer(user: User) : CombatHandler.CombatParticipant? {
        val name = user.rpName
        val player = TessUtils.getPlayer(user.mentionTag)
        if (player != null) {
            val combatPlayer = CombatHandler.Player(name, player)
            combatPlayer.speed = player.speed
            combatPlayer.accuracy = player.accuracy
            combatPlayer.defense = player.defense
            combatPlayer.power = player.power
            combatPlayer.strength = player.strength
            combatPlayer.intelligence = player.intelligence
            combatPlayer.health = player.health*3.0

            AlcoholHandler.editStats(combatPlayer, player.drunkness)

            combatPlayer.ogSpeed = player.speed
            combatPlayer.ogAccuracy = player.accuracy
            combatPlayer.ogDefense = player.defense
            combatPlayer.ogPower = player.power
            combatPlayer.ogStrength = player.strength
            combatPlayer.ogIntelligence = player.intelligence
            combatPlayer.ogHealth = player.maxHealth.toDouble()*3.0

            combatPlayer.faction = TessUtils.getFaction(player)
            participants.add(combatPlayer)

            participants.forEach {
                (it as? Ero)?.decideMove(this)
                (it as? Guard)?.decideMove(this)
            }
            CombatHandler.printCombatInfo(this)
            setRoundTimer()
            return combatPlayer
        } else return null
    }

    fun setRoundTimer(){
        val calendar = Calendar.getInstance()
        val minutes = calendar.get(Calendar.MINUTE)
        val seconds = calendar.get(Calendar.SECOND)

        nextRoundMinutes = minutes + 1
        nextRoundSeconds = seconds
    }
}

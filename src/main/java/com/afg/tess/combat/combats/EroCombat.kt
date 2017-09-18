package com.afg.tess.combat.combats

import com.afg.tess.*
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.npcs.Ero
import de.btobastian.javacord.entities.Channel

/**
 * Created by AFlyingGrayson on 9/13/17
 */
class EroCombat(location: Channel) : Combat(location) {

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

    override fun checkForEndFight() {
        var onlyMonsters = true
        var onlyPlayers = true
        participants.forEach {
            if (it is Ero && !it.dead) onlyPlayers = false
            else if (it !is Ero && !it.dead) onlyMonsters = false
        }
        if (onlyMonsters && onlyPlayers) {
            location.sendMessage("All the participants of the fight have died. Combat ending.")
            CombatHandler.combatList.remove(this)
            participants.forEach {
                if (it is CombatHandler.Player && it.dead) {
                    PlayerData.killPlayer(it.player)
                }
            }
        } else if (onlyMonsters) {
            location.sendMessage("The monsters have killed all the players. Combat ending")
            CombatHandler.combatList.remove(this)
            participants.forEach {
                if (it is CombatHandler.Player && it.dead) {
                    PlayerData.killPlayer(it.player)
                }
            }

            val location = this.location
            var combat = TessUtils.getCombat(location)
            if (combat == null) {
                combat = EroCombat(location)
                CombatHandler.combatList.add(combat)
            }

            combat.participants.addAll(this.participants.filter { it is Ero })

            CombatHandler.printCombatInfo(combat)

        } else if (onlyPlayers) {
            var endMessage = "\nThe players have killed all the monsters. Combat ending."
            if (participants.filter { it !is Ero }.any { it.dead })
                endMessage += "\nThe remaining players were able to revive the fallen!"
            CombatHandler.combatList.remove(this)
            var rewardLog = ""
            rewardLog += endMessage
            rewardLog += "\n"
            rewardLog += "\nLoot: \n"
            participants.forEach {
                if (it is Ero && it.killer != null) {
                    rewardLog += "\n#${it.killer!!.name} killed ${it.name}"
                    participants.forEach { p ->
                        if (p is CombatHandler.Player) {
                            val player = p.player
                            val member = TessUtils.getRpMember(player.playerID)
                            val name = if (member == null) player.name else TessUtils.getName(member)
                            if (!p.dead) {
                                val money = it.rank * 10 + Tess.rand.nextInt(15)
                                val conductor = player.race == PlayerData.Race.CONDUCTOR || player.race == PlayerData.Race.ADAPTOR
                                var eggDropped = if (conductor) Tess.rand.nextInt(100) <= 75 else Tess.rand.nextInt(100) <= 25

                                rewardLog += "\n$name was able to scavenge $$money worth from its body."
                                player.money += money
                                var chance = if (conductor) 40 else 20
                                var chances = it.rank
                                while (chances > 9)
                                    chances -= 9
                                for (i in 0..chances) {
                                    if (eggDropped) {
                                        when {
                                            it.rank >= 30 -> {
                                                rewardLog += if (!conductor) "\n  A conductor from the boundary sealed a dragon class eros seed for $name!"
                                                else "\n  <$name> sealed a dragon class eros seed!"
                                                ItemStack.addItemToPlayer(Item.DRAGON_EROS_SEED, player, 1)
                                            }
                                            it.rank >= 20 -> {
                                                rewardLog += if (!conductor) "\n  A conductor from the boundary sealed a large eros seed for $name!"
                                                else "\n  <$name> sealed a large eros seed!"
                                                ItemStack.addItemToPlayer(Item.LARGE_EROS_SEED, player, 1)
                                            }
                                            it.rank >= 10 -> {
                                                rewardLog += if (!conductor) "\n  A conductor from the boundary sealed a medium eros seed for $name!"
                                                else "\n  <$name> sealed a medium eros seed!"
                                                ItemStack.addItemToPlayer(Item.MEDIUM_EROS_SEED, player, 1)
                                            }
                                            else -> {
                                                rewardLog += if (!conductor) "\n  A conductor from the boundary sealed a small eros seed for $name!"
                                                else "\n  <$name> sealed a small eros seed!"
                                                ItemStack.addItemToPlayer(Item.SMALL_EROS_SEED, player, 1)
                                            }
                                        }
                                        chance -= 5

                                    }
                                    eggDropped = Tess.rand.nextInt(100) <= chance
                                }
                                rewardLog += "\n"
                            }
                        }
                    }
                } else if (it is CombatHandler.Player) {
                    val player = it.player
                    player.health = it.health / 3.0
                    if (player.health < 0.1) player.health = 0.1
                }
            }
            infoToPrint += rewardLog
        }
    }
}
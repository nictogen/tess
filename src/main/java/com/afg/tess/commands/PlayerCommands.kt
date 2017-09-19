package com.afg.tess.commands

import com.afg.tess.*
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.PvpCombat
import com.afg.tess.combat.moves.*
import com.afg.tess.combat.npcs.Ero
import com.afg.tess.combat.npcs.Guard
import com.afg.tess.commands.api.Command
import com.afg.tess.commands.api.CommandHandler

/**
 * Created by AFlyingGrayson on 9/5/17
 */
object PlayerCommands {

    @Command(aliases = arrayOf("!give", "!g"))
    fun onGive(info: CommandHandler.MessageInfo, playerToGive: PlayerData.Player, toGive: String): String {
        if (toGive[0] == '$') {
            val i = Integer.parseInt(toGive.substring(1))
            if (info.player.money >= i) {
                info.player.money -= i
                playerToGive.money += i
                info.message.reply("${info.player.rpName} gave ${playerToGive.rpName} $$i.")
            } else return "You don't have enough money for that"
        } else {
            var remove: ItemStack? = null
            info.player.items.forEach {
                if (it.itemType.name.toLowerCase() == toGive.toLowerCase()) {
                    it.amount--
                    ItemStack.addItemToPlayer(it.itemType, playerToGive, 1)
                    if (it.amount <= 0)
                        remove = it
                    if (remove != null) info.player.items.remove(remove!!)
                    return@forEach
                }
            }
        }
        return "${info.player.rpName} gave ${playerToGive.rpName} ${toGive.toLowerCase().capitalize()}."
    }

    @Command(aliases = arrayOf("!playerinfo", "!p"))
    fun onPlayerInfo(info: CommandHandler.MessageInfo): String {

        if (!info.message.isPrivateMessage && (info.message.channelReceiver != null && !info.message.channelReceiver.name.contains("spam")))
            return ""

        var string = "#${info.player.rpName} Player Info:\n"

        string += "Stats:\n"
        string += "\nHealth:         ${info.player.health}"
        string += "\nMax Health:     ${info.player.maxHealth}"
        string += "\nStrength:       ${info.player.strength}"
        string += "\nIntelligence:   ${info.player.intelligence}"
        string += "\nPower:          ${info.player.power}"
        string += "\nSpeed:          ${info.player.speed}"
        string += "\nAccuracy:       ${info.player.accuracy}"
        string += "\nDefense:        ${info.player.defense}"

        string += "\n\nMoney: $${info.player.money}"
        string += "\n\nFaction: ${TessUtils.getFaction(info.player).name}"
        string += "\n\nDrunkenness: ${info.player.drunkness}"

        if (!info.player.items.isEmpty()) {
            string += "\n\nItems:\n"
            info.player.items.forEach {
                string += "\n${it.itemType.name.toLowerCase().capitalize()}     x${it.amount}"
            }
        }

        string += "\n\nMoves: \n"
        info.player.moves.forEach {
            val s = it.saveData().split("/")
            var extraData = ""
            if (s.size > 5) {
                val s2 = s.subList(5, s.size)
                s2.forEach { extraData += "/$it" }
            }
            string += "\n<${it.name}> Move Type: ${it.getStorageName()}, Main Stat: ${it.mainStat.name.toLowerCase().capitalize()}, TargetType: ${it.type.name.toLowerCase().capitalize()}, Source: ${it.source.name.toLowerCase().capitalize()} $extraData"
        }
        return "```md\n$string```"
    }

    @Command(aliases = arrayOf("!use", "!u"))
    fun onUse(info: CommandHandler.MessageInfo, item: Item, extra: String? = null, extra2: String? = null): String {
        if (TessUtils.getCombat(info.player) != null) return "You can't use items while in combat!"
        val player = info.player
        val defaultMessage = "${player.name} Used 1 ${item.name.toLowerCase().capitalize()}\n"
        val defaultErrorMessage = "${player.rpName} doesn't have a ${item.name.toLowerCase().capitalize()}"
        return when (item.type) {
            ItemType.FOOD -> {
                if (ItemStack.removeItemFromPlayer(item, player)) {
                    val s = item.usefullness
                    player.health += s
                    if (player.health > player.maxHealth)
                        player.health = player.maxHealth.toDouble()
                    "$defaultMessage${player.rpName} healed $s health, and is now at ${player.health}/${player.maxHealth.toDouble()}."
                } else defaultErrorMessage
            }
            ItemType.MOVE_REMOVER -> {
                val combat = TessUtils.getCombat(player)
                if (combat != null) "You can't remove moves while in combat!"
                else {
                    if (player.moves.any { it.name == extra }) {
                        val move = player.moves.filter { it.name == extra }[0]
                        if (ItemStack.removeItemFromPlayer(item, player)) {
                            player.moves.remove(move)
                            "${player.rpName} removed ${move.name}"
                        } else defaultErrorMessage
                    } else "You don't have a move with that name!"
                }
            }
            ItemType.STAT_BOOSTER -> {
                if (ItemStack.removeItemFromPlayer(item, player)) {
                    var statTotal = player.strength + player.speed + player.maxHealth + player.intelligence + player.power + player.accuracy + player.defense - 100
                    if (statTotal < 1) statTotal = 1
                    val diminishingReturns = Math.ceil(statTotal.toDouble() / 50.0).toInt()
                    when (PlayerData.Stat.valueOf(extra!!.toUpperCase())) {
                        PlayerData.Stat.STRENGTH -> player.strength += item.usefullness.toInt() / diminishingReturns
                        PlayerData.Stat.SPEED -> player.speed += item.usefullness.toInt() / diminishingReturns
                        PlayerData.Stat.HEALTH -> {
                            player.maxHealth += item.usefullness.toInt() / diminishingReturns
                            player.health = player.maxHealth.toDouble() / diminishingReturns
                        }
                        PlayerData.Stat.INTELLIGENCE -> player.intelligence += item.usefullness.toInt() / diminishingReturns
                        PlayerData.Stat.POWER -> player.power += item.usefullness.toInt() / diminishingReturns
                        PlayerData.Stat.MAXHEALTH -> {
                            player.maxHealth += item.usefullness.toInt() / diminishingReturns
                            player.health = player.maxHealth.toDouble() / diminishingReturns
                        }
                        PlayerData.Stat.ACCURACY -> player.accuracy += item.usefullness.toInt() / diminishingReturns
                        PlayerData.Stat.DEFENSE -> player.defense += item.usefullness.toInt() / diminishingReturns
                    }
                    "${defaultMessage}Upgraded ${extra.toLowerCase().capitalize()} by ${item.usefullness.toInt() / diminishingReturns}."
                } else defaultErrorMessage
            }
            ItemType.GUARD -> {
                if (ItemStack.removeItemFromPlayer(item, player)) {
                    val location = LocationHandler.getLocationFromName(player.location)
                    val faction = TessUtils.getFaction(player)
                    if (location != null) {
                        val claimingFaction = TessUtils.getClaimingFaction(location)
                        if (claimingFaction == faction) {
                            if (faction.controlledLocations[location]!!.size < 5) {
                                faction.controlledLocations[location]!!.add(Factions.Guard(location, extra!!, item.usefullness.toInt(), Integer.parseInt(extra2)))
                                faction.saveData()
                                "${defaultMessage}Added a guard to ${location.channel.name}"
                            }
                        }
                    }
                }
                ""
            }
            ItemType.PHONE -> {
                if (player.items.any { it.itemType == item }) {
                    if (extra != null) {
                        val player2 = TessUtils.getPlayer(extra)
                        if (player2 != null && !player.contacts.any { it == player2.playerID }) {
                            if (player2.items.any { it.itemType.type == ItemType.PHONE }) {
                                player.contacts.add(player2.playerID)
                                "Added ${player2.rpName} as a contact!"
                            } else "${player2.rpName} doesn't have a phone!"
                        } else ""
                    } else {
                        var contacts = "```md\n"
                        var id = 1
                        player.contacts.forEach { contacts += "${id++}: <${TessUtils.getPlayer(it)?.rpName}>\n" }
                        return "$contacts```"
                    }
                } else defaultErrorMessage
            }
            ItemType.ALCOHOL -> {
                if (ItemStack.removeItemFromPlayer(item, player)) {
                    if (player.race != PlayerData.Race.CONDUCTOR && player.race != PlayerData.Race.ADAPTOR) {
                        player.drunkness += item.usefullness.toInt()
                        when (player.drunkness) {
                            in 0..10 -> "$defaultMessage${player.rpName} is feeling a bit tipsy."
                            in 10..20 -> "$defaultMessage${player.rpName} is properly buzzed."
                            in 20..30 -> "$defaultMessage${player.rpName} is drunk."
                            in 30..40 -> "$defaultMessage${player.rpName} is wasted."
                            in 40..50 -> "$defaultMessage${player.rpName} should probably stop before they die."
                            else -> {
                                PlayerData.killPlayer(player)
                                "$defaultMessage${player.rpName} died of alcohol poisoning. Sad."
                            }
                        }
                    } else "$defaultMessage${player.rpName} doesn't feel any effects."
                } else defaultErrorMessage
            }
            ItemType.ERO_BAIT -> {
                if (ItemStack.removeItemFromPlayer(item, player)) {
                    val location = LocationHandler.getLocationFromName(player.location)
                    if (location != null) location.erobait += item.usefullness.toInt()
                    defaultMessage
                } else defaultErrorMessage
            }
        }
    }

    @Command(aliases = arrayOf("!market", "!ma"))
    fun onMarket(info: CommandHandler.MessageInfo, extra: String? = null, item: Item? = null, amount: Int = 1): String {
        val location = LocationHandler.getLocationFromName(info.player.location)
        val player = info.player
        if (location != null) {
            if (extra == null) {
                var string = "```md\nMarket Selection:"
                Item.values().forEach {
                    if (it.shop == ShopType.MARKET && it.marketCost != -1.0)
                        string += "\n${it.name.toLowerCase().capitalize()}, $${it.marketCost}"
                }
                return "$string```"
            } else {
                if (extra == "buy") {
                    if (item != null) {
                        if (item.shop == ShopType.MARKET && item.marketCost != -1.0) {
                            return if (player.money >= item.marketCost * amount) {
                                if (ItemStack.addItemToPlayer(item, player, amount)) {
                                    player.money -= item.marketCost * amount
                                    player.saveData()
                                    "You bought $amount ${item.name.toLowerCase().capitalize()}"
                                } else "Your backpack is full."
                            } else "You don't have enough money to buy $amount ${item.name.toLowerCase().capitalize()}"
                        }
                        return "No item in the market by that name"
                    } else return "You have to pick an item"
                }
            }
        }
        return ""
    }

    @Command(aliases = arrayOf("!scan", "!sc"))
    fun onScan(info: CommandHandler.MessageInfo, player2: PlayerData.Player): String {
        val player = info.player
        return if (player.location == player2.location) {
            if (player.canScan == 1) {
                var scan = "```md\n"
                scan += "#Scan contents:\n"
                scan += "\nName: ${player2.rpName}"
                val raceName = when (player2.race) {
                    PlayerData.Race.HUMAN -> "Human"
                    PlayerData.Race.EROS -> "Ero"
                    PlayerData.Race.EROEX -> "Eroex"
                    PlayerData.Race.EROEXY -> "Evolved Eroex"
                    PlayerData.Race.HYBRIDEX -> "Human"
                    PlayerData.Race.EX -> "Human, Possible Error"
                    PlayerData.Race.EXY -> "Human, Possible Error"
                    PlayerData.Race.CONDUCTOR -> "No Organic Life Detected"
                    PlayerData.Race.ADAPTOR -> "Very Little Organic Life Detected"
                    PlayerData.Race.TATTOOEDHUMAN -> "Mostly Human"
                }
                scan += "\nRace: $raceName"
                scan += "\nPossible Attack Options: "
                player2.moves.forEach {
                    if (player2.moves.indexOf(it) <= 2) {
                        val s = it.saveData().split("/")
                        var extraData = ""
                        if (s.size > 5) {
                            val s2 = s.subList(5, s.size)
                            s2.forEach { extraData += "/$it" }
                        }
                        scan += "\n<${it.name}> Move Type: ${it.getStorageName()}, Main Stat: ${it.mainStat.name.toLowerCase().capitalize()}, TargetType: ${it.type.name.toLowerCase().capitalize()}, Source: ${it.source.name.toLowerCase().capitalize()} $extraData"
                    }
                }
                val stats = HashMap<PlayerData.Stat, Int>()
                var highestStat = PlayerData.Stat.STRENGTH
                var highestStatValue = 0
                stats.put(PlayerData.Stat.STRENGTH, player2.strength)
                stats.put(PlayerData.Stat.INTELLIGENCE, player2.intelligence)
                stats.put(PlayerData.Stat.POWER, player2.power)
                stats.put(PlayerData.Stat.SPEED, player2.speed)
                stats.put(PlayerData.Stat.ACCURACY, player2.accuracy)
                stats.put(PlayerData.Stat.DEFENSE, player2.defense)
                stats.forEach { t, u ->
                    if (u > highestStatValue) {
                        highestStatValue = u
                        highestStat = t
                    }
                }
                scan += "\nThey seem to specialize in ${highestStat.name.toLowerCase()}, at $highestStatValue."
                val healthy = player2.health >= player2.maxHealth / 2.0
                scan += if (healthy) "\n They appear to be healthy" else "\nThey appear to be wounded."
                info.user.sendMessage(scan + "```")
                ""
            } else "You can't scan."
        } else "You can't scan a player that isn't in the same place as you!"
    }

    @Command(aliases = arrayOf("!travel", "!t"))
    fun onTravel(info: CommandHandler.MessageInfo, location: String) {
        if (location.length > 1) {
            if (TessUtils.isAdmin(info.user))
                LocationHandler.travelToLocationAnywhere(info.user, info.player, location, info.message)
            else
                LocationHandler.travelToLocation(info.player, location, info.message)
        } else
            LocationHandler.travelToLocation(info.player, Integer.parseInt(location), info.message)
    }

    @Command(aliases = arrayOf("!quicktravel", "!qt"))
    fun onQuickTravel(info: CommandHandler.MessageInfo, locationNumber: Int? = null): String {
        val locations = LocationHandler.getQuickTravelLocations()
        return if (locationNumber == null) {
            var string = ""
            locations.forEach { string += "\n${locations.indexOf(it)}: ${it.channel.name}, ${it.quickTravelCost}" }
            "Available quick travel locations: $string"
        } else {
            val target = locations[locationNumber]
            if (info.player.money >= target.quickTravelCost) {
                LocationHandler.travelToLocationAnywhere(info.user, info.player, target.channel.name, info.message)
                info.player.money -= target.quickTravelCost
                ""
            } else {
                "You don't have enough money to travel here."
            }
        }
    }

    @Command(aliases = arrayOf("!addmove", "!am"))
    fun onAddMove(info: CommandHandler.MessageInfo, arg0: String? = null, arg1: String? = null, arg2: String? = null, arg3: String? = null, arg4: String? = null, arg5: String? = null): String {
        return if (info.player.moves.size < 4) {
            var move: Move? = null
            when (arg0) {
                "basicDamage" -> move = BasicDamageMove(Move.getMainStat(arg1!!), Move.getType(arg2!!), Move.getSource(arg3!!), arg4!!)
                "selfPowerUp" -> move = SelfPowerUpMove(Move.getSource(arg1!!), Move.getMainStat(arg2!!), Move.getSecondaryStat(arg2), arg3!!)
                "longCombatMove" -> move = LongCombatMove(Move.getSource(arg1!!), arg2!!)
                "counter" -> move = CounterMove(Move.getMainStat(arg1!!), Move.getType(arg2!!), Move.getSource(arg3!!), arg4!!)
                "selfDestruct" -> move = SelfDestructMove(Move.getMainStat(arg1!!), Move.getSource(arg3!!), arg4!!)
                "heal" -> move = HealOtherMove(Move.getMainStat(arg1!!), Move.getSource(arg2!!), arg3!!)
                "switch" -> move = SwitchMove(Move.getSource(arg1!!), arg2!!)
                "stunningDamage" -> move = StunningDamageMove(Move.getMainStat(arg1!!), Move.getType(arg2!!), Move.getSource(arg3!!), arg4!!)
                "absorb" -> move = AbsorbMove(Move.getMainStat(arg1!!), Move.getType(arg2!!), Move.getSource(arg3!!), arg4!!)
                "accurateDamage" -> move = AccurateDamageMove(Move.getMainStat(arg1!!), Move.getType(arg2!!), Move.getSource(arg3!!), arg4!!)
                "damageOverTime" -> move = DamageOverTimeMove(Move.getMainStat(arg1!!), Move.getType(arg2!!), Move.getSource(arg3!!), arg4!!)
                "randomExtremeSelfPowerUp" -> move = RandomExtremeSelfPowerUpMove(Move.getSource(arg1!!), arg2!!)
                "healOverTime" -> move = HealOverTimeMove(Move.getMainStat(arg1!!), Move.getType(arg2!!), Move.getSource(arg3!!), arg4!!)
                "extremeSelfPowerUp" -> move = ExtremeSelfPowerUpMove(Move.getSource(arg1!!), Move.getMainStat(arg2!!), Move.getSecondaryStat(arg2), Move.MainStat.valueOf(arg3!!.toUpperCase()), Move.SecondaryStat.valueOf(arg4!!.toUpperCase()), arg5!!)
                "debuff" -> move = DebuffMove(Move.getType(arg1!!), Move.getSource(arg2!!), Move.getMainStat(arg3!!), Move.getSecondaryStat(arg3), arg4!!)
                "powerSteal" -> move = PowerStealMove(Move.getAttackType(arg1!!), Move.getSource(arg2!!), Move.getMainStat(arg3!!), Move.getSource(arg4!!), arg5!!)
                "buffNearby" -> move = BuffNearbyMove(Move.getSource(arg1!!), Move.getMainStat(arg2!!), Move.getSecondaryStat(arg2), arg3!!)
            }
            if (move != null) {
                info.player.moves.add(move)
                info.player.saveData()
                "Added ${move.name} to ${info.player.rpName}."
            } else "Incorrect arguments"
        } else "You already have four moves! Remove one with !removeMove <move name>"
    }

    @Command(aliases = arrayOf("!usemove", "!um"))
    fun onUseMove(info: CommandHandler.MessageInfo, arg0: String, target: String? = null): String {
        val combat = TessUtils.getCombat(info.player) ?: return "You aren't in a combat."
        val player = info.player
        val move: Move =
                if (arg0.length == 1)
                    player.moves[Integer.parseInt(arg0) - 1]
                else if (player.moves.any { it is PowerStealMove } && arg0.length == 2 && arg0[0] == 's')
                    (player.moves.filter { it is PowerStealMove }[0] as PowerStealMove).movesStolen[Integer.parseInt(arg0[1].toString()) - 1]
                else
                    player.moves.filter { it.name.toLowerCase() == arg0.toLowerCase() }[0]

        move.targets.clear()

        val participant = combat.participants.filter { it is CombatHandler.Player && it.player == info.player }[0]

        when (move) {
            is SelfPowerUpMove -> {
                combat.decideMove(move, participant)
            }
            is SelfDestructMove -> {
                move.targets.addAll(combat.participants)
                combat.decideMove(move, participant)
            }
            is LongCombatMove -> {
                move.target = Integer.parseInt(arg0)
                combat.decideMove(move, participant)
            }
            is ExtremeSelfPowerUpMove -> {
                combat.decideMove(move, participant)
            }
            is RandomExtremeSelfPowerUpMove -> {
                combat.decideMove(move, participant)
            }
            is BuffNearbyMove -> {
                move.targets.addAll(combat.participants.filter { it.area == participant.area })
                combat.decideMove(move, participant)
            }
            else -> if (target != null) {
                if (target.length == 1) {
                    move.targets.add(combat.participants[TessUtils.letterToNumber(target[0])])
                    combat.decideMove(move, participant)
                } else {
                    move.targets.add(combat.participants.filter { it.name == target }[0])
                    combat.decideMove(move, participant)
                }
            }
        }
        return ""
    }

    @Command(aliases = arrayOf("!combat", "!c"))
    fun onCombat(info: CommandHandler.MessageInfo): String {
        if (TessUtils.getCombat(info.player) != null)
            return "You're already fighting."
        val location = TessUtils.getLocation(info.player)
        if (location != null) {
            val combat = TessUtils.getCombat(location)
            if (combat != null) {
                if (combat.participants.filter { it !is Ero && it !is Guard }.size < combat.maxPlayers) {
                    combat.addPlayer(info.user)
                } else return "There are already ${combat.maxPlayers} players in this combat."
            }
        }
        return ""
    }

    @Command(aliases = arrayOf("!combatmove", "!cm"))
    fun onCombatMove(info: CommandHandler.MessageInfo, area: Int): String {
        val combat = TessUtils.getCombat(info.player)
        if (combat != null)
            combat.decideMove(CombatMove(area), combat.participants.filter { it is CombatHandler.Player && it.player == info.player }[0])
        else return "You aren't in a combat."
        return ""
    }

    @Command(aliases = arrayOf("!fight", "!f"))
    fun onFight(info: CommandHandler.MessageInfo): String {
        if (!info.player.location.contains("arena"))
            return "You can only pvp in an arena."
        val location = TessUtils.getLocation(info.player)
        if (location != null) {
            var combat = TessUtils.getCombat(location)
            if (combat == null) {
                combat = PvpCombat(location)
                CombatHandler.combatList.add(combat)
                return "Creating new combat with ${combat.maxPlayers} players maximum."
            }
        }
        return ""
    }

    @Command(aliases = arrayOf("!leavecombat", "!lc"))
    fun onLeaveCombat(info: CommandHandler.MessageInfo): String {
        val combat = TessUtils.getCombat(info.player)
        if (combat is PvpCombat) {
            val participant = combat.participants.filter { it is CombatHandler.Player && it.player == info.player }[0]
            combat.participants.remove(participant)
            var s = "${info.player.rpName} left the combat."
            if (combat.participants.isEmpty()) {
                CombatHandler.combatList.remove(combat)
                s += "\nNo players left in combat, ending combat."
            }
            return s
        }
        return ""
    }

    @Command(aliases = arrayOf("!flee", "!ff"))
    fun onFlee(info: CommandHandler.MessageInfo): String {
        val combat = TessUtils.getCombat(info.player)
        if (combat != null)
            combat.decideMove(FleeMove(), combat.participants.filter { it is CombatHandler.Player && it.player == info.player }[0])
        else return "You aren't in a combat."
        return ""
    }

    @Command(aliases = arrayOf("!addtofaction"))
    fun onAddToFaction(info: CommandHandler.MessageInfo, player2: PlayerData.Player, admin: Boolean): String {
        val player = info.player
        if (Factions.factionList.any { it.admins.contains(player2) } || Factions.factionList.any { it.grunts.contains(player2) })
            return "Player is already in a faction"
        val faction = Factions.factionList.filter { it.admins.contains(player) }[0]
        if (admin) faction.admins.add(player2) else faction.grunts.add(player2)
        faction.saveData()
        return "Added player to faction as an " + if (admin) faction.adminName else faction.gruntName
    }

    @Command(aliases = arrayOf("!leavefaction"))
    fun onLeaveFaction(info: CommandHandler.MessageInfo): String {
        Factions.factionList.forEach {
            it.admins.remove(info.player)
            it.grunts.remove(info.player)
            it.saveData()
        }
        return "Left all factions"
    }

    @Command(aliases = arrayOf("!claimlocation"))
    fun onClaimLocation(info: CommandHandler.MessageInfo): String {
        val location = LocationHandler.getLocationFromName(info.player.location)!!
        val faction = TessUtils.getFaction(info.player)
        val claimingFaction = TessUtils.getClaimingFaction(location)
        return if (claimingFaction == null) {
            faction.controlledLocations.put(location, ArrayList())
            faction.saveData()
            "Claimed location for ${faction.name}."
        } else "Another faction already controls this location."
    }

    @Command(aliases = arrayOf("!attack", "!a"))
    fun onAttack(info: CommandHandler.MessageInfo, location: String) {
        if (location.length > 1)
            LocationHandler.attackLocation(info.player, location, info.message)
        else
            LocationHandler.attackLocation(info.player, Integer.parseInt(location), info.message)
    }

    @Command(aliases = arrayOf("!factioninfo"))
    fun onFactionInfo(info: CommandHandler.MessageInfo): String {
        val location = LocationHandler.getLocationFromName(info.player.location)!!
        val claimingFaction = TessUtils.getClaimingFaction(location)!!
        var string = "```\n${claimingFaction.name}"
        string += "\nGuards:\n"
        claimingFaction.controlledLocations[location]?.forEach { string += "${it.name}: Lvl ${it.rank}\n" }
        return "$string```"
    }

    @Command(aliases = arrayOf("!text"))
    fun onText(info: CommandHandler.MessageInfo, contactNumber: Int): String {
        return if (info.player.items.any { it.itemType.type == ItemType.PHONE }) {
            val player2 = TessUtils.getPlayer(info.player.contacts[contactNumber - 1])!!
            if (player2.items.any { it.itemType.type == ItemType.PHONE }) {
                var messageContent = ""
                info.message.content.split(" ").subList(2, info.message.content.split(" ").size).forEach { messageContent += "$it " }
                TessUtils.getLocation(player2)?.sendMessage("Text to ${player2.name} from ${info.player.name}: $messageContent")
                return "Texted ${player2.name}."
            } else "That player doesn't have a phone!"
        } else "You don't have a phone!"
    }

    @Command(aliases = arrayOf("!order", "!o"))
    fun onOrder(info: CommandHandler.MessageInfo, server: PlayerData.Player? = null, item: Item? = null, amount : Int = 1): String {
        val location = LocationHandler.getLocationFromName(info.player.location)!!
        return if (location.bar) {
            if (server != null) {
                if (server.bartender == 1) {
                    if (item!!.shop == ShopType.BAR && item.marketCost != -1.0) {
                        if (info.player.money >= item.marketCost * amount) {
                            if (ItemStack.addItemToPlayer(item, info.player, amount)) {
                                info.player.money -= item.marketCost * amount
                                server.money += (item.marketCost * amount) / 2
                                info.player.saveData()
                                "You bought $amount ${item.name.toLowerCase().capitalize()} from ${server.rpName}"
                            } else "Your backpack is full."
                        } else "You don't have enough money to buy $amount ${item.name.toLowerCase().capitalize()} from ${server.rpName}"
                    } else "That item is not sold at this bar."
                } else "That player is not a bartender."
            } else {
                var string = "Bar Selection:"
                Item.values().forEach {
                    if (it.shop == ShopType.BAR && it.marketCost != -1.0)
                        string += "\n${it.name.toLowerCase().capitalize()}, $${it.marketCost}"
                }
                return string
            }
        } else "This location is not a bar."
    }

}

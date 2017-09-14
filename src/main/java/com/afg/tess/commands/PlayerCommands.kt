package com.afg.tess.commands

import com.afg.tess.*
import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.PvpCombat
import com.afg.tess.combat.moves.*
import com.afg.tess.combat.npcs.Ero
import com.afg.tess.combat.npcs.Guard
import de.btobastian.javacord.entities.message.Message
import de.btobastian.sdcf4j.Command
import de.btobastian.sdcf4j.CommandExecutor

/**
 * Created by AFlyingGrayson on 9/5/17
 */
object PlayerCommands : CommandExecutor {

    @Command(aliases = arrayOf("!give", "!g"), description = "Gives money/items to another player")
    fun onGive(message: Message, args: Array<String>) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            if (args.size == 2) {
                val player2 = TessUtils.getPlayer(args[0])
                if (player2 != null) {
                    if (args[1][0] == '$') {
                        val i = Integer.parseInt(args[1].substring(1))
                        if (player.money >= i) {
                            player.money -= i
                            player2.money += i
                            message.reply("You gave ${player2.name} $$i.")
                        } else message.reply("You don't have enough money for that")
                        return@onGive
                    } else {
                        var remove: ItemStack? = null
                        player.items.forEach {
                            if (it.itemType.name.toLowerCase() == args[1].toLowerCase()) {
                                it.amount--
                                ItemStack.addItemToPlayer(it.itemType, player2, 1)
                                if (it.amount <= 0)
                                    remove = it
                                message.reply("You gave ${player2.name} 1 ${args[1].toLowerCase().capitalize()}.")
                                return@forEach
                            }
                        }
                        if (remove != null) player.items.remove(remove!!)
                        return@onGive
                    }
                } else message.reply("That person isn't a player.")
            }
        } else message.reply("You aren't a player.")
    }

    @Command(aliases = arrayOf("!playerinfo", "!p"), description = "Gets your player's info")
    fun onPlayerInfo(message: Message, args: Array<String>) {
        message.delete()
        if (!message.isPrivateMessage && (message.channelReceiver == null || !message.channelReceiver.name.contains("spam")))
            return

        var name = TessUtils.getName(message.author)
        var player = TessUtils.getPlayer(message.author.mentionTag)
        if (args.isNotEmpty()) {
            if (message.author.getRoles(TessUtils.getServer()).contains(TessUtils.getRole("Perms"))) {
                val member = TessUtils.getRpMember(args[0])
                if (member != null) {
                    name = TessUtils.getName(member)
                    player = TessUtils.getPlayer(args[0])
                }
            }
        }
        if (player != null) {
            var string = "#$name Player Info:\n"

            string += "Stats:\n"
            string += "\nStrength:       ${player.strength}"
            string += "\nSpeed:          ${player.speed}"
            string += "\nMax Health:     ${player.maxHealth}"
            string += "\nHealth:         ${player.health}"
            string += "\nIntelligence:   ${player.intelligence}"
            string += "\nPower:          ${player.power}"
            string += "\nAccuracy:       ${player.accuracy}"
            string += "\nDefense:        ${player.defense}"

            string += "\n\nMoney: $${player.money}"
            string += "\n\nFaction: ${TessUtils.getFaction(player).name}"

            if (!player.items.isEmpty()) {
                string += "\n\nItems:\n"
                player.items.forEach {
                    string += "\n${it.itemType.name.toLowerCase().capitalize()}     x${it.amount}"
                }
            }

            string += "\n\nMoves: \n"
            player.moves.forEach {
                val s = it.saveData().split("/")
                var extraData = ""
                if (s.size > 5) {
                    val s2 = s.subList(5, s.size)
                    s2.forEach { extraData += "/$it" }
                }
                string += "\n<${it.name}> Move Type: ${it.getStorageName()}, Main Stat: ${it.mainStat.name.toLowerCase().capitalize()}, TargetType: ${it.type.name.toLowerCase().capitalize()}, Source: ${it.source.name.toLowerCase().capitalize()} $extraData"
            }
            message.reply("```md\n$string```")
        } else message.reply("You aren't a player.")

    }

    @Command(aliases = arrayOf("!use", "!u"), description = "Use an item from your inventory")
    fun onUse(message: Message, args: Array<String>) {
        message.delete()
        val item = if (!args.isEmpty()) args[0] else ""
        val name = TessUtils.getName(message.author)
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            val combat = TessUtils.getCombat(player)
            if (combat != null) {
                message.reply("You can't use items while in combat!")
                return@onUse
            }
            var remove: ItemStack? = null
            player.items.forEach {
                if (it.itemType.name.toLowerCase() == item.toLowerCase()) {
                    when (it.itemType.type) {
                        ItemType.FOOD -> {
                            val s = it.itemType.usefullness
                            player.health += s
                            if (player.health > player.maxHealth) {
                                player.health = player.maxHealth.toDouble()
                            }
                            message.reply("$name healed $s health, and is now at ${player.health}/${player.maxHealth.toDouble()}.")
                        }
                        ItemType.MOVE_REMOVER -> {
                            message.reply("Use that item with the !remove command.")
                            return@forEach
                        }
                        ItemType.STAT_BOOSTER -> {
                            try {
                                val statTotal = player.strength + player.speed + player.maxHealth + player.intelligence + player.power + player.accuracy + player.defense
                                val diminishingReturns = Math.ceil(statTotal.toDouble() / 150.0).toInt()
                                when (PlayerData.Stat.valueOf(args[1].toUpperCase())) {
                                    PlayerData.Stat.STRENGTH -> player.strength += it.itemType.usefullness.toInt() / diminishingReturns
                                    PlayerData.Stat.SPEED -> player.speed += it.itemType.usefullness.toInt() / diminishingReturns
                                    PlayerData.Stat.HEALTH -> {
                                        player.maxHealth += it.itemType.usefullness.toInt() / diminishingReturns
                                        player.health = player.maxHealth.toDouble() / diminishingReturns
                                    }
                                    PlayerData.Stat.INTELLIGENCE -> player.intelligence += it.itemType.usefullness.toInt() / diminishingReturns
                                    PlayerData.Stat.POWER -> player.power += it.itemType.usefullness.toInt() / diminishingReturns
                                    PlayerData.Stat.MAXHEALTH -> {
                                        player.maxHealth += it.itemType.usefullness.toInt() / diminishingReturns
                                        player.health = player.maxHealth.toDouble() / diminishingReturns
                                    }
                                    PlayerData.Stat.ACCURACY -> player.accuracy += it.itemType.usefullness.toInt() / diminishingReturns
                                    PlayerData.Stat.DEFENSE -> player.defense += it.itemType.usefullness.toInt() / diminishingReturns
                                }
                                message.reply("Upgraded ${args[1].toLowerCase().capitalize()} by ${it.itemType.usefullness.toInt() / diminishingReturns}.")
                            } catch (e: Exception) {
                                message.reply("incorrect arguments")
                                return@forEach
                            }
                        }
                        ItemType.GUARD -> {
                            message.reply("Use that item with the !addguard command.")
                            return@forEach
                        }
                    }
                    it.amount -= 1
                    if (it.amount <= 0)
                        remove = it

                    message.reply("Used 1 ${it.itemType.name.toLowerCase().capitalize()}")
                    return@forEach
                }
            }
            if (remove != null)
                player.items.remove(remove!!)
            player.saveData()
            return@onUse
        } else message.reply("You aren't a player.")
    }

    @Command(aliases = arrayOf("!market", "!ma"), description = "Get the market's selection")
    fun onMarket(message: Message, args: Array<String>) {
        message.delete()
        val channel = message.channelReceiver
        if (channel != null) {
            if (channel.name == "tess-testing" || channel.name == "city-absol-shop" || channel.name == "city-absol-shopping-mall" || channel.name == "sky-city-shopping-centre") {
                val player = TessUtils.getPlayer(message.author.mentionTag)
                if (player != null) {
                    if (player.location == channel.name) {
                        if (args.isEmpty()) {
                            var string = "Market Selection:"
                            Item.values().forEach {
                                if (it.marketCost != -1.0)
                                    string += "\n${it.name.toLowerCase().capitalize()}, $${it.marketCost}"
                            }
                            message.reply(string)
                            return@onMarket
                        } else {
                            if (args[0] == "buy") {
                                if (args.size > 1) {
                                    Item.values().forEach {
                                        if (it.marketCost != -1.0 && it.name.toLowerCase() == args[1].toLowerCase()) {
                                            val amount = if (args.size > 2) Integer.parseInt(args[2]) else 1
                                            if (player.money >= it.marketCost * amount) {
                                                if (ItemStack.addItemToPlayer(it, player, amount)) {
                                                    player.money -= it.marketCost * amount
                                                    player.saveData()
                                                    message.reply("You bought $amount ${it.name.toLowerCase().capitalize()}")
                                                } else message.reply("Your backpack is full.")
                                                return@onMarket
                                            } else {
                                                message.reply("You don't have enough money to buy $amount ${it.name}")
                                                return@onMarket
                                            }
                                        }
                                    }
                                    message.reply("No item in the market by that name")
                                    return@onMarket
                                } else {
                                    message.reply("You have to pick an item")
                                    return@onMarket
                                }
                            }
                        }
                    } else message.reply("You aren't in this area in order to buy things!")
                } else message.reply("You aren't a player.")
            }
        }
    }

    @Command(aliases = arrayOf("!scan", "!sc"), description = "Scans another player")
    fun onScan(message: Message, args: Array<String>) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            if (args.size == 1) {
                val player2 = TessUtils.getPlayer(args[0])
                if (player2 != null) {
                    if (player.location == player2.location) {
                        if (player.canScan == 1) {
                            var scan = "```md\n"
                            scan += "#Scan contents:\n"
                            scan += "\nName: ${player2.name}"
                            val race = TessUtils.getRace(player2.playerID)
                            val raceName = when (race) {
                                PlayerData.Race.HUMAN -> "Human"
                                PlayerData.Race.EROS -> "Ero"
                                PlayerData.Race.EROEX -> "Eroex"
                                PlayerData.Race.EROEXY -> "Evolved Eroex"
                                PlayerData.Race.HYBRIDEX -> "Human"
                                PlayerData.Race.EX -> "Human, Possible Error"
                                PlayerData.Race.EXY -> "Human, Possible Error"
                                PlayerData.Race.CONDUCTOR -> "No Organic Life Detected"
                                PlayerData.Race.ADAPTOR -> "Very Little Organic Life Detected"
                            }
                            scan += "\nRace: $raceName}"
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
                            message.author.sendMessage(scan + "```")
                        } else message.reply("You can't scan.")
                    } else message.reply("You can't scan a player that isn't in the same place as you!")
                } else message.reply("That person isn't a player.")
            }
        } else message.reply("You aren't a player.")
    }

    @Command(aliases = arrayOf("!travel", "!t"), description = "Travel to a location")
    fun onTravel(message: Message, args: Array<String>) {
        message.delete()
        if (!args.isEmpty()) {
            val player = TessUtils.getPlayer(message.author.mentionTag)
            if (player != null) {

                if (args[0].length > 1) {
                    if (message.author.getRoles(TessUtils.getServer()).contains(TessUtils.getRole("Admin")))
                        LocationHandler.travelToLocationAnywhere(message.author, player, args[0], message)
                    else
                        LocationHandler.travelToLocation(player, args[0], message)
                } else
                    LocationHandler.travelToLocation(player, Integer.parseInt(args[0]), message)
            } else message.reply("You aren't a player.")
        }
    }

    @Command(aliases = arrayOf("!quicktravel", "!qt"), description = "Quick travels to a location")
    fun onQuickTravel(message: Message, args: Array<String>) {
        message.delete()
        val locations = LocationHandler.getQuickTravelLocations()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            if (args.isEmpty()) {
                var string = ""
                locations.forEach { string += "\n${locations.indexOf(it)}: ${it.channel.name}, ${it.quickTravelCost}" }
                message.reply("Available quick travel locations: $string")
            } else {
                var target: LocationHandler.Location? = null
                if (args[0].length == 1) {
                    try {
                        val index = Integer.parseInt(args[0])
                        target = locations[index]
                    } catch (e: Exception) {
                        message.reply("invalid arguments")
                    }
                } else {
                    target = LocationHandler.getLocationFromName(args[0])
                }
                if (target != null) {
                    if (player.money >= target.quickTravelCost) {
                        LocationHandler.travelToLocationAnywhere(message.author, player, target.channel.name, message)
                        player.money -= target.quickTravelCost
                    } else {
                        message.reply("You don't have enough money to travel here.")
                    }
                }
            }
        } else message.reply("You aren't a player.")
    }

    @Command(aliases = arrayOf("!detention"), description = "Transports a player to detention")
    fun onDetention(message: Message, args: Array<String>) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (message.author.getRoles(TessUtils.getServer()).contains(TessUtils.getRole("Teacher"))) {
            if (player != null) {
                if (args.size == 1) {
                    val player2 = TessUtils.getPlayer(args[0])
                    val user = TessUtils.getRpMember(args[0])
                    if (player2 != null) {
                        if (player2.location == "detention")
                            LocationHandler.travelToLocationAnywhere(user!!, player2, "sky-city-highschool", message)
                        else
                            LocationHandler.travelToLocationAnywhere(user!!, player2, "detention", message)
                    } else message.reply("That person isn't a player.")
                }
            } else message.reply("You aren't a player.")
        }
    }

    @Command(aliases = arrayOf("!addmove", "!am"), description = "Adds a move to your player")
    fun onAddMove(message: Message, args: Array<String>) {
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            if (player.moves.size < 4) {
                try {
                    var move: Move? = null
                    val mainStat = Move.MainStat.valueOf(args[1].toUpperCase())
                    val type = Move.Type.valueOf(args[2].toUpperCase())
                    val source = Move.Source.valueOf(args[3].toUpperCase())
                    val name = args[4]
                    when (args[0]) {
                        "basicDamage" -> move = BasicDamageMove(mainStat, type, source, name)
                        "selfPowerUp" -> move = SelfPowerUpMove(mainStat, source, name, args[5])
                        "longCombatMove" -> move = LongCombatMove(source, name)
                        "counter" -> move = CounterMove(mainStat, type, source, name)
                        "selfDestruct" -> move = SelfDestructMove(mainStat, Move.Type.RANGE, source, name)
                        "healOther" -> move = HealOtherMove(mainStat, source, name)
                    }
                    if (move != null) {
                        message.delete()
                        player.moves.add(move)
                        player.saveData()
                        message.reply("Added $name to ${player.name}.")
                    }
                } catch (e: Exception) {
                    message.reply("Incorrect arguments")
                }

            } else message.reply("You already have four moves! Remove one with !removeMove <move name>")
        }
    }

    @Command(aliases = arrayOf("!removemove", "!rm"), description = "Removes a move from your player.")
    fun onRemoveMove(message: Message, args: Array<String>) {
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            val location = TessUtils.getLocation(player)
            if (location != null) {
                val combat = TessUtils.getCombat(location)
                combat?.participants?.forEach {
                    if (it.name == player.name) {
                        message.reply("You can't remove moves while in combat!")
                        return@onRemoveMove
                    }
                }
            }
            var remove: ItemStack? = null
            var usedItem = false
            player.items.forEach {
                if (it.itemType.type == ItemType.MOVE_REMOVER) {
                    usedItem = true
                }
            }

            if (!usedItem) {
                message.reply("You need a memory adjuster to remove that move.")
                return
            }

            var toRemove: Move? = null
            player.moves.forEach {
                if (it.name == args[0])
                    toRemove = it
            }

            if (toRemove != null) {
                message.delete()
                player.moves.remove(toRemove!!)
                message.reply("Removed move: ${toRemove!!.name}.")
                player.items.forEach {
                    if (it.itemType.type == ItemType.MOVE_REMOVER) {
                        message.reply("Used 1 ${it.itemType.name}")
                        it.amount -= 1
                        if (it.amount <= 0)
                            remove = it
                        usedItem = true
                    }
                }
                if (remove != null)
                    player.items.remove(remove!!)

                player.saveData()
            } else message.reply("You don't have a move with that name!")
        }
    }

    @Command(aliases = arrayOf("!usemove", "!um"), description = "Uses a move your player has")
    fun onUseMove(message: Message, args: Array<String>) {
        val player = TessUtils.getPlayer(message.author.mentionTag)
        val name = TessUtils.getName(message.author)
        var selectedMove: Move? = null


        if (args.isNotEmpty() && player != null) {
            if (args[0].length == 1) {
                try {
                    var index = Integer.parseInt(args[0]) - 1
                    if (index < 0) index = 0
                    if (player.moves.size > index)
                        selectedMove = player.moves[index]
                } catch (e: Exception) {
                }
            }
            if (selectedMove == null) {
                player.moves.forEach { m ->
                    if (m.name.toLowerCase() == args[0].toLowerCase()) {
                        selectedMove = m
                    }
                }
            }
        }

        try {
            if (player != null && selectedMove != null) {
                val move = selectedMove!!
                move.targets.clear()
                val location = TessUtils.getLocation(player)
                if (location != null) {
                    val combat = TessUtils.getCombat(location)
                    if (combat != null && combat == TessUtils.getCombat(player)) {
                        combat.participants.forEach { participant ->
                            if (participant.name == name) {
                                when (move) {
                                    is SelfPowerUpMove -> {
                                        combat.decideMove(move, participant, message)
                                        return@onUseMove
                                    }
                                    is SelfDestructMove -> {
                                        move.targets.addAll(combat.participants)
                                        combat.decideMove(move, participant, message)
                                        return@onUseMove
                                    }
                                    is LongCombatMove -> {
                                        move.target = Integer.parseInt(args[1])
                                        combat.decideMove(move, participant, message)
                                        return@onUseMove
                                    }
                                    else -> {
                                        if (args.size == 2) {
                                            if (args[1].length == 1) {
                                                val index = TessUtils.letterToNumber(args[1][0])
                                                if (combat.participants.size > index) {
                                                    val target = combat.participants[index]
                                                    move.targets.add(target)
                                                    combat.decideMove(move, participant, message)
                                                    return@onUseMove
                                                }
                                                message.reply("Couldn't find this target.")
                                            } else {
                                                combat.participants.forEach { target ->
                                                    if (args[1].toLowerCase() == target.name.toLowerCase()) {
                                                        move.targets.add(target)
                                                        combat.decideMove(move, participant, message)
                                                        return@onUseMove
                                                    }
                                                }
                                                message.reply("Couldn't find this target.")
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (e: Exception) {
        }
    }

    @Command(aliases = arrayOf("!combat", "!c"), description = "Joins a combat")
    fun onCombat(message: Message) {
        val player = TessUtils.getPlayer(message.author.mentionTag)

        if (player != null) {
            if (TessUtils.getCombat(player) != null) {
                message.reply("You're already fighting.")
                return
            }
            val location = TessUtils.getLocation(player)
            if (location != null) {
                val combat = TessUtils.getCombat(location)
                if (combat != null) {
                    message.delete()
                    if (combat.participants.filter { it !is Ero && it !is Guard }.size < combat.maxPlayers) {
                        combat.addPlayer(message.author)
                    } else message.reply("There are already ${combat.maxPlayers} grunts in this combat.")
                }
            }
        } else message.reply("You aren't a player.")
    }

    @Command(aliases = arrayOf("!combatmove", "!cm"), description = "Moves in combat")
    fun onCombatMove(message: Message, args: Array<String>) {
        val player = TessUtils.getPlayer(message.author.mentionTag)
        val name = TessUtils.getName(message.author)
        if (args.size == 1 && player != null) {
            val combat = TessUtils.getCombat(player)
            combat?.participants?.forEach { participant ->
                if (participant.name == name) {
                    combat.decideMove(CombatMove(Integer.parseInt(args[0])), participant, message)
                    return@onCombatMove
                }
            }

        }
    }

    @Command(aliases = arrayOf("!endcombat", "!ec"), description = "Ends a Combat")
    fun onEndCombat(message: Message) {
        message.delete()
        message.author.getRoles(message.channelReceiver.server).forEach { role ->
            if (role.name == "Perms") {
                if (message.channelReceiver != null) {
                    val combat = TessUtils.getCombat(message.channelReceiver)
                    if (combat != null) {
                        CombatHandler.combatList.remove(combat)
                        message.reply("Ended Combat")
                    }
                }
            }
        }
    }

    @Command(aliases = arrayOf("!fight", "!f"), description = "Starts a Fight")
    fun onFight(message: Message) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            if (!player.location.contains("arena"))
                return
            val location = TessUtils.getLocation(player)
            if (location != null) {
                var combat = TessUtils.getCombat(location)
                if (combat == null) {
                    combat = PvpCombat(location)
                    message.reply("Creating new combat with ${combat.maxPlayers} players maximum.")
                    CombatHandler.combatList.add(combat)
                }
            }
        }
    }

    @Command(aliases = arrayOf("!leavecombat", "!lc"), description = "Leaves a pvp combat")
    fun onLeaveCombat(message: Message) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        val name = TessUtils.getName(message.author)

        if (player != null) {
            val combat = TessUtils.getCombat(player)
            if (combat != null && combat is PvpCombat) {
                var remove: CombatHandler.CombatParticipant? = null
                combat.participants.forEach {
                    if (it is CombatHandler.Player && it.id == player.playerID)
                        remove = it
                }
                if (remove != null) {
                    combat.participants.remove(remove!!)
                    message.reply("$name left the combat.")
                    if (combat.participants.isEmpty()) {
                        CombatHandler.combatList.remove(combat)
                        message.reply("No players left in combat, ending combat.")
                    }
                }

            }
        }
    }

    @Command(aliases = arrayOf("!flee", "!ff"), description = "Flees from combat")
    fun onFlee(message: Message) {
        val player = TessUtils.getPlayer(message.author.mentionTag)
        val name = TessUtils.getName(message.author)
        if (player != null) {
            val combat = TessUtils.getCombat(player)
            combat?.participants?.forEach { participant ->
                if (participant.name == name) {
                    combat.decideMove(FleeMove(), participant, message)
                    return@onFlee
                }
            }

        }
    }

    @Command(aliases = arrayOf("!addtofaction"), description = "Adds a new player to a faction")
    fun onAddToFaction(message: Message, args: Array<String>) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            try {
                val player2 = TessUtils.getPlayer(args[0])
                if (Factions.factionList.any { it.admins.contains(player2) } || Factions.factionList.any { it.grunts.contains(player2) }) {
                    message.reply("Player is already in a faction")
                    return
                }
                val admin = args[1] == "admin"
                val faction = Factions.factionList.filter { it.admins.contains(player) }[0]
                if (admin) faction.admins.add(player2!!) else faction.grunts.add(player2!!)
                faction.saveData()
                message.reply("Added player to faction as an " + if(admin) faction.adminName else faction.gruntName)
            } catch (e: Exception) {
            }
        }
    }

    @Command(aliases = arrayOf("!leavefaction"), description = "Leaves your faction")
    fun onLeaveFaction(message: Message) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            Factions.factionList.forEach {
                if (it.admins.contains(player))
                    it.admins.remove(player)
                if (it.grunts.contains(player))
                    it.grunts.remove(player)
                it.saveData()
            }
            message.reply("Left all factions")
        }
    }

    @Command(aliases = arrayOf("!claimlocation"), description = "Claims a location")
    fun onClaimLocation(message: Message) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            val location = LocationHandler.getLocationFromName(player.location)
            val faction = TessUtils.getFaction(player)
            if (location != null) {
                val claimingFaction = TessUtils.getClaimingFaction(location)
                if (claimingFaction == null) {
                    faction.controlledLocations.put(location, ArrayList())
                    message.reply("Claimed location for ${faction.name}.")
                    faction.saveData()
                }
            }
        }
    }

    @Command(aliases = arrayOf("!addguard"), description = "Adds a guard to a location")
    fun onAddGuard(message: Message, args: Array<String>) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            val location = LocationHandler.getLocationFromName(player.location)
            val faction = TessUtils.getFaction(player)
            if (location != null) {
                val claimingFaction = TessUtils.getClaimingFaction(location)
                if (claimingFaction == faction) {
                    try {
                        if (faction.controlledLocations[location]!!.size < 5) {
                            var add = false
                            var rank = 0
                            var remove: ItemStack? = null
                            player.items.forEach {
                                if (it.itemType.type == ItemType.GUARD) {
                                    add = true
                                    rank = it.itemType.usefullness.toInt()
                                    it.amount--
                                    if (it.amount <= 0)
                                        remove = it
                                    return@forEach
                                }
                            }
                            if (remove != null)
                                player.items.remove(remove!!)
                            if (add) {
                                faction.controlledLocations[location]!!.add(Factions.Guard(location, args[0], rank, Integer.parseInt(args[1])))
                                message.reply("Added a guard to ${location.channel.name}")
                                faction.saveData()
                                player.saveData()
                            }
                        }
                    } catch (e: Exception) {
                    }
                }
            }
        }
    }

    @Command(aliases = arrayOf("!attack", "!a"), description = "Attack a location")
    fun onAttack(message: Message, args: Array<String>) {
        message.delete()
        if (!args.isEmpty()) {
            val player = TessUtils.getPlayer(message.author.mentionTag)
            if (player != null) {
                if (args[0].length > 1)
                    LocationHandler.attackLocation(player, args[0], message)
                else
                    LocationHandler.attackLocation(player, Integer.parseInt(args[0]), message)
            } else message.reply("You aren't a player.")
        }
    }

    @Command(aliases = arrayOf("!factioninfo"), description = "Gets faction info for a channel")
    fun onFactionInfo(message: Message) {
        message.delete()
        val player = TessUtils.getPlayer(message.author.mentionTag)
        if (player != null) {
            val location = LocationHandler.getLocationFromName(player.location)
            if (location != null) {
                val claimingFaction = TessUtils.getClaimingFaction(location)
                if(claimingFaction != null) {
                    var string = "```\n${claimingFaction.name}"
                    string += "\nGuards:\n"
                    claimingFaction.controlledLocations[location]?.forEach {
                        string += "${it.name}: Lvl ${it.rank}\n"
                    }
                    message.reply("$string```")
                }
            }
        }
    }
}

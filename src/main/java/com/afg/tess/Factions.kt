package com.afg.tess

import com.afg.tess.combat.CombatHandler
import com.afg.tess.combat.combats.ZoneCombat
import de.btobastian.javacord.entities.UserStatus
import de.btobastian.javacord.entities.message.Message
import java.io.File
import java.io.FileWriter
import java.io.PrintWriter
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

/**
 * Created by AFlyingGrayson on 9/12/17
 */
object Factions {

    val factionList = ArrayList<Faction>()
    init {
        val default = Faction()
        default.name = "factionless"
        factionList.add(default)
    }

    class Faction {
        var name = ""
        var adminName = "admin"
        var gruntName = "grunt"

        var admins = ArrayList<PlayerData.Player>()
        var grunts = ArrayList<PlayerData.Player>()

        val controlledLocations = HashMap<LocationHandler.Location, ArrayList<Factions.Guard>>()

        fun saveData() {
            val data = HashMap<String, String>()
            data.put("name", name)
            data.put("adminName", adminName)
            data.put("gruntName", gruntName)
            var factionString = ""
            grunts.forEach {
                factionString += it.playerID + ","
            }
            data.put("grunts", factionString)

            factionString = ""
            admins.forEach {
                factionString += it.playerID + ","
            }
            data.put("admins", factionString)

            controlledLocations.forEach { l, g ->
                var s = ""
                g.forEach { s += it.saveData() + "," }
                data.put(l.channel.name, s)
            }

            val dr = File(Tess.factionDataFolderPath)
            dr.mkdirs()
            val factionDataFile = File(dr, name)
            factionDataFile.createNewFile()
            val fileWriter = FileWriter(factionDataFile)
            val printWriter = PrintWriter(fileWriter)
            data.forEach { k, v -> printWriter.println(k + "=" + v) }
            printWriter.close()
        }

        fun loadData(data: HashMap<String, String>) {
            if (data["name"] != null) name = data["name"]!!
            if (data["adminName"] != null) adminName = data["adminName"]!!
            if (data["gruntName"] != null) gruntName = data["gruntName"]!!
            grunts.clear()
            if (data["grunts"] != null) {
                val playersString = data["grunts"]
                playersString!!.split(",").forEach {
                    val player = TessUtils.getPlayer(it)
                    if (player != null) {
                        grunts.add(player)
                    }
                }
            }
            admins.clear()
            if (data["admins"] != null) {
                val playersString = data["admins"]
                playersString!!.split(",").forEach {
                    val player = TessUtils.getPlayer(it)
                    if (player != null) {
                        admins.add(player)
                    }
                }
            }
            controlledLocations.clear()
            LocationHandler.locationList.forEach {
                if (data[it.channel.name] != null) {
                    val d = data[it.channel.name]!!.split(",")
                    val guards = ArrayList<Guard>()
                    try {
                        d.forEach {
                            val s = it.split("$")
                            guards.add(Guard(LocationHandler.getLocationFromName(s[0])!!, s[1], Integer.parseInt(s[2]), Integer.parseInt(s[3])))
                        }
                    } catch (e: Exception) { }
                    controlledLocations.put(it, guards)
                }
            }
        }
    }

    fun loadData() {
        val factionArray = ArrayList<String>()
        factionList.clear()
        val dr = File(Tess.factionDataFolderPath)
        dr.mkdirs()

        val factionList = File(Tess.factionListFilePath)
        factionList.createNewFile()
        var scanner = Scanner(factionList)
        while (scanner.hasNextLine())
            factionArray.add(scanner.nextLine())
        scanner.close()

        factionArray.forEach { name ->
            val factionDataFile = File(dr, name)
            val factionData = HashMap<String, String>()
            factionDataFile.createNewFile()

            scanner = Scanner(factionDataFile)
            while (scanner.hasNextLine()) {
                val s = scanner.nextLine()
                factionData.put(TessUtils.getKey(s), TessUtils.getValue(s))
            }
            val faction = Faction()
            faction.loadData(factionData)
            if(!Factions.factionList.any { it.name == faction.name })
                Factions.factionList.add(faction)
        }
    }

    class Guard(var location: LocationHandler.Location, var name: String, var rank: Int, var area: Int) {

        fun saveData(): String {
            return "$location$$name$$rank$$area"
        }
    }

    fun attackControlledLocation(location: LocationHandler.Location, attacker: PlayerData.Player, message: Message) : Boolean {
        val faction = TessUtils.getClaimingFaction(location)
        val guards = faction!!.controlledLocations[location]
        var combat = TessUtils.getCombat(location.channel)
        val attackingUser = TessUtils.getRpMember(attacker.playerID)
        if(attackingUser != null) {
            if(TessUtils.getCombat(attacker) != null){
                message.reply("You are already in a combat")
                return false
            }
            if(location.combatCooldown){
                message.reply("That location was recently attacked, and can't be attacked again for a while.")
                return false
            }
            if (combat != null) {
                combat.participants.forEach {
                    if (it.faction != faction && it.faction != TessUtils.getFaction(attacker)) {
                        message.reply("Another faction is already attacking this location.")
                        return false
                    }
                }

                combat.addPlayer(attackingUser)?.area = 5
                return true
            } else {
                combat = ZoneCombat(location.channel, TessUtils.getFaction(attacker), faction)
                CombatHandler.combatList.add(combat)
                guards?.forEach {
                    com.afg.tess.combat.npcs.Guard.spawnGuard(it.location, it.name, it.rank, it.area, faction)
                }
                combat.addPlayer(attackingUser)?.area = 5

                PlayerData.players.forEach {
                    if (it.location == location.channel.name && TessUtils.getFaction(it) == faction && TessUtils.getCombat(it) == null){
                        val user = TessUtils.getRpMember(it.playerID)
                        if(user != null && user.status == UserStatus.ONLINE)
                            combat!!.addPlayer(user)?.area = 0
                    }
                }
                return true
            }
        } else return false
    }
}
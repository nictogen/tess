package com.afg.tess.commands.api

import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.DiscordAPI
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.listener.message.MessageCreateListener
import de.btobastian.javacord.listener.message.MessageEditListener
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 * Created by AFlyingGrayson on 9/18/17
 */
object CommandHandler : MessageCreateListener, MessageEditListener {

    private val commands = HashMap<Array<String>, CommandHolder>()

    fun loadCommands(obj : Any) {
        obj::class.declaredFunctions.forEach { f ->
            if (f.annotations.any { it is Command }) {
                val aliases = (f.annotations.filter { it is Command }[0] as Command).aliases
                commands.put(aliases, CommandHolder(obj, f))
            }
        }
    }
    class CommandHolder(val obj : Any, val func : KFunction<*>)

    override fun onMessageCreate(p0: DiscordAPI?, p1: Message) {
        readCommand(p1, p1.content)
    }

    override fun onMessageEdit(p0: DiscordAPI?, p1: Message, p2: String) {
        readCommand(p1, p2)
    }

    private fun readCommand(message: Message, content: String) {
        val args = content.split(" ")
        if (args.isNotEmpty()) {
            commands.forEach C@ { a, c ->
                a.forEach { alias ->
                    if (alias == args[0]) doCommand(c.obj, c.func, if (args.size > 1) args.subList(1, args.size) else emptyList(), message)
                }
            }
        }
    }

    private fun doCommand(obj: Any, function: KFunction<*>, args: List<String>, message: Message) {
        val argObjects = LinkedHashMap<KParameter, Any>()
        try {
            argObjects.put(function.parameters[0], obj)
            val player = TessUtils.getPlayer(message.author.id)
            val member = TessUtils.getMember(player)
            argObjects.put(function.parameters[1], MessageInfo(message, player, member))

            function.parameters.subList(2, function.parameters.size).forEach { par ->
                if (par.isOptional && args.size <= par.index - 2)
                    return@forEach
                when (par.type) {
                    String::class.starProjectedType -> argObjects.put(par, args[par.index - 2])
                    PlayerHandler.Player::class.starProjectedType -> argObjects.put(par, TessUtils.getPlayer(args[par.index - 2])!!)
                    Int::class.starProjectedType -> argObjects.put(par, Integer.parseInt(args[par.index - 2]))
                    Boolean::class.starProjectedType -> if (args[par.index - 2] == "true") argObjects.put(par, true) else if (args[par.index - 2] == "false") argObjects.put(par, false) else null!!
                    String::class.starProjectedType.withNullability(true) -> argObjects.put(par, args[par.index - 2])
                    PlayerHandler.Player::class.starProjectedType.withNullability(true) -> argObjects.put(par, TessUtils.getPlayer(args[par.index - 2])!!)
                    Int::class.starProjectedType.withNullability(true) -> argObjects.put(par, Integer.parseInt(args[par.index - 2]))
                    Boolean::class.starProjectedType.withNullability(true) -> if (args[par.index - 2] == "true") argObjects.put(par, true) else if (args[par.index - 2] == "false") argObjects.put(par, false) else null!!

                }
            }
            if(function.returnType == String::class.starProjectedType) {
                val s = function.callBy(argObjects) as String
                message.delete()
                message.reply(s)
            } else {
                message.delete()
                function.callBy(argObjects)
            }
        } catch (e: Exception) {
            message.reply("Incorrect parameters.")
        }

    }

    class MessageInfo(val message: Message, val player: PlayerHandler.Player, val user: User)
}
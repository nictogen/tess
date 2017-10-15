package com.afg.tess.commands.api

import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.entities.User
import de.btobastian.javacord.entities.message.Message
import de.btobastian.javacord.events.message.MessageCreateEvent
import de.btobastian.javacord.events.message.MessageEditEvent
import de.btobastian.javacord.listeners.message.MessageCreateListener
import de.btobastian.javacord.listeners.message.MessageEditListener
import java.util.*
import kotlin.collections.LinkedHashMap
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredFunctions
import kotlin.reflect.full.isSubtypeOf
import kotlin.reflect.full.starProjectedType
import kotlin.reflect.full.withNullability

/**
 * Created by AFlyingGrayson on 9/18/17
 */
object CommandHandler : MessageCreateListener, MessageEditListener {
    override fun onMessageEdit(event: MessageEditEvent) {
        if (!event.message.get().author.get().isYourself && event.channel != null && TessUtils.isPlayerChannel(event.channel.id.toString())) {
            readCommand(event.message.get(), event.message.get().content, TessUtils.getPlayer(event.channel.id.toString()))
        }
    }

    override fun onMessageCreate(event: MessageCreateEvent) {
        if (!event.message.author.get().isYourself && event.channel != null && TessUtils.isPlayerChannel(event.channel.id.toString())) {
            readCommand(event.message, event.message.content, TessUtils.getPlayer(event.channel.id.toString()))
        }
    }

    private val commands = HashMap<Array<String>, CommandHolder>()

    fun loadCommands(obj: Any) {
        obj::class.declaredFunctions.forEach { f ->
            if (f.annotations.any { it is Command }) {
                val aliases = (f.annotations.filter { it is Command }[0] as Command).aliases
                commands.put(aliases, CommandHolder(obj, f))
            }
        }
    }

    class CommandHolder(val obj: Any, val func: KFunction<*>)

    fun readCommand(message: Message, content: String, player: PlayerHandler.Player) {
        val args = content.split(" ")
        if (args.isNotEmpty()) {
            commands.forEach C@ { a, c ->
                a.forEach { alias ->
                    if (alias == args[0])
                        doCommand(c.obj, c.func, if (args.size > 1) args.subList(1, args.size) else emptyList(), player, message)
                }
            }
        }
    }

    private fun doCommand(obj: Any, function: KFunction<*>, args: List<String>, player: PlayerHandler.Player, message: Message) {
        val argObjects = LinkedHashMap<KParameter, Any>()
//        try {
            argObjects.put(function.parameters[0], obj)
            val member = message.author
            argObjects.put(function.parameters[1], MessageInfo(message, player, member.get()))

            function.parameters.subList(2, function.parameters.size).forEach { par ->
                if (par.isOptional && args.size <= par.index - 2)
                    return@forEach
                when {
                    par.type == String::class.starProjectedType -> argObjects.put(par, args[par.index - 2])
                    par.type == Int::class.starProjectedType -> argObjects.put(par, Integer.parseInt(args[par.index - 2]))
                    par.type == Boolean::class.starProjectedType -> if (args[par.index - 2] == "true") argObjects.put(par, true) else if (args[par.index - 2] == "false") argObjects.put(par, false) else null!!
                    par.type == String::class.starProjectedType.withNullability(true) -> argObjects.put(par, args[par.index - 2])
                    par.type == Int::class.starProjectedType.withNullability(true) -> argObjects.put(par, Integer.parseInt(args[par.index - 2]))
                    par.type == Boolean::class.starProjectedType.withNullability(true) -> if (args[par.index - 2] == "true") argObjects.put(par, true) else if (args[par.index - 2] == "false") argObjects.put(par, false) else null!!
                    par.type.isSubtypeOf(ArrayList::class.starProjectedType) -> { argObjects.put(par, args.subList(par.index - 2, args.size)) }
                }
            }
            if (function.returnType == String::class.starProjectedType) {
                val s = function.callBy(argObjects) as String
                message.delete()
                message.channel.sendMessage(s)
            } else {
                message.delete()
                function.callBy(argObjects)
            }
//        } catch (e: Exception) {
//            message.reply("Incorrect parameters.")
//        }

    }

    class MessageInfo(val message: Message, val player: PlayerHandler.Player, val user: User)
}
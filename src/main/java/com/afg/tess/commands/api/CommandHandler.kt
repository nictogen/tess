package com.afg.tess.commands.api

import com.afg.tess.Item
import com.afg.tess.PlayerData
import com.afg.tess.TessUtils
import com.afg.tess.reply
import org.javacord.api.entity.message.Message
import org.javacord.api.entity.user.User
import org.javacord.api.event.message.MessageCreateEvent
import org.javacord.api.event.message.MessageEditEvent
import org.javacord.api.listener.message.MessageCreateListener
import org.javacord.api.listener.message.MessageEditListener
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
    override fun onMessageCreate(p0: MessageCreateEvent) {
        readCommand(p0.message, p0.message.content)
    }

    override fun onMessageEdit(p0: MessageEditEvent) {
        if (p0.message.isPresent)
            readCommand(p0.message.get(), p0.message.get().content)
    }

    private val commands = HashMap<Array<String>, CommandHolder>()

    fun loadCommands(obj: Any) {
        obj::class.declaredFunctions.forEach { f ->
            if (f.annotations.any { it is Command }) {
                val aliases = (f.annotations.filter { it is Command }[0] as Command).aliases
                commands[aliases] = CommandHolder(obj, f)
            }
        }
    }

    class CommandHolder(val obj: Any, val func: KFunction<*>)

    private fun readCommand(message: Message, content: String) {
        val args = content.split(" ")
        if (args.isNotEmpty()) {
            commands.forEach C@{ a, c ->
                a.forEach { alias ->
                    if (alias == args[0]) doCommand(c.obj, c.func, if (args.size > 1) args.subList(1, args.size) else emptyList(), message)
                }
            }
        }
    }

    private fun doCommand(obj: Any, function: KFunction<*>, args: List<String>, message: Message) {
        val argObjects = LinkedHashMap<KParameter, Any>()
        try {
            argObjects[function.parameters[0]] = obj
            val player = TessUtils.getPlayer(message.author)!!
            val member = TessUtils.getMember(player)!!
            argObjects[function.parameters[1]] = MessageInfo(message, player, member)

            function.parameters.subList(2, function.parameters.size).forEach { par ->
                if (par.isOptional && args.size <= par.index - 2)
                    return@forEach
                when (par.type) {
                    String::class.starProjectedType -> argObjects[par] = args[par.index - 2]
                    PlayerData.Player::class.starProjectedType -> argObjects[par] = TessUtils.getPlayer(args[par.index - 2])!!
                    Int::class.starProjectedType -> argObjects[par] = Integer.parseInt(args[par.index - 2])
                    Item::class.starProjectedType -> argObjects[par] = Item.valueOf(args[par.index - 2].toUpperCase())
                    Boolean::class.starProjectedType -> if (args[par.index - 2] == "true") argObjects[par] = true else if (args[par.index - 2] == "false") argObjects[par] = false else null!!
                    String::class.starProjectedType.withNullability(true) -> argObjects[par] = args[par.index - 2]
                    PlayerData.Player::class.starProjectedType.withNullability(true) -> argObjects[par] = TessUtils.getPlayer(args[par.index - 2])!!
                    Int::class.starProjectedType.withNullability(true) -> argObjects[par] = Integer.parseInt(args[par.index - 2])
                    Item::class.starProjectedType.withNullability(true) -> argObjects[par] = Item.valueOf(args[par.index - 2].toUpperCase())
                    Boolean::class.starProjectedType.withNullability(true) -> if (args[par.index - 2] == "true") argObjects[par] = true else if (args[par.index - 2] == "false") argObjects[par] = false else null!!
                }
            }
            if (function.returnType == String::class.starProjectedType) {
                val s = function.callBy(argObjects) as String
                message.reply(s)
            } else function.callBy(argObjects)
            message.delete()
        } catch (e: Exception) {
            message.reply("Incorrect parameters.")
        }

    }

    class MessageInfo(val message: Message, val player: PlayerData.Player, val user: User)
}

package com.afg.tess.players.skills

import com.afg.tess.players.skills.fighter.Block
import com.afg.tess.players.skills.fighter.BruteStrike
import com.afg.tess.players.skills.fighter.Cleave
import com.afg.tess.players.skills.fighter.Strike
import com.afg.tess.util.TessUtils
import de.btobastian.javacord.entities.message.emoji.CustomEmoji

/**
 * Created by AFlyingGrayson on 10/7/17
 */
abstract class Skill {

    companion object {
        val skillList = ArrayList<Skill>()
        fun getSkill(id : String) : Skill {return skillList.first { it.getID() == id }}

        fun loadSkills(){
            skillList.add(Strike)
            skillList.add(BruteStrike)
            skillList.add(Cleave)
            skillList.add(Block)
        }
    }
    abstract fun getID() : String
    abstract fun getName() : String
    abstract fun getEmojiName() : String

    fun getEmoji() : CustomEmoji = TessUtils.emojiServer.customEmojis.first { it.name == getEmojiName() }
}
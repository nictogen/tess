package com.afg.tess.players.races

import com.afg.tess.handlers.PlayerHandler
import com.afg.tess.players.talents.Talent

/**
 * Created by AFlyingGrayson on 10/7/17
 */
abstract class Race {

    abstract fun getStartingStats() : ArrayList<PlayerHandler.Stat>

    abstract fun getStartingTalents() : ArrayList<Talent>
}
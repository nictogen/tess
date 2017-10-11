package com.afg.tess.classes

import com.afg.tess.players.talents.Talent

/**
 * Created by AFlyingGrayson on 10/7/17
 */
abstract class Class {
    abstract fun getTalents() : ArrayList<Talent>
    abstract fun getStartingTalents() : ArrayList<Talent>
}
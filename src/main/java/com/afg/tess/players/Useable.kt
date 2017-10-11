package com.afg.tess.players

/**
 * Created by AFlyingGrayson on 10/7/17
 */
abstract class Useable {
    abstract fun useOutOfCombat(args : ArrayList<String>)

    abstract fun useInCombat(args: ArrayList<String>)
}
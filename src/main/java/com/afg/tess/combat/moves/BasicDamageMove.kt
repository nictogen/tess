package com.afg.tess.combat.moves

/**
 * Created by AFlyingGrayson on 9/8/17
 */
class BasicDamageMove(mainStat: MainStat, type: Type, source: Source, name: String) : Move(mainStat, type, source, name) {

    override fun getBasePower(): Double {
        return 10.0
    }

    override fun getStorageName(): String {
        return "basicDamage"
    }
}
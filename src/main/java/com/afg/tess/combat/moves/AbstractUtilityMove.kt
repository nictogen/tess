package com.afg.tess.combat.moves

/**
 * Created by AFlyingGrayson on 9/12/17
 */
abstract class AbstractUtilityMove(mainStat: MainStat, source: Source, name: String) : Move(mainStat, Type.UTILITY, source, name) {

    override fun getBasePower(): Double { return 0.0 }

    override fun saveData(): String {
        return "${getStorageName()}/${mainStat.name}/$source/$name"
    }

}

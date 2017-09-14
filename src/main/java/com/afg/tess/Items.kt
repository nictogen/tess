package com.afg.tess

/**
 * Created by AFlyingGrayson on 9/6/17
 */
class ItemStack(val itemType: Item){

    companion object {
        fun getItemTypeFromDataTag(dataTag: String) : Item {
            val args = dataTag.split("/")
            try {
                return Item.valueOf(args[0].toUpperCase())
            } catch (e : Exception){}
            return Item.APPLE
        }
        fun addItemToPlayer(itemType: Item, player: PlayerData.Player, amount : Int) : Boolean{
            player.items.forEach {
                if(it.itemType == itemType){
                    if(it.amount < it.itemType.maxStackSize) {
                        it.amount += amount
                        player.saveData()
                        return true
                    }
                }
            }
            if(player.items.size < player.backpackSize){
                val itemStack = ItemStack(itemType)
                itemStack.amount = amount
                player.items.add(itemStack)
            } else return false
            player.saveData()
            return true
        }
    }

    constructor(dataTag: String) : this(getItemTypeFromDataTag(dataTag)){
       val subStrings = dataTag.split("/")
       amount = Integer.parseInt(subStrings[1])
    }
    var amount = 1

    fun saveData() : String{
        return "$itemType/$amount"
    }

}
enum class Item(var marketCost: Double, var type: ItemType, var usefullness: Double, var maxStackSize : Int){
    APPLE(3.0, ItemType.FOOD, 1.0, 64),
    MEMORY_ADJUSTER(200.0, ItemType.MOVE_REMOVER, 1.0, 5),
    SMALL_EROS_SEED(5000.0, ItemType.STAT_BOOSTER, 1.0, 5),
    MEDIUM_EROS_SEED(-1.0, ItemType.STAT_BOOSTER, 2.0, 5),
    LARGE_EROS_SEED(-1.0, ItemType.STAT_BOOSTER, 5.0, 5),
    DRAGON_EROS_SEED(-1.0, ItemType.STAT_BOOSTER, 10.0, 5),
    RANK_1_GUARD(5000.0, ItemType.GUARD, 1.0, 1),
    RANK_2_GUARD(10000.0, ItemType.GUARD, 1.0, 1),
    RANK_3_GUARD(15000.0, ItemType.GUARD, 1.0, 1)
}

enum class ItemType{
    FOOD,
    MOVE_REMOVER,
    STAT_BOOSTER,
    GUARD
}
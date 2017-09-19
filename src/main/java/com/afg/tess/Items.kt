package com.afg.tess

/**
 * Created by AFlyingGrayson on 9/6/17
 */
class ItemStack(val itemType: Item) {

    companion object {
        fun getItemTypeFromDataTag(dataTag: String): Item {
            val args = dataTag.split("/")
            try {
                return Item.valueOf(args[0].toUpperCase())
            } catch (e: Exception) {
            }
            return Item.APPLE
        }

        fun addItemToPlayer(itemType: Item, player: PlayerData.Player, amount: Int): Boolean {
            player.items.forEach {
                if (it.itemType == itemType) {
                    if (it.amount < it.itemType.maxStackSize) {
                        it.amount += amount
                        player.saveData()
                        return true
                    }
                }
            }
            if (player.items.size < player.backpackSize) {
                val itemStack = ItemStack(itemType)
                itemStack.amount = amount
                player.items.add(itemStack)
            } else return false
            player.saveData()
            return true
        }

        fun removeItemFromPlayer(itemType: Item, player: PlayerData.Player): Boolean {
            var removed = false
            player.items.forEach {
                if (it.itemType == itemType) {
                    it.amount -= 1
                    removed = true
                    return@forEach
                }
            }
            val toRemove = player.items.filter { it.amount <= 0 }
            player.items.removeAll(toRemove)
            player.saveData()
            return removed
        }
    }

    constructor(dataTag: String) : this(getItemTypeFromDataTag(dataTag)) {
        val subStrings = dataTag.split("/")
        amount = Integer.parseInt(subStrings[1])
    }

    var amount = 1

    fun saveData(): String {
        return "$itemType/$amount"
    }

}

enum class Item(var marketCost: Double, var type: ItemType, var shop: ShopType, var usefullness: Double, var maxStackSize: Int) {
    APPLE(3.0, ItemType.FOOD, ShopType.MARKET, 1.0, 64),
    PHONE(100.0, ItemType.PHONE, ShopType.MARKET, 0.0, 1),
    MEMORY_ADJUSTER(200.0, ItemType.MOVE_REMOVER, ShopType.MARKET, 1.0, 5),
    SMALL_EROS_SEED(5000.0, ItemType.STAT_BOOSTER, ShopType.MARKET, 1.0, 5),
    MEDIUM_EROS_SEED(-1.0, ItemType.STAT_BOOSTER, ShopType.MARKET, 2.0, 5),
    LARGE_EROS_SEED(-1.0, ItemType.STAT_BOOSTER, ShopType.MARKET, 5.0, 5),
    DRAGON_EROS_SEED(-1.0, ItemType.STAT_BOOSTER, ShopType.MARKET, 10.0, 5),
    RANK_1_GUARD(1000.0, ItemType.GUARD, ShopType.MARKET, 1.0, 1),
    RANK_2_GUARD(3000.0, ItemType.GUARD, ShopType.MARKET, 2.0, 1),
    RANK_3_GUARD(5000.0, ItemType.GUARD, ShopType.MARKET, 3.0, 1),
    RANK_4_GUARD(10000.0, ItemType.GUARD, ShopType.MARKET, 4.0, 1),
    BEER(5.0, ItemType.ALCOHOL, ShopType.BAR, 1.0, 5),
    SHOT(10.0, ItemType.ALCOHOL, ShopType.BAR, 5.0, 1),
    SMALL_ERO_BAIT(50.0, ItemType.ERO_BAIT, ShopType.MARKET, 1.0, 5),
    MEDIUM_ERO_BAIT(100.0, ItemType.ERO_BAIT, ShopType.MARKET, 5.0, 5),
    LARGE_ERO_BAIT(200.0, ItemType.ERO_BAIT, ShopType.MARKET, 10.0, 5)
}

enum class ItemType {
    FOOD,
    MOVE_REMOVER,
    STAT_BOOSTER,
    GUARD,
    PHONE,
    ALCOHOL,
    ERO_BAIT
}

enum class ShopType {
    MARKET,
    BAR
}
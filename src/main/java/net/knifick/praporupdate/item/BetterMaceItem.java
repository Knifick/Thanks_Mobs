package net.knifick.praporupdate.item;

import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.Rarity;

public class BetterMaceItem extends MaceItem {
    public BetterMaceItem(Properties properties) {
        super(properties);
    }

    @Override
    public int getEnchantmentValue() {
        return 10;
    }

    public BetterMaceItem() {
        super(new Item.Properties()
                .rarity(Rarity.EPIC)
                .durability(500)
                .component(DataComponents.TOOL, BetterMaceItem.createToolProperties())
                .attributes(BetterMaceItem.createAttributes()));
    }
}

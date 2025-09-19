package net.knifick.praporupdate.item;

import net.minecraft.core.Holder;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.MaceItem;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.enchantment.Enchantable;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.Random;
import java.util.Set;

public class BetterMaceItem extends MaceItem {

    public BetterMaceItem(Properties attributes) {
        super(new Item.Properties()
                .rarity(Rarity.EPIC)
                .durability(500)
                .component(DataComponents.TOOL, createToolProperties())
                .component(DataComponents.ENCHANTABLE, new Enchantable(5))
                .attributes(createAttributes()));
    }

    @Override
    public boolean isPrimaryItemFor(ItemStack stack, Holder<Enchantment> ench) {
        return ench.is(Enchantments.BANE_OF_ARTHROPODS)
                || ench.is(Enchantments.SMITE)
                || ench.is(Enchantments.FIRE_ASPECT)
                || ench.is(Enchantments.UNBREAKING)
                || ench.is(Enchantments.MENDING)
                || ench.is(Enchantments.VANISHING_CURSE)
                || ench.is(Enchantments.DENSITY)
                || ench.is(Enchantments.BREACH)
                || ench.is(Enchantments.WIND_BURST);
    }

}

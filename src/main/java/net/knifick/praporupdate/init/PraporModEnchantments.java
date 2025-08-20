package net.knifick.praporupdate.init;

import net.knifick.praporupdate.PraporMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.LevelAccessor;

import java.util.Optional;

public class PraporModEnchantments {
    public static final ResourceLocation RAGE_OF_SOULS = ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "rage_of_souls");

    public static Holder<Enchantment> getEnchantment(HolderLookup.RegistryLookup<Enchantment> lookup, ResourceLocation id){
        Holder<Enchantment> ench = lookup.getOrThrow(
                ResourceKey.create(Registries.ENCHANTMENT,id)
        );
        return ench;
    }

    public static Holder<Enchantment> getEnchantment(Optional<HolderLookup.RegistryLookup<Enchantment>> lookup, ResourceLocation id){
        Holder<Enchantment> ench = lookup.get().getOrThrow(
                ResourceKey.create(Registries.ENCHANTMENT,id)
        );
        return ench;
    }

    public static Holder<Enchantment> getEnchantment(LevelAccessor level, ResourceLocation id){
        HolderLookup.RegistryLookup lookup = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        Holder<Enchantment> ench = lookup.getOrThrow(
                ResourceKey.create(Registries.ENCHANTMENT,id)
        );
        return ench;
    }
}

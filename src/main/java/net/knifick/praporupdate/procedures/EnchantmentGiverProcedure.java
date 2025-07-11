package net.knifick.praporupdate.procedures;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.network.chat.Component;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.Level;

import java.util.Map;

public class EnchantmentGiverProcedure {
    public static void execute(ServerPlayer player) {
        // Получаем предмет в главной руке игрока
        ItemStack heldItem = player.getItemInHand(InteractionHand.MAIN_HAND);

        // Проверяем, является ли предмет зачарованной книгой
        if (heldItem.getItem() != Items.ENCHANTED_BOOK) {
            return; // Если это не зачарованная книга — выходим
        }

        // Получаем список зачарований на книге
        ItemEnchantments enchantments = heldItem.get(DataComponents.STORED_ENCHANTMENTS);
        ItemStack newBook = new ItemStack(Items.ENCHANTED_BOOK);
        boolean upgraded = false;
        Level world = player.level();

        for (Object2IntMap.Entry<Holder<Enchantment>> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey().value();
            int currentLevel = entry.getValue();
            int maxLevel = enchantment.getMaxLevel();

            // Проверяем, есть ли у зачарования уровни (оно не одноручное, как Починка)
            if (maxLevel > 1 && currentLevel < maxLevel) {
                // Повышаем уровень, но не выше максимального
                int newLevel = Math.min(currentLevel + 1, maxLevel);
                if (!world.isClientSide())
                    newBook.enchant(entry.getKey(), newLevel);
                upgraded = true;
            } else {
                // Оставляем неизменённые зачарования
                if (!world.isClientSide())
                    newBook.enchant(entry.getKey(), currentLevel);
            }
        }

        // Если хотя бы одно зачарование было улучшено, заменяем книгу
        if (upgraded) {
            player.setItemInHand(InteractionHand.MAIN_HAND, newBook);
            player.getInventory().setChanged();
        }
    }
}

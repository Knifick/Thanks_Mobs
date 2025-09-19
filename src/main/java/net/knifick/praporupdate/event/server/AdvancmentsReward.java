package net.knifick.praporupdate.event.server;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.item.crafting.Recipe;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.ItemEntityPickupEvent;

import java.util.List;
import java.util.Optional;

@EventBusSubscriber
public class AdvancmentsReward {

    /**
     * Определяет, какой рецепт нужно выдать за конкретный предмет.
     * Если рецепта нет – вернёт null.
     */
    public static ResourceLocation getRecipeIdForItem(ItemStack stack, Player player, MinecraftServer server) {
        if(stack.getItem() == Items.COMPARATOR
                || stack.getItem() == Items.IRON_INGOT
                || stack.getItem() == Items.REPEATER
                || stack.getItem() == PraporModItems.COPPER_WIRE.get()
                || stack.getItem() == Items.REDSTONE
                || stack.getItem() == Items.REDSTONE_BLOCK) {
            return ResourceLocation.fromNamespaceAndPath("prapor", "battery_craft");
        }
        else if(stack.getItem() == PraporModItems.OXIDIZEDCOPPERSHEET.get()
                || stack.getItem() == Items.GOLD_INGOT) {
            return ResourceLocation.fromNamespaceAndPath("prapor", "board_craft");
        }
        else if(stack.getItem() == Items.NETHER_BRICK
                || stack.getItem() == PraporModItems.SPAWNER_SHARD.get()
                || stack.getItem() == Items.NETHER_STAR) {
            return ResourceLocation.fromNamespaceAndPath("prapor", "casket_of_souls_craft");
        }
        else if(stack.getItem() == Items.COPPER_INGOT
                || stack.getItem() == Items.STICK) {
            return ResourceLocation.fromNamespaceAndPath("prapor", "copper_wire_craft");
        }
        else if(stack.getItem() == Items.GOLD_BLOCK
                || stack.getItem() == Items.GOLD_INGOT
                || stack.getItem() == Items.NETHER_STAR) {
            return ResourceLocation.fromNamespaceAndPath("prapor", "gold_trophy");
        }
        else if(stack.getItem() == Items.RABBIT_HIDE
                || stack.getItem() == Items.STRING
                || stack.getItem() == Items.PAPER) {
            return ResourceLocation.fromNamespaceAndPath("prapor", "guide1");
        }
        else if(stack.is(ItemTags.create(ResourceLocation.fromNamespaceAndPath("minecraft", "copper_craft_rag")))) {
            return ResourceLocation.fromNamespaceAndPath("prapor", "oxidized_copper_ingot_craft");
        }
        else if(stack.getItem() == PraporModItems.OXIDIZED_COPPER_INGOT.get()) {
            return ResourceLocation.fromNamespaceAndPath("prapor", "oxidized_copper_sheet_craft");
        }
        return null;
    }

    /**
     * Универсальная выдача рецепта
     */
    public static void grantRecipe(ServerPlayer player, ResourceLocation recipeId) {
        if (player == null || recipeId == null) return;

        ResourceKey<Recipe<?>> key = ResourceKey.create(Registries.RECIPE, recipeId);
        player.awardRecipesByKey(List.of(key)); // сервер сам проверит существование
    }

    @SubscribeEvent
    public static void onItemPickup(ItemEntityPickupEvent.Pre event) {
        Player player = event.getPlayer();
        if (player.level().isClientSide) return;

        var server = player.level().getServer();
        if (server == null) return;

        ItemStack stack = event.getItemEntity().getItem();

        // --- вызываем общую логику ---
        ResourceLocation recipeId = getRecipeIdForItem(stack, player, server);
        if (player instanceof ServerPlayer serverPlayer)
        grantRecipe(serverPlayer, recipeId);
    }


}

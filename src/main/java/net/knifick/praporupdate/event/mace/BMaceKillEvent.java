package net.knifick.praporupdate.event.mace;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.AttackEntityEvent;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@EventBusSubscriber(modid = PraporMod.MODID)
public class BMaceKillEvent {
    private static final Map<UUID, Integer> LAST_HOTBAR_SLOT = new HashMap<>();

    @SubscribeEvent
    public static void onAttack(AttackEntityEvent e) {
        Player player = e.getEntity();
        // сохраняем выбранный слот (0‑8) прямо перед ударом
        LAST_HOTBAR_SLOT.put(player.getUUID(), player.getInventory().getSelectedSlot());
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent e) {
        if (!(e.getSource().getEntity() instanceof Player player)) return;

        ItemStack weapon = e.getSource().getWeaponItem();
        if (weapon == null || !weapon.is(PraporModItems.BETTER_MACE.get())) {
            return; // оружия нет или это не нужная булава
        }

        Integer slot = LAST_HOTBAR_SLOT.get(player.getUUID());
        ItemStack newWeapon = new ItemStack(PraporModItems.BETTER_MACE_CHARGED.get());

        // копируем прочность
        newWeapon.setDamageValue(weapon.getDamageValue() + 1);
        ItemEnchantments enchants = weapon.getEnchantments();
        EnchantmentHelper.setEnchantments(newWeapon, enchants);

        if (slot != null && slot >= 0) {
            player.getInventory().setItem(slot, newWeapon);
        }
    }

}

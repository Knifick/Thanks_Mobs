package net.knifick.praporupdate.event.ironkin;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.init.PraporModEnchantments;
import net.knifick.praporupdate.init.PraporModEntities;
import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

import static net.minecraft.world.item.EnchantedBookItem.createForEnchantment;

@EventBusSubscriber(modid = PraporMod.MODID)
public class CustomDrop {

    @SubscribeEvent
    public static void onMobDrop(LivingDropsEvent event) {
        // Проверяем, что умер наш моб
        if (event.getEntity().getType() == PraporModEntities.DARKIRONKIN.get()) { // ← заменишь на своего моба
            // Шанс 10% на книгу с энчантом
            if (event.getEntity().level().random.nextFloat() < 0.1f) {
                Holder<Enchantment> rage = PraporModEnchantments.getEnchantment(event.getEntity().level(), PraporModEnchantments.RAGE_OF_SOULS);
                ItemStack book = createForEnchantment(new EnchantmentInstance(rage, 1));
                event.getDrops().add(new ItemEntity(
                        event.getEntity().level(),
                        event.getEntity().getX(),
                        event.getEntity().getY(),
                        event.getEntity().getZ(),
                        book
                ));
            }
        }
    }
}
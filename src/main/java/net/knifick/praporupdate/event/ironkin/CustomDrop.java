package net.knifick.praporupdate.event.ironkin;

import net.knifick.praporupdate.PraporMod;
import net.knifick.praporupdate.init.PraporModEnchantments;
import net.knifick.praporupdate.init.PraporModEntities;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.event.entity.living.LivingDropsEvent;

@EventBusSubscriber(modid = PraporMod.MODID)
public class CustomDrop {

    @SubscribeEvent
    public static void onMobDrop(LivingDropsEvent event) {
        // Проверяем, что умер нужный моб
        if (event.getEntity().getType() == PraporModEntities.DARKIRONKIN.get()) {
            // Шанс 10% на книгу с энчантом
            if (event.getEntity().level().random.nextFloat() < 0.1f) {
                Holder<Enchantment> rage = PraporModEnchantments.getEnchantment(
                        event.getEntity().level(),
                        PraporModEnchantments.RAGE_OF_SOULS
                );

                ItemStack book = createEnchantedBook(rage, 1);

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

    public static ItemStack createEnchantedBook(Holder<Enchantment> enchantment, int level) {
        ItemStack book = new ItemStack(Items.ENCHANTED_BOOK);

        // Используем Mutable для сборки чар
        ItemEnchantments.Mutable mutable = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        mutable.set(enchantment, level);

        // Превращаем обратно в иммутабельный ItemEnchantments
        ItemEnchantments enchants = mutable.toImmutable();

        // Сохраняем в компонент книги
        book.set(DataComponents.STORED_ENCHANTMENTS, enchants);

        return book;
    }
}

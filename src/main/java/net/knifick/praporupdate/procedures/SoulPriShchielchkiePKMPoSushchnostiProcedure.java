package net.knifick.praporupdate.procedures;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import net.knifick.praporupdate.init.PraporModItems;

public class SoulPriShchielchkiePKMPoSushchnostiProcedure {

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceEntity) {
		if (!(entity instanceof LivingEntity) || !(sourceEntity instanceof LivingEntity livingSource))
			return;

		ItemStack mainHand = livingSource.getMainHandItem();
		if (mainHand.getItem() != Items.GLASS_BOTTLE
		&& mainHand.getItem() != Items.BUCKET)
			return;

		// Удаляем сущность на сервере
		if (!entity.level().isClientSide()) {
			entity.discard();
		}

		ItemStack newItem;
		if (mainHand.getItem() == Items.GLASS_BOTTLE) {
			newItem = new ItemStack(PraporModItems.SOUL_BOTTLE.get());
		} else if (mainHand.getItem() == Items.BUCKET) {
			newItem = new ItemStack(PraporModItems.SOUL_SPAWN_EGG.get()); // пример, если есть
		} else {
			return;
		}

		livingSource.setItemInHand(InteractionHand.MAIN_HAND, newItem);
		if (livingSource instanceof Player player) {
			player.getInventory().setChanged();
		}


		// Проигрываем звук
		if (world instanceof Level level) {
			level.registryAccess()
					.lookupOrThrow(Registries.SOUND_EVENT)
					.get(ResourceLocation.parse("prapor:soul_sounds")) // Optional<Holder.Reference<SoundEvent>>
					.ifPresent(holder -> {
						var soundEvent = holder.value(); // <- извлекаем SoundEvent
						if (!level.isClientSide()) {
							level.playSound(null, BlockPos.containing(x, y, z),
									soundEvent, SoundSource.AMBIENT, 1.0F, 1.0F);
						} else {
							level.playLocalSound(x, y, z,
									soundEvent, SoundSource.AMBIENT, 1.0F, 1.0F, false);
						}
					});
		}
	}
}


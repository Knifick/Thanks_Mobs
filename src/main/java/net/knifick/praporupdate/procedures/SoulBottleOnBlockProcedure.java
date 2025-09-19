package net.knifick.praporupdate.procedures;

import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.core.registries.Registries;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import javax.annotation.Nullable;

@EventBusSubscriber
public class SoulBottleOnBlockProcedure {
	@SubscribeEvent
	public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
		if (event.getHand() != event.getEntity().getUsedItemHand()) return;
		execute(event, event.getLevel(),
				event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(),
				event.getEntity());
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		execute(null, world, x, y, z, entity);
	}

	private static void execute(@Nullable net.neoforged.bus.api.Event event,
								LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null) return;

		BlockPos pos = BlockPos.containing(x, y, z);

		if (world.getBlockState(pos).getBlock() == Blocks.CAMPFIRE
				&& entity instanceof Player player
				&& player.getItemInHand(InteractionHand.MAIN_HAND).is(PraporModItems.SOUL_BOTTLE.get())) {

			// === Меняем блок, переносим только свойства ===
			BlockState newState = Blocks.SOUL_CAMPFIRE.defaultBlockState();
			BlockState oldState = world.getBlockState(pos);

			for (Property<?> pOld : oldState.getProperties()) {
				Property<?> pNew = newState.getBlock().getStateDefinition().getProperty(pOld.getName());
				if (pNew != null) {
					try {
						newState = copyProperty(newState, oldState, pOld, pNew);
					} catch (Exception ignored) {}
				}
			}

			// НИКАКОГО сохранения/загрузки NBT BlockEntity — это и ломает сборку на 1.21.8
			world.setBlock(pos, newState, 3);

			// Частицы
			if (world instanceof ServerLevel sl)
				sl.sendParticles(ParticleTypes.SOUL, x, y, z, 5, 0.5, 0.5, 0.5, 0.05);

			// Звук (нужен именно SoundEvent, а не Holder/Optional)
			if (world instanceof Level level) {
				var soundOpt = level.registryAccess()
						.lookupOrThrow(Registries.SOUND_EVENT)
						.get(ResourceLocation.parse("prapor:soul_sounds"));

				soundOpt.ifPresent(holder -> {
					var sound = holder.value(); // SoundEvent
					if (!level.isClientSide()) {
						level.playSound(null, pos, sound, SoundSource.AMBIENT, 1.0F, 1.0F);
					} else {
						level.playLocalSound(x, y, z, sound, SoundSource.AMBIENT, 1.0F, 1.0F, false);
					}
				});
			}

			// Замена бутылки в руке
			if (!(entity instanceof Player p && p.getAbilities().instabuild)) {
				if (entity instanceof LivingEntity le) {
					le.setItemInHand(InteractionHand.MAIN_HAND, new ItemStack(Items.GLASS_BOTTLE));
					if (le instanceof Player p) p.getInventory().setChanged();
				}
			}
		}
	}

	// Типобезопасная копия значения свойства из oldState -> в newState
	@SuppressWarnings({"unchecked","rawtypes"})
	private static <T extends Comparable<T>> BlockState copyProperty(BlockState newState, BlockState oldState,
																	 Property oldP, Property newP) {
		return newState.setValue(newP, (Comparable) oldState.getValue(oldP));
	}
}

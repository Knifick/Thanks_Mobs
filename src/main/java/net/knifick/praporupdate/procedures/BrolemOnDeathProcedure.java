package net.knifick.praporupdate.procedures;

import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.bus.api.ICancellableEvent;
import net.neoforged.bus.api.Event;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;

import net.knifick.praporupdate.entity.BrolemEntity;

import javax.annotation.Nullable;

@EventBusSubscriber
public class BrolemOnDeathProcedure {
	@SubscribeEvent
	public static void onEntityDeath(LivingDeathEvent event) {
		if (event.getEntity() != null) {
			execute(event, event.getEntity().level(), event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ(), event.getSource(), event.getEntity());
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity) {
		execute(null, world, x, y, z, damagesource, entity);
	}

	private static void execute(@Nullable Event event, LevelAccessor world, double x, double y, double z, DamageSource damagesource, Entity entity) {
		if (damagesource == null || entity == null)
			return;
		if (entity instanceof BrolemEntity && !damagesource.is(DamageTypes.GENERIC_KILL)) {
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:brolem_ruins")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:brolem_ruins")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, (y + 1.5), z, 300, 2, 1.5, 2, 0.01);
			if (entity instanceof LivingEntity _entity)
				_entity.setHealth(50);
			if (event instanceof ICancellableEvent _cancellable) {
				_cancellable.setCanceled(true);
			}
			if (entity instanceof BrolemEntity _datEntSetS)
				_datEntSetS.getEntityData().set(BrolemEntity.DATA_xs, ("" + entity.getX()));
			if (entity instanceof BrolemEntity _datEntSetS)
				_datEntSetS.getEntityData().set(BrolemEntity.DATA_zs, ("" + entity.getZ()));
			if (entity instanceof BrolemEntity _datEntSetL)
				_datEntSetL.getEntityData().set(BrolemEntity.DATA_IsAlive, false);
			if (entity instanceof BrolemEntity) {
				((BrolemEntity) entity).setAnimation("empty");
			}
			if (entity instanceof BrolemEntity) {
				((BrolemEntity) entity).setAnimation("ruins");
			}
		}
	}
}

package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.InteractionHand;
import net.minecraft.sounds.SoundSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;

import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.entity.BrolemEntity;
import net.knifick.praporupdate.PraporMod;

public class BrolemOnRCMProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity) {
		if (entity == null || sourceentity == null)
			return;
		if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PraporModItems.SOUL_BOTTLE.get()
				&& !(entity instanceof BrolemEntity _datEntL2 && _datEntL2.getEntityData().get(BrolemEntity.DATA_OnBuilding)) && !(entity instanceof BrolemEntity _datEntL3 && _datEntL3.getEntityData().get(BrolemEntity.DATA_IsAlive))) {
			if (sourceentity instanceof LivingEntity _entity) {
				ItemStack _setstack = new ItemStack(PraporModItems.SOUL_BOTTLE.get()).copy();
				_setstack.setCount(1);
				_entity.setItemInHand(InteractionHand.MAIN_HAND, _setstack);
				if (_entity instanceof Player _player)
					_player.getInventory().setChanged();
			}
			if (entity instanceof BrolemEntity _datEntSetL)
				_datEntSetL.getEntityData().set(BrolemEntity.DATA_OnBuilding, true);
			if (entity instanceof BrolemEntity) {
				((BrolemEntity) entity).setAnimation("empty");
			}
			if (entity instanceof BrolemEntity) {
				((BrolemEntity) entity).setAnimation("build");
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:soul_sounds")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:soul_sounds")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:build_bro")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:build_bro")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (world instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.SOUL, x, y, z, 5, 1, 1, 1, 0.05);
			PraporMod.queueServerWork(149, () -> {
				if (sourceentity instanceof ServerPlayer _player) {
					AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("prapor:brolem_ach"));
					if (_adv != null) {
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								_player.getAdvancements().award(_adv, criteria);
						}
					}
				}
				if (entity instanceof BrolemEntity _datEntSetL)
					_datEntSetL.getEntityData().set(BrolemEntity.DATA_OnBuilding, false);
				if (entity instanceof BrolemEntity _datEntSetL)
					_datEntSetL.getEntityData().set(BrolemEntity.DATA_IsAlive, true);
				if (entity instanceof TamableAnimal _toTame && sourceentity instanceof Player _owner)
					_toTame.tame(_owner);
			});
		}
	}
}

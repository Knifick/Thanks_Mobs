package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;

import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.init.PraporModMobEffects;
import net.knifick.praporupdate.PraporMod;

public class FrameReturnerProcedure {
	public static void execute(LevelAccessor world, Entity entity, double dur) {
		if (entity == null)
			return;
		if (entity.getData(PraporModVariables.PLAYER_VARIABLES).screamAnimValue == 0) {
			if (world instanceof Level _level) {
				if (_level.isClientSide()) {
					_level.playLocalSound((entity.getX()), (entity.getY()), (entity.getZ()), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:scream")), SoundSource.AMBIENT, 1, 1, false);
				}
			}
			if (entity instanceof LivingEntity _entity && !_entity.level().isClientSide())
				_entity.addEffect(new MobEffectInstance(PraporModMobEffects.FEAR, (int) dur, 0, true, false));
			{
				PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
				_vars.screamAnimValue = 1;
				_vars.syncPlayerVariables(entity);
			}
			PraporMod.queueServerWork(1, () -> {
				{
					PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
					_vars.screamAnimValue = 2;
					_vars.syncPlayerVariables(entity);
				}
				PraporMod.queueServerWork(1, () -> {
					{
						PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
						_vars.screamAnimValue = 3;
						_vars.syncPlayerVariables(entity);
					}
					PraporMod.queueServerWork(1, () -> {
						{
							PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
							_vars.screamAnimValue = 4;
							_vars.syncPlayerVariables(entity);
						}
						PraporMod.queueServerWork(1, () -> {
							{
								PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
								_vars.screamAnimValue = 5;
								_vars.syncPlayerVariables(entity);
							}
							PraporMod.queueServerWork(1, () -> {
								{
									PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
									_vars.screamAnimValue = 6;
									_vars.syncPlayerVariables(entity);
								}
								PraporMod.queueServerWork(1, () -> {
									{
										PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
										_vars.screamAnimValue = 7;
										_vars.syncPlayerVariables(entity);
									}
									PraporMod.queueServerWork(1, () -> {
										{
											PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
											_vars.screamAnimValue = 8;
											_vars.syncPlayerVariables(entity);
										}
										PraporMod.queueServerWork(1, () -> {
											{
												PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
												_vars.screamAnimValue = 9;
												_vars.syncPlayerVariables(entity);
											}
											PraporMod.queueServerWork(1, () -> {
												{
													PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
													_vars.screamAnimValue = 10;
													_vars.syncPlayerVariables(entity);
												}
												PraporMod.queueServerWork(1, () -> {
													{
														PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
														_vars.screamAnimValue = 0;
														_vars.syncPlayerVariables(entity);
													}
												});
											});
										});
									});
								});
							});
						});
					});
				});
			});
		} else {
			{
				PraporModVariables.PlayerVariables _vars = entity.getData(PraporModVariables.PLAYER_VARIABLES);
				_vars.screamAnimValue = 0;
				_vars.syncPlayerVariables(entity);
			}
		}
	}
}

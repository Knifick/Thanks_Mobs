package net.knifick.praporupdate.event.pooker;

import net.knifick.praporupdate.entity.PookerEntity;
import net.knifick.praporupdate.procedures.FrameReturnerProcedure;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.util.List;

@EventBusSubscriber
public class LookAtPookerHandler {

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		double reachDistance = 900.0D; // Максимальная дистанция проверки

		// Получение позиции глаз игрока и направления взгляда
		Vec3 eyePosition = player.getEyePosition(1.0F);
		Vec3 lookDirection = player.getLookAngle();

		// Вычисление конечной точки луча
		Vec3 endPosition = eyePosition.add(lookDirection.scale(reachDistance));

		// Создание AABB (Axis-Aligned Bounding Box) от позиции глаз до конечной точки луча
		AABB boundingBox = new AABB(eyePosition, endPosition);

		// Получение списка сущностей в пределах AABB, исключая самого игрока
		List<Entity> entities = player.level().getEntities(
				player,
				boundingBox,
				entity -> !entity.isSpectator() && entity.isPickable()
		);

		// Переменная для хранения ближайшей сущности
		Entity closestEntity = null;
		double closestDistance = reachDistance;

		// Проверка каждой сущности на пересечение с лучом
		for (Entity entity : entities) {
			AABB entityBoundingBox = entity.getBoundingBox().inflate(entity.getPickRadius());
			Vec3 hitResult = entityBoundingBox.clip(eyePosition, endPosition).orElse(null);
			if (hitResult != null) {
				double distance = eyePosition.distanceTo(hitResult);
				if (distance < closestDistance) {
					closestDistance = distance;
					closestEntity = entity;
				}
			}
		}

		// Если найдена ближайшая сущность, на которую смотрит игрок
		if (closestEntity instanceof PookerEntity pooker && !(new Object() {
			public boolean checkGamemode(Entity _ent) {
				if (_ent instanceof ServerPlayer _serverPlayer) {
					return _serverPlayer.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
				} else if (_ent.level().isClientSide() && _ent instanceof Player _player) {
					return Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()) != null && Minecraft.getInstance().getConnection().getPlayerInfo(_player.getGameProfile().getId()).getGameMode() == GameType.SPECTATOR;
				}
				return false;
			}
		}.checkGamemode(player))) {
			System.out.println("Screamer");
			if(player.isUsingItem() && player.getUseItem().getItem() instanceof SpyglassItem){
				if (player instanceof ServerPlayer _plr0 && _plr0.level() instanceof ServerLevel && !_plr0.getAdvancements().getOrStartProgress(_plr0.server.getAdvancements().get(ResourceLocation.parse("prapor:pooker_achieve"))).isDone()) {
					if (player instanceof ServerPlayer _player) {
						System.out.println("Achivment");
						AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("prapor:pooker_achieve"));
						AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
						if (!_ap.isDone()) {
							for (String criteria : _ap.getRemainingCriteria())
								_player.getAdvancements().award(_adv, criteria);
						}
					}
				}
			}
			double x = pooker.getX();
			double y = pooker.getY();
			double z = pooker.getZ();
			FrameReturnerProcedure.execute(player.level(), player, 200);
			if (player.level() instanceof ServerLevel _level)
				_level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, (y + 1.5), z, 300, 1, 1, 1, 0.05);
			if (!player.level().isClientSide()) {
					player.level().playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:pooker_dissapear")), SoundSource.MASTER, 1, 1);
				}
				else {
					player.level().playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:pooker_dissapear")), SoundSource.MASTER, 1, 1, false);
				}
			if (!closestEntity.level().isClientSide())
				closestEntity.discard();
		}
	}
}

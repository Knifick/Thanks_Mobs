package net.knifick.praporupdate.procedures;

import net.knifick.praporupdate.entity.NarratorEntity;
import net.knifick.praporupdate.init.PraporModSounds;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;

import net.knifick.praporupdate.init.PraporModItems;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.Tags;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NarratorToAdoptProcedure {
	private static final Logger LOGGER = LogManager.getLogger();

	public static final Map<Item, Holder.Reference<SoundEvent>> musics = Map.ofEntries(
			Map.entry(Items.MUSIC_DISC_13, SoundEvents.MUSIC_DISC_13),
			Map.entry(Items.MUSIC_DISC_CAT, SoundEvents.MUSIC_DISC_CAT),
			Map.entry(Items.MUSIC_DISC_BLOCKS, SoundEvents.MUSIC_DISC_BLOCKS),
			Map.entry(Items.MUSIC_DISC_CHIRP, SoundEvents.MUSIC_DISC_CHIRP),
			Map.entry(Items.MUSIC_DISC_FAR, SoundEvents.MUSIC_DISC_FAR),
			Map.entry(Items.MUSIC_DISC_MALL, SoundEvents.MUSIC_DISC_MALL),
			Map.entry(Items.MUSIC_DISC_MELLOHI, SoundEvents.MUSIC_DISC_MELLOHI),
			Map.entry(Items.MUSIC_DISC_STAL, SoundEvents.MUSIC_DISC_STAL),
			Map.entry(Items.MUSIC_DISC_STRAD, SoundEvents.MUSIC_DISC_STRAD),
			Map.entry(Items.MUSIC_DISC_WARD, SoundEvents.MUSIC_DISC_WARD),
			Map.entry(Items.MUSIC_DISC_11, SoundEvents.MUSIC_DISC_11),
			Map.entry(Items.MUSIC_DISC_CREATOR_MUSIC_BOX, SoundEvents.MUSIC_DISC_CREATOR_MUSIC_BOX),
			Map.entry(Items.MUSIC_DISC_WAIT, SoundEvents.MUSIC_DISC_WAIT),
			Map.entry(Items.MUSIC_DISC_CREATOR, SoundEvents.MUSIC_DISC_CREATOR),
			Map.entry(Items.MUSIC_DISC_PRECIPICE, SoundEvents.MUSIC_DISC_PRECIPICE),
			Map.entry(Items.MUSIC_DISC_OTHERSIDE, SoundEvents.MUSIC_DISC_OTHERSIDE),
			Map.entry(Items.MUSIC_DISC_RELIC, SoundEvents.MUSIC_DISC_RELIC),
			Map.entry(Items.MUSIC_DISC_5, SoundEvents.MUSIC_DISC_5),
			Map.entry(Items.MUSIC_DISC_PIGSTEP, SoundEvents.MUSIC_DISC_PIGSTEP)
	);

	public static boolean isMusicDisc(ItemStack stack) {
		return !stack.isEmpty() && stack.is(Tags.Items.MUSIC_DISCS) ||
				stack.getItem() == PraporModItems.MUSIC_RECORD_N_42.get() ||
				stack.getItem() == PraporModItems.MUSIC_RECORD_THANKS_STREET.get();
	}

	public static boolean isDiscHave(LivingEntity entity) {
		return entity != null && isMusicDisc(entity.getItemInHand(InteractionHand.MAIN_HAND));
	}

	// Только для сервера: воспроизведение звука всем игрокам в радиусе
	private static void playServerSound(Level level, Entity entity, SoundEvent sound, SoundSource source, float volume, float pitch) {
		if (!(level instanceof ServerLevel serverLevel)) return;

		for (ServerPlayer player : serverLevel.players()) {
			if (player.distanceToSqr(entity) < 256 * 256) {
				player.connection.send(new net.minecraft.network.protocol.game.ClientboundSoundPacket(
						Holder.direct(sound),
						source,
						entity.getX(), entity.getY(), entity.getZ(),
						volume, pitch, serverLevel.getRandom().nextLong()
				));
			}
		}
	}

	// Только для сервера: остановка звука всем игрокам
	public static void stopServerSound(ServerLevel serverLevel, ResourceLocation soundId, SoundSource source) {
		for (ServerPlayer player : serverLevel.players()) {
			player.connection.send(new ClientboundStopSoundPacket(soundId, source));
		}
	}

	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity, Entity sourceentity, ItemStack itemstack) {
		if (entity == null || sourceentity == null) return;

		if (!(entity instanceof TamableAnimal _tamEnt && _tamEnt.isTame()) &&
				(sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PraporModItems.BOARD.get()) {

			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z),
							BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("prapor:adopt")),
							SoundSource.NEUTRAL, 1, 1);
				}
			}

			if (entity instanceof TamableAnimal _toTame && sourceentity instanceof Player _owner) {
				_toTame.tame(_owner);
			}
			itemstack.shrink(1);
		}
		else if (itemstack.getItem() == PraporModItems.BATTERY.get() &&
				entity instanceof NarratorEntity narrator &&
				!narrator.isPowered()) {

			if (!world.isClientSide()) {
				narrator.setPowerTimer(72000);
				narrator.setPowered(true);
				narrator.setAnimation("BatteryEnd");
				itemstack.shrink(1);
			}
		}
		else if (isMusicDisc(itemstack) &&
				entity instanceof NarratorEntity livingEntity &&
				!isDiscHave(livingEntity) &&
				livingEntity.isTame() &&
				livingEntity.isPowered()) {

			if (!world.isClientSide()) {
				// Звук вставки диска
				world.playSound(null, BlockPos.containing(x, y, z),
						PraporModSounds.PLUH.get(), SoundSource.BLOCKS, 1f, 1f);

				// Вставка диска в руку моба
				ItemStack copy = itemstack.copy();
				copy.setCount(1);
				livingEntity.setItemInHand(InteractionHand.MAIN_HAND, copy);
				livingEntity.setMusicPos(livingEntity.blockPosition());
				livingEntity.setMusic(true);

				livingEntity.setAnimation("empty");
				// Установка анимации
				if (itemstack.getItem() != Items.MUSIC_DISC_11 &&
						itemstack.getItem() != Items.MUSIC_DISC_5 &&
						itemstack.getItem() != Items.MUSIC_DISC_13) {
					livingEntity.setAnimation("music");
				} else {
					livingEntity.setAnimation("11");
				}

				// Воспроизведение музыки через пакеты
				if (world instanceof ServerLevel serverLevel) {
					SoundEvent sound = null;

					if (itemstack.is(Tags.Items.MUSIC_DISCS)) {
						sound = musics.get(itemstack.getItem()).value();
					}
					else if (itemstack.getItem() == PraporModItems.MUSIC_RECORD_N_42.get()) {
						sound = PraporModSounds.SOUND_TRACK.get();
					}
					else if (itemstack.getItem() == PraporModItems.MUSIC_RECORD_THANKS_STREET.get()) {
						sound = PraporModSounds.THANKS_STREET.get();
					}

					if (sound != null) {
						playServerSound(serverLevel, livingEntity, sound, SoundSource.RECORDS, 4.0f, 1.0f);
					}
				}

				itemstack.shrink(1);
			}
		}
		else if (entity instanceof NarratorEntity livingEntity && isDiscHave(livingEntity)) {
			if (!world.isClientSide()) {
				livingEntity.setMusic(false);
				livingEntity.setAnimation("forced_empty");
				livingEntity.setAnimation("empty");

				// Извлечение диска
				ItemStack extractedDisc = livingEntity.getItemInHand(InteractionHand.MAIN_HAND).copy();
				livingEntity.setItemInHand(InteractionHand.MAIN_HAND, ItemStack.EMPTY);

				// Создание предмета диска
				double xr = (RandomSource.create().nextDouble() * 0.2) - 0.1;
				double zr = (RandomSource.create().nextDouble() * 0.2) - 0.1;
				ItemEntity item = new ItemEntity(
						(Level) world,
						livingEntity.getX(),
						livingEntity.getY() + 0.5,
						livingEntity.getZ(),
						extractedDisc);
				item.setDeltaMovement(new Vec3(xr, 0.3, zr));
				world.addFreshEntity(item);

				// Остановка музыки через пакеты
				if (world instanceof ServerLevel serverLevel) {
					ResourceLocation soundId = null;

					if (extractedDisc.is(Tags.Items.MUSIC_DISCS)) {
						soundId = musics.get(extractedDisc.getItem()).value().getLocation();
					}
					else if (extractedDisc.getItem() == PraporModItems.MUSIC_RECORD_N_42.get()) {
						soundId = PraporModSounds.SOUND_TRACK.get().getLocation();
					}
					else if (extractedDisc.getItem() == PraporModItems.MUSIC_RECORD_THANKS_STREET.get()) {
						soundId = PraporModSounds.THANKS_STREET.get().getLocation();
					}

					if (soundId != null) {
						stopServerSound(serverLevel, soundId, SoundSource.RECORDS);
					}
				}
			}
		}
	}
}
package net.knifick.praporupdate.util.narrator;

import net.knifick.praporupdate.entity.NarratorEntity;
import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.init.PraporModSounds;
import net.knifick.praporupdate.procedures.NarratorToAdoptProcedure;
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.common.Tags;

public class MusicStopOnDeathHandler {

	public static void stopMusicForNarrator(NarratorEntity entity) {
		if (entity.level() instanceof ServerLevel serverLevel) {
			// Работаем только на сервере
			if (entity.level().isClientSide) return;

			if (!(entity instanceof NarratorEntity narrator)) return;

			// Проверяем, что моб воспроизводит музыку
			if (!narrator.isMusic()) return;

			// Отмечаем, что музыка должна быть остановлена
			narrator.setMusic(false);

			// Определяем ID звука для остановки
			ResourceLocation soundId = null;
			ItemStack discStack = narrator.getItemInHand(InteractionHand.MAIN_HAND);

			if (discStack.getItem() == PraporModItems.MUSIC_RECORD_N_42.get()) {
				soundId = PraporModSounds.SOUND_TRACK.getId();
			} else if (discStack.getItem() == PraporModItems.MUSIC_RECORD_THANKS_STREET.get()) {
				soundId = PraporModSounds.THANKS_STREET.getId();
			} else if (discStack.is(Tags.Items.MUSIC_DISCS)) {
				var soundEvent = NarratorToAdoptProcedure.musics.get(discStack.getItem());
				if (soundEvent != null) {
					soundId = soundEvent.value().getLocation();
				}
			}

			// Останавливаем звук для всех игроков
			if (soundId != null) {
				stopSoundForAllPlayers(serverLevel, soundId);
			}
		}
	}

	// Метод для остановки звука у всех игроков
	private static void stopSoundForAllPlayers(ServerLevel level, ResourceLocation soundId) {
		for (ServerPlayer player : level.getServer().getPlayerList().getPlayers()) {
			player.connection.send(new ClientboundStopSoundPacket(soundId, SoundSource.RECORDS));
		}
	}
}

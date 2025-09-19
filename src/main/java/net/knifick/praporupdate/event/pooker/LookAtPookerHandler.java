package net.knifick.praporupdate.event.pooker;

import com.mojang.blaze3d.pipeline.RenderPipeline;
import com.mojang.blaze3d.systems.RenderSystem;
import net.knifick.praporupdate.entity.PookerEntity;
import net.knifick.praporupdate.init.PraporModMobEffects;
import net.knifick.praporupdate.init.PraporModSounds;
import net.knifick.praporupdate.item.GuideBookItem;
import net.knifick.praporupdate.network.PraporModVariables;
import net.knifick.praporupdate.procedures.FrameReturnerProcedure;
import net.knifick.praporupdate.procedures.PookerPerTickProcedure;
import net.knifick.praporupdate.util.ironkin.ScreenShakeUtil;
import net.knifick.praporupdate.util.misc.UIHelper;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.SpyglassItem;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.pipeline.PipelineModifier;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;

import java.awt.*;
import java.util.List;

@EventBusSubscriber
public class LookAtPookerHandler {

	private static final double REACH_DISTANCE = 900.0D;
	private static final ResourceLocation POOKER_ADVANCEMENT = ResourceLocation.parse("prapor:pooker_achieve");
	private static final ResourceLocation POOKER_SOUND = ResourceLocation.parse("prapor:pooker_dissapear");

	@SubscribeEvent
	public static void onPlayerTick(PlayerTickEvent.Post event) {
		Player player = event.getEntity();
		if (player == null) return;

		Entity target = getLookedAtEntity(player, REACH_DISTANCE);
		if (target instanceof PookerEntity pooker && !isSpectator(player)) {
			handleLookAtPooker(player, pooker);
		}
		else {
			PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
			vars.isSee = false;
			vars.syncPlayerVariables(player);
		}
	}

	/**
	 * Возвращает ближайшую сущность, на которую смотрит игрок
	 */
	private static Entity getLookedAtEntity(Player player, double reachDistance) {
		Vec3 eyePos = player.getEyePosition(1.0F);
		Vec3 lookVec = player.getLookAngle();
		Vec3 endPos = eyePos.add(lookVec.scale(reachDistance));

		AABB searchBox = new AABB(eyePos, endPos);

		List<Entity> candidates = player.level().getEntities(
				player,
				searchBox,
				entity -> !entity.isSpectator() && entity.isPickable()
		);

		Entity closest = null;
		double closestDist = reachDistance;

		for (Entity entity : candidates) {
			AABB box = entity.getBoundingBox().inflate(entity.getPickRadius());
			Vec3 hit = box.clip(eyePos, endPos).orElse(null);
			if (hit != null) {
				double dist = eyePos.distanceTo(hit);
				if (dist < closestDist) {
					closestDist = dist;
					closest = entity;
				}
			}
		}

		return closest;
	}

	/**
	 * Проверка, является ли игрок спектатором
	 */
	private static boolean isSpectator(Entity entity) {
		if (entity instanceof ServerPlayer sp) {
			return sp.gameMode.getGameModeForPlayer() == GameType.SPECTATOR;
		}
		if (entity.level().isClientSide() && entity instanceof Player p) {
			var info = Minecraft.getInstance().getConnection().getPlayerInfo(p.getGameProfile().getId());
			return info != null && info.getGameMode() == GameType.SPECTATOR;
		}
		return false;
	}

	/**
	 * Основная логика при взгляде на PookerEntity
	 */
	private static void handleLookAtPooker(Player player, PookerEntity pooker) {
		// Добавляем в книгу
		GuideBookItem.addToBook(player, pooker, 1);

		// Достижение через подзорную трубу
		if (player.isUsingItem() && player.getUseItem().getItem() instanceof SpyglassItem) {
			grantAdvancement(player, POOKER_ADVANCEMENT);
		}

		if(player.tickCount%20==0) {
			player.playSound(SoundEvents.WARDEN_HEARTBEAT);
		}
		double distance = player.distanceTo(pooker);
		System.out.println(distance);
		if(distance<15)
			PookerPerTickProcedure.handlePlayerInteraction(player, pooker);
		PraporModVariables.PlayerVariables vars = player.getData(PraporModVariables.PLAYER_VARIABLES);
		vars.isSee = true;
		vars.syncPlayerVariables(player);
		player.addEffect(new MobEffectInstance(MobEffects.SLOWNESS, 2, 2));
//		screamer(pooker, player);
	}


	/**
	 * Выдача достижения
	 */
	private static void grantAdvancement(Player player, ResourceLocation id) {
		if (!(player instanceof ServerPlayer sp) || !(sp.level() instanceof ServerLevel)) return;

		AdvancementHolder adv = sp.getServer().getAdvancements().get(id);
		AdvancementProgress progress = sp.getAdvancements().getOrStartProgress(adv);

		if (!progress.isDone()) {
			for (String criteria : progress.getRemainingCriteria()) {
				sp.getAdvancements().award(adv, criteria);
			}
		}
	}



	public static void screamer(PookerEntity pooker, Player player){
		// Визуальные эффекты
		double x = pooker.getX();
		double y = pooker.getY();
		double z = pooker.getZ();

		FrameReturnerProcedure.execute(player.level(), player, 200);

		if (player.level() instanceof ServerLevel level) {
			level.sendParticles(ParticleTypes.CAMPFIRE_COSY_SMOKE, x, y + 1.5, z, 300, 1, 1, 1, 0.05);
		}

		player.playSound(PraporModSounds.POOKER_DISSAPEAR.get());
		//playSound(player, x, y, z, POOKER_SOUND);

		if (!pooker.level().isClientSide()) {
			pooker.discard();
		}
	}
	private static final ResourceLocation VIGNETTE_TEXTURE = ResourceLocation.fromNamespaceAndPath("prapor","textures/screens/vignette.png");
	private static final float SPEED = 1.0f; // Скорость пульсации
	private static final float FADE_SPEED = 0.0008f; // Скорость затухания
	private static long startTime = System.nanoTime();
	private static float pulseAlpha = 0.0f;
	private static float targetAlpha = 0f;         // целевая альфа (пульсация или 0)
	private static boolean fadingOut = false;

	@SubscribeEvent
	public static void onRenderOverlay(RenderGuiLayerEvent.Post event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;

		PraporModVariables.PlayerVariables vars = mc.player.getData(PraporModVariables.PLAYER_VARIABLES);
		var gui = event.getGuiGraphics();
		int w = gui.guiWidth();
		int h = gui.guiHeight();

		boolean hasFearEffect = vars.isSee;

		if (hasFearEffect) {
			long currentTime = System.nanoTime();
			float elapsedSeconds = (currentTime - startTime) / 1_000_000_000.0f;

			// Пульс даёт базовую амплитуду (0..1)
			float pulse = (float)(0.5f + 0.5f * Math.sin(elapsedSeconds * Math.PI * SPEED));
			targetAlpha = pulse; // цель — динамическая пульсация
		} else {
			targetAlpha = 0f; // цель — исчезновение
		}

		// Плавное приближение к цели
		if (pulseAlpha < targetAlpha) {
			pulseAlpha = Math.min(targetAlpha, pulseAlpha + FADE_SPEED);
		} else if (pulseAlpha > targetAlpha) {
			pulseAlpha = Math.max(targetAlpha, pulseAlpha - FADE_SPEED);
		}

		if (pulseAlpha > 0.01f) {
			int a = Mth.clamp((int)(pulseAlpha * 255f), 0, 255);
			int color = UIHelper.rgbaToColor(255, 255, 255, a);

			// x=0, y=0, u=0, v=0, w,h, texW=w, texH=h
			gui.blit(RenderPipelines.GUI_TEXTURED, VIGNETTE_TEXTURE, 0, 0, 0, 0, w, h, w, h, color);
		}
	}

	private static float mult = 0;
	private static final float fade = 0.05f;

	@SubscribeEvent
	public static void onComputeFov(ViewportEvent.ComputeFov event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;

		PraporModVariables.PlayerVariables vars = mc.player.getData(PraporModVariables.PLAYER_VARIABLES);
		if(vars.isSee) {
			if(mult<50){
				mult += fade;
			}
		}
		else {
			if(mult>0){
				mult -= fade*4;
			}
		}
		float fov = event.getFOV();

		// Например, уменьшим его на 20%
		fov -= mult;

		// Устанавливаем новое значение
		event.setFOV(fov);
	}

	@SubscribeEvent
	public static void onClientTick(ClientTickEvent.Post event){
		Minecraft mc = Minecraft.getInstance();
		if (mc.player == null) return;
		PraporModVariables.PlayerVariables vars = mc.player.getData(PraporModVariables.PLAYER_VARIABLES);
		if(vars.isSee) {
			if (mc.player.tickCount % 10 == 0) {
				ScreenShakeUtil.startShake(10, 3);
			}
		}
	}
}

package net.knifick.praporupdate.goal;

import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.pathfinder.Path;
import net.minecraft.world.phys.Vec3;
import net.knifick.praporupdate.entity.NarratorEntity;

import java.util.EnumSet;
import java.util.Random;

public class NarratorGoal extends Goal {
	private final NarratorEntity mob;
	private Player targetPlayer;
	private int state = 0;
	private int actionCooldown = 0;
	private final Random random = new Random();
	private int fleeTicks = 0;
	private static final int SOUND_DURATION = 60; // 3 секунды (60 тиков)

	public NarratorGoal(NarratorEntity mob) {
		this.mob = mob;
		this.setFlags(EnumSet.of(Flag.MOVE));
	}

	@Override
	public boolean canUse() {
		if (mob.isTame()) {
			return false;
		}

		this.targetPlayer = mob.level().getNearestPlayer(mob, 10);
		return targetPlayer != null
				&& !mob.getScaredPlayers().contains(targetPlayer.getUUID())
				&& mob.isPowered()
				&& mob.hasLineOfSight(targetPlayer);
	}

	@Override
	public boolean canContinueToUse() {
		return targetPlayer != null
				&& !targetPlayer.isRemoved()
				&& !mob.isTame()
				&& mob.isPowered()
				&& (state < 2 || fleeTicks > 0)
				&& mob.distanceTo(targetPlayer) < (state == 2 ? 30.0 : 15.0);
	}

	@Override
	public void start() {
		if (targetPlayer != null) {
			mob.getLookControl().setLookAt(targetPlayer, 30.0F, 30.0F);
		}
		state = 0;
		actionCooldown = 0;
		fleeTicks = 0;
	}

	@Override
	public void tick() {
		if (actionCooldown > 0) {
			actionCooldown--;
			return;
		}

		switch (state) {
			case 0 -> handleApproach();
			case 1 -> handleSoundAndScare();
			case 2 -> handleFlee();
		}
	}

	private void handleApproach() {
		if (targetPlayer == null || targetPlayer.isRemoved()) {
			this.stop();
			return;
		}

		if (mob.distanceTo(targetPlayer) < 2.0) {
			state = 1;
			actionCooldown = 20;
			return;
		}

		if (mob.getNavigation().isDone()) {
			Path path = mob.getNavigation().createPath(targetPlayer, 0);
			if (path != null) {
				mob.getNavigation().moveTo(path, 1.2);
			}
		}
	}

	private void handleSoundAndScare() {
		if (targetPlayer != null) {
			if(mob.getVariant()==1){
				mob.playSound(
						BuiltInRegistries.SOUND_EVENT.get(
								ResourceLocation.parse("prapor:pleaswwfthenarrator")
						),
						3.0F,
						1.0F
				);
			}
			else if(mob.getVariant()==2)
			{
				mob.playSound(
						BuiltInRegistries.SOUND_EVENT.get(
								ResourceLocation.parse("prapor:dictor_sound")
						),
						3.0F,
						1.0F
				);
			}
			mob.getScaredPlayers().add(targetPlayer.getUUID());
		}
		grantAdvancement();
		state = 2;
		actionCooldown = SOUND_DURATION; // Ждем окончания звука
		fleeTicks = 80; // 4 секунды побега
	}

	private void handleFlee() {
		if (fleeTicks <= 0) {
			this.stop();
			return;
		}

		handleFleeMovement();
		fleeTicks--;
	}

	private void grantAdvancement() {
		if (targetPlayer instanceof ServerPlayer serverPlayer) {
			AdvancementHolder advancement = serverPlayer.server.getAdvancements()
					.get(ResourceLocation.parse("prapor:narator_ach"));

			if (advancement != null) {
				AdvancementProgress progress = serverPlayer.getAdvancements()
						.getOrStartProgress(advancement);

				if (!progress.isDone()) {
					progress.getRemainingCriteria()
							.forEach(criterion ->
									serverPlayer.getAdvancements().award(advancement, criterion)
							);
				}
			}
		}
	}

	private void handleFleeMovement() {
		if (mob.getNavigation().isDone() && targetPlayer != null) {
			Vec3 awayVector = mob.position()
					.subtract(targetPlayer.position())
					.normalize()
					.scale(15.0)
					.add(
							random.nextFloat() * 10 - 5,
							0,
							random.nextFloat() * 10 - 5
					);

			Path path = mob.getNavigation().createPath(
					(int) Math.floor(awayVector.x()),
					(int) Math.floor(mob.getY()),
					(int) Math.floor(awayVector.z()),
					0
			);

			if (path != null) {
				mob.getNavigation().moveTo(path, 2.0);
			}
		}
	}

	@Override
	public void stop() {
		mob.getNavigation().stop();
		targetPlayer = null;
		state = 0;
		fleeTicks = 0;
	}

	@Override
	public boolean requiresUpdateEveryTick() {
		return true;
	}
}
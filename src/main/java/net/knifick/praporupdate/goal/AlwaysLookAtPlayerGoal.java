package net.knifick.praporupdate.goal;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.EnumSet;

public class AlwaysLookAtPlayerGoal extends Goal {
	private final Mob mob;
	private Player targetPlayer;

	public AlwaysLookAtPlayerGoal(Mob mob) {
		this.mob = mob;
		// Указываем, что цель влияет только на взгляд (LOOK), чтобы не конфликтовать с другими задачами.
		this.setFlags(EnumSet.of(Flag.LOOK));
	}

	@Override
	public boolean canUse() {
		// Ищем ближайшего игрока в радиусе (например, 64 блока)
		this.targetPlayer = mob.level().getNearestPlayer(mob, 128.0);
		return this.targetPlayer != null;
	}

	@Override
	public boolean canContinueToUse() {
		// Добавляем проверку дистанции и видимости игрока
		return this.targetPlayer != null
				&& this.targetPlayer.isAlive()
				&& mob.distanceTo(targetPlayer) <= 128.0
				&& mob.hasLineOfSight(targetPlayer);
	}

	@Override
	public void tick() {
		// Устанавливаем взгляд моба на игрока с углами поворота 30 градусов по горизонтали и вертикали
		//mob.getLookControl().setLookAt(targetPlayer, 360.0F, 30.0F);
		mob.lookAt(EntityAnchorArgument.Anchor.EYES, new Vec3(
				targetPlayer.getX(),
				targetPlayer.getY(),
				targetPlayer.getZ()));
	}
}


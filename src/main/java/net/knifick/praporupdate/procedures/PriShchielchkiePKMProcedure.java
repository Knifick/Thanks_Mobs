package net.knifick.praporupdate.procedures;

import net.minecraft.world.level.Level;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.projectile.ThrownPotion;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;

public class PriShchielchkiePKMProcedure {
	public static void execute(Entity entity) {
		if (entity == null)
			return;
		{
			Entity _shootFrom = entity;
			Level projectileLevel = _shootFrom.level();
			if (!projectileLevel.isClientSide()) {
				Projectile _entityToSpawn = new Object() {
					public Projectile getPotion(Level level) {
						ThrownPotion entityToSpawn = new ThrownPotion(EntityType.POTION, level);
						entityToSpawn.setItem(PotionContents.createItemStack(Items.SPLASH_POTION, Potions.LONG_POISON));
						return entityToSpawn;
					}
				}.getPotion(projectileLevel);
				_entityToSpawn.setPos(_shootFrom.getX(), _shootFrom.getEyeY() - 0.1, _shootFrom.getZ());
				_entityToSpawn.shoot(_shootFrom.getLookAngle().x, _shootFrom.getLookAngle().y, _shootFrom.getLookAngle().z, 1, 0);
				projectileLevel.addFreshEntity(_entityToSpawn);
			}
		}
	}
}

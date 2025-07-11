package net.knifick.praporupdate.procedures;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.Level;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.Entity;
import net.minecraft.sounds.SoundSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.BlockPos;

import net.knifick.praporupdate.entity.BastardEntity;

import java.util.Comparator;

public class BastardPerTickProcedure {
	public static void execute(LevelAccessor world, double x, double y, double z, Entity entity) {
		if (entity == null)
			return;
		Entity player = null;
		Entity itemEnt = null;
		ItemStack itemFromEnt = ItemStack.EMPTY;
		if ((entity instanceof BastardEntity _datEntI ? _datEntI.getEntityData().get(BastardEntity.DATA_State) : 0) > 0) {
			if (entity instanceof Mob _entity)
				_entity.getNavigation().stop();
		}
		itemEnt = (Entity) world.getEntitiesOfClass(ItemEntity.class, AABB.ofSize(new Vec3(x, (y - 0.5), z), 2, 2, 2), e -> true).stream().sorted(new Object() {
			Comparator<Entity> compareDistOf(double _x, double _y, double _z) {
				return Comparator.comparingDouble(_entcnd -> _entcnd.distanceToSqr(_x, _y, _z));
			}
		}.compareDistOf(x, (y - 0.5), z)).findFirst().orElse(null);
		if (!(itemEnt == null) && (new Object() {
			public ItemStack getItemStack(int sltid, Entity entity) {
				if (entity.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandler) {
					return _modHandler.getStackInSlot(sltid).copy();
				}
				return ItemStack.EMPTY;
			}
		}.getItemStack(0, entity)).getItem() == Blocks.AIR.asItem()) {
			itemFromEnt = (itemEnt instanceof ItemEntity _itemEnt ? _itemEnt.getItem() : ItemStack.EMPTY).copy();
			if (world instanceof Level _level) {
				if (!_level.isClientSide()) {
					_level.playSound(null, BlockPos.containing(x, y, z), BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.item.pickup")), SoundSource.NEUTRAL, 1, 1);
				} else {
					_level.playLocalSound(x, y, z, BuiltInRegistries.SOUND_EVENT.get(ResourceLocation.parse("entity.item.pickup")), SoundSource.NEUTRAL, 1, 1, false);
				}
			}
			if (entity.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandler) {
				ItemStack _setstack = itemFromEnt.copy();
				_setstack.setCount(itemFromEnt.getCount());
				_modHandler.setStackInSlot(0, _setstack);
			}
			if (!itemEnt.level().isClientSide()) {
				BastardRCMProcedure.addToTradeList(itemFromEnt);
				itemEnt.discard();
			}
		}
	}
}

package net.knifick.praporupdate.procedures;

import net.neoforged.neoforge.items.IItemHandlerModifiable;
import net.neoforged.neoforge.capabilities.Capabilities;

import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.AdvancementHolder;

import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.entity.PraporEntity;

public class PraporkaToPraporProcedure {
	public static void execute(LevelAccessor world, Entity entity, Entity sourceentity, ItemStack itemstack) {
		if (entity == null || sourceentity == null)
			return;
		ItemStack sign_praporka = ItemStack.EMPTY;
		String kostil = "";
		if ((entity instanceof TamableAnimal _tamEnt ? (Entity) _tamEnt.getOwner() : null) == sourceentity && (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Blocks.AIR.asItem()
				&& (new Object() {
					public ItemStack getItemStack(int sltid, Entity entity) {
						if (entity.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandler) {
							return _modHandler.getStackInSlot(sltid).copy();
						}
						return ItemStack.EMPTY;
					}
				}.getItemStack(0, entity)).getItem() == Items.SADDLE) {
			if (sourceentity instanceof ServerPlayer _player) {
				AdvancementHolder _adv = _player.server.getAdvancements().get(ResourceLocation.parse("prapor:prapor_ach"));
				if (_adv != null) {
					AdvancementProgress _ap = _player.getAdvancements().getOrStartProgress(_adv);
					if (!_ap.isDone()) {
						for (String criteria : _ap.getRemainingCriteria())
							_player.getAdvancements().award(_adv, criteria);
					}
				}
			}
			sourceentity.startRiding(entity);
		} else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == PraporModItems.PRAPORKA.get()) {
			if (!world.isClientSide()) {
				if (entity.getPersistentData().getBoolean("dance")) {
					entity.getPersistentData().putBoolean("dance", false);
					if (entity instanceof PraporEntity) {
						((PraporEntity) entity).setAnimation("empty");
					}
				} else {
					entity.getPersistentData().putBoolean("dance", true);
					if (entity instanceof PraporEntity) {
						((PraporEntity) entity).setAnimation("Dance");
					}
				}
			}
		} else if ((sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.CHICKEN
				|| (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.COOKED_CHICKEN) {
			entity.hurt(new DamageSource(world.holderOrThrow(DamageTypes.STARVE)), 100);
			if (!(sourceentity instanceof Player _plr ? _plr.getAbilities().instabuild : false)) {
				itemstack.shrink(1);
			}
		} else if ((entity instanceof TamableAnimal _tamEnt ? (Entity) _tamEnt.getOwner() : null) == sourceentity && (sourceentity instanceof LivingEntity _livEnt ? _livEnt.getMainHandItem() : ItemStack.EMPTY).getItem() == Items.SADDLE) {
			if (entity.getCapability(Capabilities.ItemHandler.ENTITY, null) instanceof IItemHandlerModifiable _modHandler) {
				ItemStack _setstack = new ItemStack(Items.SADDLE).copy();
				_setstack.setCount(1);
				_modHandler.setStackInSlot(0, _setstack);
			}
			if (entity instanceof PraporEntity animatable)
				animatable.setTexture("prapor_saddle");
			itemstack.shrink(1);
		}
	}
}

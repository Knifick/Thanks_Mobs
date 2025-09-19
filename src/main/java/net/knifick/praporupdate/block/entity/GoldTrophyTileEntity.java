package net.knifick.praporupdate.block.entity;

import net.knifick.praporupdate.block.GoldTrophyBlock;
import net.knifick.praporupdate.init.PraporModBlockEntities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.shapes.VoxelShape;

import net.neoforged.neoforge.items.wrapper.SidedInvWrapper;

import software.bernie.geckolib.animatable.GeoBlockEntity;
import software.bernie.geckolib.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.animatable.manager.AnimatableManager;
import software.bernie.geckolib.animatable.processing.AnimationController;
import software.bernie.geckolib.animatable.processing.AnimationTest;
import software.bernie.geckolib.animation.PlayState;
import software.bernie.geckolib.animation.RawAnimation;
import software.bernie.geckolib.util.GeckoLibUtil;

import javax.annotation.Nullable;
import java.util.stream.IntStream;

public class GoldTrophyTileEntity extends RandomizableContainerBlockEntity implements GeoBlockEntity, net.minecraft.world.WorldlyContainer {
	private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);
	private NonNullList<ItemStack> stacks = NonNullList.withSize(9, ItemStack.EMPTY);
	private final SidedInvWrapper handler = new SidedInvWrapper(this, null);

	public GoldTrophyTileEntity(BlockPos pos, BlockState state) {
		super(PraporModBlockEntities.GOLD_TROPHY.get(), pos, state);
	}

	// ===== GeckoLib 5: предикаты =====

	private PlayState loopPredicate(AnimationTest<GoldTrophyTileEntity> test) {
		// Читаем числовое состояние из блока и играем одноимённый луп "0"/"1"/... если 0 — стопим
		String anim = String.valueOf(this.getBlockState().getValue(GoldTrophyBlock.ANIMATION));
		if ("0".equals(anim)) return PlayState.STOP;
		return test.setAndContinue(RawAnimation.begin().thenLoop(anim));
	}

	private String prevAnim = "0";

	private PlayState procedurePredicate(AnimationTest<GoldTrophyTileEntity> test) {
		String anim = String.valueOf(this.getBlockState().getValue(GoldTrophyBlock.ANIMATION));

		// Если пришёл новый клип (не "0") или предыдущий завершился — проигрываем одноразово
		boolean newAnim = !anim.equals(prevAnim) && !"0".equals(anim);
		boolean stopped = test.controller().getAnimationState() == AnimationController.State.STOPPED;

		if (newAnim || (!"0".equals(anim) && stopped)) {
			if (newAnim) test.resetCurrentAnimation();
			test.setAnimation(RawAnimation.begin().thenPlay(anim));
			// Если клип завершился — сбрасываем property обратно в 0
			if (test.controller().getAnimationState() == AnimationController.State.STOPPED) {
				if (this.level != null && this.level.isClientSide) {
					// клиент сам не меняет blockstate — ждём сервер
				} else if (this.level != null) {
					IntegerProperty prop = this.getBlockState().getBlock().getStateDefinition().getProperty("animation") instanceof IntegerProperty ip ? ip : null;
					if (prop != null) {
						this.level.setBlock(this.getBlockPos(), this.getBlockState().setValue(prop, 0), 3);
					}
				}
				test.resetCurrentAnimation();
			}
			prevAnim = anim;
			return PlayState.CONTINUE;
		}

		if ("0".equals(anim)) {
			prevAnim = "0";
			return PlayState.STOP;
		}

		prevAnim = anim;
		return PlayState.CONTINUE;
	}

	@Override
	public void registerControllers(AnimatableManager.ControllerRegistrar data) {
		data.add(new AnimationController<>("controller", 0, this::loopPredicate));
		data.add(new AnimationController<>("procedurecontroller", 0, this::procedurePredicate));
	}

	@Override
	public AnimatableInstanceCache getAnimatableInstanceCache() {
		return this.cache;
	}

	// ====== NBT (BlockEntity продолжает работать через CompoundTag) ======

	@Override
	public void loadAdditional(ValueInput input) {
		super.loadAdditional(input);
		if (!this.tryLoadLootTable(input)) {
			this.stacks = NonNullList.withSize(this.getContainerSize(), ItemStack.EMPTY);
			ContainerHelper.loadAllItems(input, this.stacks);
		}
	}

	@Override
	public void saveAdditional(ValueOutput output) {
		super.saveAdditional(output);
		if (!this.trySaveLootTable(output)) {
			ContainerHelper.saveAllItems(output, this.stacks);
		}
	}

	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket() {
		return ClientboundBlockEntityDataPacket.create(this);
	}

	@Override
	public net.minecraft.nbt.CompoundTag getUpdateTag(HolderLookup.Provider lookup) {
		return this.saveWithFullMetadata(lookup);
	}

	// ====== Инвентарь/GUI ======

	@Override
	public int getContainerSize() {
		return stacks.size();
	}

	@Override
	public boolean isEmpty() {
		for (ItemStack s : this.stacks) if (!s.isEmpty()) return false;
		return true;
	}

	@Override
	public Component getDefaultName() {
		return Component.literal("gold_trophy");
	}

	@Override
	public int getMaxStackSize() {
		return 64;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory inv) {
		// если хочешь, чтобы GUI реально работал с этим BE-инвентарём, лучше сделать свой Menu,
		// но для теста подойдёт generic 9x3:
		return ChestMenu.threeRows(id, inv);
	}

	@Override
	public Component getDisplayName() {
		return Component.literal("Gold Trophy");
	}

	@Override
	protected NonNullList<ItemStack> getItems() {
		return this.stacks;
	}

	@Override
	protected void setItems(NonNullList<ItemStack> stacks) {
		this.stacks = stacks;
	}

	@Override
	public boolean canPlaceItem(int index, ItemStack stack) {
		return true;
	}

	@Override
	public int[] getSlotsForFace(Direction side) {
		return IntStream.range(0, this.getContainerSize()).toArray();
	}

	@Override
	public boolean canPlaceItemThroughFace(int index, ItemStack stack, @Nullable Direction dir) {
		return this.canPlaceItem(index, stack);
	}

	@Override
	public boolean canTakeItemThroughFace(int index, ItemStack stack, Direction dir) {
		return true;
	}

	public SidedInvWrapper getItemHandler() {
		return handler;
	}
}

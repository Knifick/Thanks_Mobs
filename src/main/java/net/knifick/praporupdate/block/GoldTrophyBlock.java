package net.knifick.praporupdate.block;

import com.mojang.serialization.MapCodec;
import net.knifick.praporupdate.init.PraporModBlockEntities;
import net.knifick.praporupdate.procedures.GoldTrophyPerTickProcedure;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;

public class GoldTrophyBlock extends BaseEntityBlock {
	// Свойства блока
	public static final IntegerProperty ANIMATION = IntegerProperty.create("animation", 0, 4);
	public static final Property<Direction> FACING = BlockStateProperties.HORIZONTAL_FACING;

	// MapCodec по стандарту 1.21.x
	public static final MapCodec<GoldTrophyBlock> CODEC = simpleCodec(GoldTrophyBlock::new);

	@Override public MapCodec<GoldTrophyBlock> codec() { return CODEC; }

	// Рекомендуемый конструктор с Properties
	public GoldTrophyBlock(BlockBehaviour.Properties props) {
		super(props
				.sound(SoundType.METAL)
				.strength(1f, 10f)
				.lightLevel(s -> 11)
				.noOcclusion()
				.isRedstoneConductor((bs, br, bp) -> false)
				.setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.fromNamespaceAndPath("prapor", "gold_trophy")))
		);
		this.registerDefaultState(this.stateDefinition.any()
				.setValue(FACING, Direction.NORTH)
				.setValue(ANIMATION, 0));
	}

	/** Временная совместимость со старой регистрацией; перейди на конструктор с Properties. */
	@Deprecated
	public GoldTrophyBlock() { this(BlockBehaviour.Properties.of()); }

	// Для GeckoLib/BE-рендеринга
	@Override
	public RenderShape getRenderShape(BlockState state) {
		return RenderShape.MODEL;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
		return PraporModBlockEntities.GOLD_TROPHY.get().create(pos, state);
	}

	@Override
	public VoxelShape getShape(BlockState state, net.minecraft.world.level.BlockGetter level, BlockPos pos, CollisionContext ctx) {
		return box(0, 0, 0, 16, 26, 16);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
		builder.add(FACING, ANIMATION);
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext ctx) {
		return this.defaultBlockState().setValue(FACING, ctx.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rot) {
		return state.setValue(FACING, rot.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror) {
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}

	@Override
	public void onPlace(BlockState state, Level level, BlockPos pos, BlockState old, boolean moving) {
		super.onPlace(state, level, pos, old, moving);
		level.scheduleTick(pos, this, 1);
	}

	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource rng) {
		super.tick(state, level, pos, rng);
		GoldTrophyPerTickProcedure.execute(level, pos.getX(), pos.getY(), pos.getZ());
		level.scheduleTick(pos, this, 5);
	}
}

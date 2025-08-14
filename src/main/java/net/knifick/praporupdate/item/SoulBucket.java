package net.knifick.praporupdate.item;

import net.knifick.praporupdate.init.PraporModItems;
import net.knifick.praporupdate.init.PraporModSounds;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;

public class SoulBucket extends Item{
    private final EntityType<? extends Mob> type;

    public SoulBucket(EntityType<? extends Mob> type, Item.Properties properties) {
        super(properties);
        this.type = type;
    }

    @Override
    public InteractionResult useOn(UseOnContext context) {
        Level level = context.getLevel();
        Player player = context.getPlayer();
        Minecraft mc = Minecraft.getInstance();
        mc.player.playSound(SoundEvents.BUCKET_FILL,
                1f,
                1f);
        mc.player.playSound(PraporModSounds.SOUL_SOUNDS.get(),
                1f,
                1f);
        if (!(level instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack stack = context.getItemInHand();
        BlockPos pos = context.getClickedPos().relative(context.getClickedFace());
        level.playSound(context.getPlayer(),
                pos,
                SoundEvents.BUCKET_FILL_AXOLOTL,
                SoundSource.PLAYERS,
                1f,
                1f);
        type.spawn(serverLevel, stack, context.getPlayer(), pos, MobSpawnType.SPAWN_EGG, true, false);

        player.setItemInHand(context.getHand(), new ItemStack(Items.BUCKET));
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        // аналогично SpawnEggItem.use, если хочешь спавн в воде
        return super.use(level, player, hand);
    }
}

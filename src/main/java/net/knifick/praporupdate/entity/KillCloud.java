package net.knifick.praporupdate.entity;

import net.knifick.praporupdate.init.PraporModParticleTypes;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageSources;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class KillCloud extends Entity {
    public static final EntityDataAccessor<Optional<UUID>> FILTER_UUID = SynchedEntityData.defineId(KillCloud.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Integer> VARIANT = SynchedEntityData.defineId(KillCloud.class, EntityDataSerializers.INT);
    private float radius = 1f;
    private int duration = 200;
    public KillCloud(EntityType<?> type, Level level) {
        super(type, level);
        this.noPhysics = true;
    }

    public void setPlayerUUID(@Nullable UUID player) {
        this.entityData.set(FILTER_UUID, Optional.ofNullable(player));
    }

    @Nullable
    public UUID getPlayerUUID() {
        return this.entityData.get(FILTER_UUID).orElse(null);
    }

    public void setVariant(int variant) {
        this.entityData.set(VARIANT, variant);
    }

    public int getVariant() {
        return this.entityData.get(VARIANT);
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(FILTER_UUID, Optional.empty());
        builder.define(VARIANT, 0);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        if(this.getPlayerUUID() != null)
            tag.putUUID("player_uuid", this.getPlayerUUID());
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        if (tag.contains("player_uuid"))
            this.setPlayerUUID(tag.getUUID("player_uuid"));
    }

    @Override
    public void onAddedToLevel() {
        super.onAddedToLevel();
        if(level() instanceof ServerLevel level){
            level.sendParticles(PraporModParticleTypes.EYES.get(),
                    getX(), getY()+0.3, getZ(), 10,
                    radius,0,radius, 0);
        }
    }

    @Override
    public void tick() {
        super.tick();
        if(tickCount>=duration) discard();
        if(level() instanceof ServerLevel level){
            if(tickCount<=duration-180){
                level.sendParticles(ParticleTypes.CAMPFIRE_SIGNAL_SMOKE,
                        getX(), getY()+0.1, getZ(), 4,
                        radius,0,radius, 0);
            }
            AABB box = getBoundingBox().inflate(radius*2.2, 0.1, radius*2.2);
            List<LivingEntity> players = level.getEntitiesOfClass(LivingEntity.class, box);
            DamageSource mantleSource = new DamageSource(level().holderOrThrow(ResourceKey.create(Registries.DAMAGE_TYPE, ResourceLocation.parse("prapor:mantle_hurt"))));
            for (LivingEntity player : players){
                if(!player.getUUID().equals(getPlayerUUID())){
                    float damage = getDamage(player);

                    player.hurt(mantleSource, damage);
                }
            }
        }
    }

    private float getDamage(LivingEntity entity) {
        float damage = 0.2f;
        if (getVariant() == 1) {
            // Чем меньше maxHealth, тем больше урон
            // Для свиньи (~10 хп) будет ~0.4f, для игрока (~20 хп) ~0.25f
            damage = Math.min(5f / entity.getMaxHealth(), 1f);
        } else if (getVariant() == 2) {
            // Чем больше maxHealth, тем больше урон
            // Для игрока (~20 хп) будет ~0.6f, для голема (~100 хп) ~3.3f
            damage = Math.min(entity.getMaxHealth() / 200.0f, 0.4f);
        }
        return damage;
    }
}
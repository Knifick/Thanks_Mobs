
/*
 *    MCreator note: This file will be REGENERATED on each build.
 */
package net.knifick.praporupdate.init;

import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.bus.api.SubscribeEvent;

import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Entity;
import net.minecraft.core.registries.Registries;

import net.knifick.praporupdate.entity.SoulEntity;
import net.knifick.praporupdate.entity.PraporEntity;
import net.knifick.praporupdate.entity.PookerEntity;
import net.knifick.praporupdate.entity.NarratorEntity;
import net.knifick.praporupdate.entity.DarkironkinEntity;
import net.knifick.praporupdate.entity.BrolemEntity;
import net.knifick.praporupdate.entity.BastardEntity;
import net.knifick.praporupdate.PraporMod;

@EventBusSubscriber(bus = EventBusSubscriber.Bus.MOD)
public class PraporModEntities {
	public static final DeferredRegister<EntityType<?>> REGISTRY = DeferredRegister.create(Registries.ENTITY_TYPE, PraporMod.MODID);
	public static final DeferredHolder<EntityType<?>, EntityType<PraporEntity>> PRAPOR = register("prapor",
			EntityType.Builder.<PraporEntity>of(PraporEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.6f, 1.6f));
	public static final DeferredHolder<EntityType<?>, EntityType<PookerEntity>> POOKER = register("pooker",
			EntityType.Builder.<PookerEntity>of(PookerEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(128).setUpdateInterval(3).fireImmune().sized(2f, 3f));
	public static final DeferredHolder<EntityType<?>, EntityType<SoulEntity>> SOUL = register("soul",
			EntityType.Builder.<SoulEntity>of(SoulEntity::new, MobCategory.AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune().sized(0.2f, 0.4f));
	public static final DeferredHolder<EntityType<?>, EntityType<NarratorEntity>> NARRATOR = register("narrator",
			EntityType.Builder.<NarratorEntity>of(NarratorEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.9f, 0.7f));
	public static final DeferredHolder<EntityType<?>, EntityType<BastardEntity>> BASTARD = register("bastard",
			EntityType.Builder.<BastardEntity>of(BastardEntity::new, MobCategory.AMBIENT).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(0.8f, 1.4f));
	public static final DeferredHolder<EntityType<?>, EntityType<BrolemEntity>> BROLEM = register("brolem",
			EntityType.Builder.<BrolemEntity>of(BrolemEntity::new, MobCategory.CREATURE).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3)

					.sized(1.4f, 2.9f));
	public static final DeferredHolder<EntityType<?>, EntityType<DarkironkinEntity>> DARKIRONKIN = register("darkironkin",
			EntityType.Builder.<DarkironkinEntity>of(DarkironkinEntity::new, MobCategory.MONSTER).setShouldReceiveVelocityUpdates(true).setTrackingRange(64).setUpdateInterval(3).fireImmune().sized(1.2f, 2.5f));

	// Start of user code block custom entities
	// End of user code block custom entities
	private static <T extends Entity> DeferredHolder<EntityType<?>, EntityType<T>> register(String registryname, EntityType.Builder<T> entityTypeBuilder) {
		return REGISTRY.register(registryname, () -> (EntityType<T>) entityTypeBuilder.build(registryname));
	}

	@SubscribeEvent
	public static void init(RegisterSpawnPlacementsEvent event) {
		PraporEntity.init(event);
		PookerEntity.init(event);
		SoulEntity.init(event);
		NarratorEntity.init(event);
		BastardEntity.init(event);
		BrolemEntity.init(event);
		DarkironkinEntity.init(event);
	}

	@SubscribeEvent
	public static void registerAttributes(EntityAttributeCreationEvent event) {
		event.put(PRAPOR.get(), PraporEntity.createAttributes().build());
		event.put(POOKER.get(), PookerEntity.createAttributes().build());
		event.put(SOUL.get(), SoulEntity.createAttributes().build());
		event.put(NARRATOR.get(), NarratorEntity.createAttributes().build());
		event.put(BASTARD.get(), BastardEntity.createAttributes().build());
		event.put(BROLEM.get(), BrolemEntity.createAttributes().build());
		event.put(DARKIRONKIN.get(), DarkironkinEntity.createAttributes().build());
	}
}

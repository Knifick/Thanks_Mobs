package net.knifick.praporupdate.network;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.knifick.praporupdate.PraporMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;
import net.minecraft.world.level.storage.DimensionDataStorage;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

@EventBusSubscriber
public class PraporModVariables {
	public static final DeferredRegister<AttachmentType<?>> ATTACHMENT_TYPES =
			DeferredRegister.create(NeoForgeRegistries.Keys.ATTACHMENT_TYPES, PraporMod.MODID);

	public static final Supplier<AttachmentType<PlayerVariables>> PLAYER_VARIABLES =
			ATTACHMENT_TYPES.register("player_variables", () ->
					AttachmentType.builder(PlayerVariables::new).build()
			);

	@SubscribeEvent
	public static void init(FMLCommonSetupEvent event) {
		PraporMod.addNetworkMessage(SavedDataSyncMessage.TYPE, SavedDataSyncMessage.STREAM_CODEC, SavedDataSyncMessage::handleData);
		PraporMod.addNetworkMessage(PlayerVariablesSyncMessage.TYPE, PlayerVariablesSyncMessage.STREAM_CODEC, PlayerVariablesSyncMessage::handleData);
	}

	@EventBusSubscriber
	public static class EventBusVariableHandlers {
		@SubscribeEvent
		public static void onPlayerLoggedInSyncPlayerVariables(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(player);
		}

		@SubscribeEvent
		public static void onPlayerRespawnedSyncPlayerVariables(PlayerEvent.PlayerRespawnEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(player);
		}

		@SubscribeEvent
		public static void onPlayerChangedDimensionSyncPlayerVariables(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player)
				player.getData(PLAYER_VARIABLES).syncPlayerVariables(player);
		}

		@SubscribeEvent
		public static void clonePlayer(PlayerEvent.Clone event) {
			PlayerVariables original = event.getOriginal().getData(PLAYER_VARIABLES);
			PlayerVariables clone = new PlayerVariables();

			clone.isFirst = original.isFirst;
			if (!event.isWasDeath()) {
				clone.screamAnimValue = original.screamAnimValue;
				clone.mantleTimer = original.mantleTimer;
				clone.isSee = original.isSee;
			}
			clone.hasSucker = original.hasSucker;
			clone.bmaceSlot = original.bmaceSlot;
			clone.suckCount = original.suckCount;
			clone.seenMobs = new HashMap<>(original.seenMobs);

			event.getEntity().setData(PLAYER_VARIABLES, clone);
		}

		@SubscribeEvent
		public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				MapVariables mapdata = MapVariables.get(player.level());
				WorldVariables worlddata = WorldVariables.get(player.level());
				if (mapdata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(0, mapdata.isWitherDead));
				if (worlddata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, false));
			}
		}

		@SubscribeEvent
		public static void onPlayerChangedDimension(PlayerEvent.PlayerChangedDimensionEvent event) {
			if (event.getEntity() instanceof ServerPlayer player) {
				WorldVariables worlddata = WorldVariables.get(player.level());
				if (worlddata != null)
					PacketDistributor.sendToPlayer(player, new SavedDataSyncMessage(1, false));
			}
		}
	}

	// ---------- WORLD VARIABLES ----------
	public static class WorldVariables extends SavedData {
		public static final String DATA_NAME = "prapor_worldvars";

		public static final SavedDataType<WorldVariables> ID =
				new SavedDataType<>(DATA_NAME, WorldVariables::new, Codec.unit(new WorldVariables()));

		static WorldVariables clientSide = new WorldVariables();

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof ServerLevel level) {
				PacketDistributor.sendToPlayersInDimension(level, new SavedDataSyncMessage(1, false));
			}
		}

		public static WorldVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel level) {
				DimensionDataStorage ds = level.getDataStorage();
				return ds.computeIfAbsent(ID);
			} else {
				return clientSide;
			}
		}
	}

	// ---------- MAP VARIABLES ----------
	public static class MapVariables extends SavedData {
		public static final String DATA_NAME = "prapor_mapvars";
		public boolean isWitherDead = false;

		public static final SavedDataType<MapVariables> ID =
				new SavedDataType<>(DATA_NAME, MapVariables::new,
						RecordCodecBuilder.create(i -> i.group(
								Codec.BOOL.fieldOf("isWitherDead").forGetter(v -> v.isWitherDead)
						).apply(i, b -> {
							MapVariables v = new MapVariables();
							v.isWitherDead = b;
							return v;
						})));

		static MapVariables clientSide = new MapVariables();

		public void syncData(LevelAccessor world) {
			this.setDirty();
			if (world instanceof Level lvl && !lvl.isClientSide()) {
				PacketDistributor.sendToAllPlayers(new SavedDataSyncMessage(0, this.isWitherDead));
			}
		}

		public static MapVariables get(LevelAccessor world) {
			if (world instanceof ServerLevel serverLevel) {
				return serverLevel.getServer().overworld().getDataStorage().computeIfAbsent(ID);
			} else {
				return clientSide;
			}
		}
	}

	// ---------- SAVED DATA SYNC PACKET ----------
	public record SavedDataSyncMessage(int dataType, boolean isWitherDead) implements CustomPacketPayload {
		public static final Type<SavedDataSyncMessage> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "saved_data_sync"));

		public static final StreamCodec<RegistryFriendlyByteBuf, SavedDataSyncMessage> STREAM_CODEC =
				StreamCodec.of(
						(buf, msg) -> {
							buf.writeInt(msg.dataType);
							buf.writeBoolean(msg.isWitherDead);
						},
						buf -> new SavedDataSyncMessage(buf.readInt(), buf.readBoolean())
				);

		@Override
		public Type<SavedDataSyncMessage> type() { return TYPE; }

		public static void handleData(final SavedDataSyncMessage msg, final IPayloadContext ctx) {
			if (ctx.flow() == PacketFlow.CLIENTBOUND) {
				ctx.enqueueWork(() -> {
					if (msg.dataType == 0) {
						MapVariables.clientSide.isWitherDead = msg.isWitherDead;
					} else {
						// world vars пока пустые
					}
				}).exceptionally(e -> {
					ctx.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}

	// ---------- PLAYER VARIABLES ----------
	public static class PlayerVariables {
		public boolean isFirst = false;
		public boolean hasSucker = false;
		public double screamAnimValue = 0;
		public int bmaceSlot = -1;
		public int suckCount = 0;
		public Map<String, Integer> seenMobs = new HashMap<>() {{
			put("prapor", 0);
			put("pooker", 0);
			put("soul", 0);
			put("narrator", 0);
			put("bastard", 0);
			put("brolem", 0);
			put("darkironkin", 0);
			put("sucker", 0);
			put("bob", 0);
			put("nymph", 0);
		}};
		public int mantleTimer = 0;
		public boolean isSee = false;

		public void syncPlayerVariables(Entity entity) {
			if (entity instanceof ServerPlayer sp)
				PacketDistributor.sendToPlayer(sp, new PlayerVariablesSyncMessage(this));
		}
	}

	// ---------- PLAYER VARIABLES SYNC PACKET ----------
	public record PlayerVariablesSyncMessage(PlayerVariables data) implements CustomPacketPayload {
		public static final Type<PlayerVariablesSyncMessage> TYPE =
				new Type<>(ResourceLocation.fromNamespaceAndPath(PraporMod.MODID, "player_variables_sync"));

		public static final StreamCodec<RegistryFriendlyByteBuf, PlayerVariablesSyncMessage> STREAM_CODEC =
				StreamCodec.of(
						(buf, msg) -> {
							var d = msg.data();
							buf.writeBoolean(d.isFirst);
							buf.writeBoolean(d.hasSucker);
							buf.writeDouble(d.screamAnimValue);
							buf.writeInt(d.bmaceSlot);
							buf.writeInt(d.suckCount);
							buf.writeVarInt(d.seenMobs.size());
							d.seenMobs.forEach((k, v) -> {
								buf.writeUtf(k);
								buf.writeInt(v);
							});
							buf.writeInt(d.mantleTimer);
							buf.writeBoolean(d.isSee);
						},
						buf -> {
							PlayerVariables d = new PlayerVariables();
							d.isFirst = buf.readBoolean();
							d.hasSucker = buf.readBoolean();
							d.screamAnimValue = buf.readDouble();
							d.bmaceSlot = buf.readInt();
							d.suckCount = buf.readInt();
							int n = buf.readVarInt();
							d.seenMobs.clear();
							for (int i = 0; i < n; i++) d.seenMobs.put(buf.readUtf(), buf.readInt());
							d.mantleTimer = buf.readInt();
							d.isSee = buf.readBoolean();
							return new PlayerVariablesSyncMessage(d);
						}
				);

		@Override
		public Type<PlayerVariablesSyncMessage> type() { return TYPE; }

		public static void handleData(final PlayerVariablesSyncMessage msg, final IPayloadContext ctx) {
			if (ctx.flow() == PacketFlow.CLIENTBOUND && msg.data != null) {
				ctx.enqueueWork(() -> {
					var pv = ctx.player().getData(PLAYER_VARIABLES);
					pv.isFirst = msg.data.isFirst;
					pv.hasSucker = msg.data.hasSucker;
					pv.screamAnimValue = msg.data.screamAnimValue;
					pv.bmaceSlot = msg.data.bmaceSlot;
					pv.suckCount = msg.data.suckCount;
					pv.seenMobs.clear();
					pv.seenMobs.putAll(msg.data.seenMobs);
					pv.mantleTimer = msg.data.mantleTimer;
					pv.isSee = msg.data.isSee;
				}).exceptionally(e -> {
					ctx.connection().disconnect(Component.literal(e.getMessage()));
					return null;
				});
			}
		}
	}
}

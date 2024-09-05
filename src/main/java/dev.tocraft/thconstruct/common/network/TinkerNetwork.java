package dev.tocraft.thconstruct.common.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import dev.tocraft.eomantle.network.NetworkWrapper;
import dev.tocraft.thconstruct.library.materials.definition.UpdateMaterialsPacket;
import dev.tocraft.thconstruct.library.materials.stats.UpdateMaterialStatsPacket;
import dev.tocraft.thconstruct.library.materials.traits.UpdateMaterialTraitsPacket;
import dev.tocraft.thconstruct.library.modifiers.UpdateModifiersPacket;
import dev.tocraft.thconstruct.library.modifiers.fluid.UpdateFluidEffectsPacket;
import dev.tocraft.thconstruct.library.tools.definition.UpdateToolDefinitionDataPacket;
import dev.tocraft.thconstruct.library.tools.layout.UpdateTinkerSlotLayoutsPacket;
import dev.tocraft.thconstruct.shared.network.GeneratePartTexturesPacket;
import dev.tocraft.thconstruct.smeltery.network.ChannelFlowPacket;
import dev.tocraft.thconstruct.smeltery.network.FaucetActivationPacket;
import dev.tocraft.thconstruct.smeltery.network.FluidUpdatePacket;
import dev.tocraft.thconstruct.smeltery.network.SmelteryFluidClickedPacket;
import dev.tocraft.thconstruct.smeltery.network.SmelteryTankUpdatePacket;
import dev.tocraft.thconstruct.smeltery.network.StructureErrorPositionPacket;
import dev.tocraft.thconstruct.smeltery.network.StructureUpdatePacket;
import dev.tocraft.thconstruct.tables.network.StationTabPacket;
import dev.tocraft.thconstruct.tables.network.TinkerStationRenamePacket;
import dev.tocraft.thconstruct.tables.network.TinkerStationSelectionPacket;
import dev.tocraft.thconstruct.tables.network.UpdateCraftingRecipePacket;
import dev.tocraft.thconstruct.tables.network.UpdateStationScreenPacket;
import dev.tocraft.thconstruct.tables.network.UpdateTinkerStationRecipePacket;
import dev.tocraft.thconstruct.tools.network.EntityMovementChangePacket;
import dev.tocraft.thconstruct.tools.network.InteractWithAirPacket;
import dev.tocraft.thconstruct.tools.network.TinkerControlPacket;

import javax.annotation.Nullable;

/**
 * Base network class for all tinkers logic
 * <p>
 * In general, if you need to send packets you should use your own network class
 */
public class TinkerNetwork extends NetworkWrapper {
  private static TinkerNetwork instance = null;

  private TinkerNetwork() {
    super(dev.tocraft.thconstruct.ThConstruct.getResource("network"));
  }

  /** Gets the instance of the network */
  public static TinkerNetwork getInstance() {
    if (instance == null) {
      throw new IllegalStateException("Attempt to call network getInstance before network is setup");
    }
    return instance;
  }

  /**
   * Called during mod construction to setup the network
   */
  public static void setup() {
    if (instance != null) {
      return;
    }
    instance = new TinkerNetwork();

    // shared
    instance.registerPacket(InventorySlotSyncPacket.class, InventorySlotSyncPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateNeighborsPacket.class, UpdateNeighborsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(GeneratePartTexturesPacket.class, GeneratePartTexturesPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SyncPersistentDataPacket.class, SyncPersistentDataPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // gadgets
    instance.registerPacket(EntityMovementChangePacket.class, EntityMovementChangePacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // tables
    instance.registerPacket(StationTabPacket.class, StationTabPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(TinkerStationRenamePacket.class, TinkerStationRenamePacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(UpdateCraftingRecipePacket.class, UpdateCraftingRecipePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(TinkerStationSelectionPacket.class, TinkerStationSelectionPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(UpdateTinkerSlotLayoutsPacket.class, UpdateTinkerSlotLayoutsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateStationScreenPacket.class, buf -> UpdateStationScreenPacket.INSTANCE, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateTinkerStationRecipePacket.class, UpdateTinkerStationRecipePacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // tools
    instance.registerPacket(UpdateMaterialsPacket.class, UpdateMaterialsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateMaterialStatsPacket.class, UpdateMaterialStatsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateMaterialTraitsPacket.class, UpdateMaterialTraitsPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateToolDefinitionDataPacket.class, UpdateToolDefinitionDataPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // modifiers
    instance.registerPacket(TinkerControlPacket.class, TinkerControlPacket::read, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(InteractWithAirPacket.class, InteractWithAirPacket::read, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(UpdateModifiersPacket.class, UpdateModifiersPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(UpdateFluidEffectsPacket.class, UpdateFluidEffectsPacket::new, NetworkDirection.PLAY_TO_CLIENT);

    // smeltery
    instance.registerPacket(FluidUpdatePacket.class, FluidUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(FaucetActivationPacket.class, FaucetActivationPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(ChannelFlowPacket.class, ChannelFlowPacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SmelteryTankUpdatePacket.class, SmelteryTankUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(StructureUpdatePacket.class, StructureUpdatePacket::new, NetworkDirection.PLAY_TO_CLIENT);
    instance.registerPacket(SmelteryFluidClickedPacket.class, SmelteryFluidClickedPacket::new, NetworkDirection.PLAY_TO_SERVER);
    instance.registerPacket(StructureErrorPositionPacket.class, StructureErrorPositionPacket::new, NetworkDirection.PLAY_TO_CLIENT);
  }

  /**
   * Sends a vanilla packet to the given player
   * @param player  Player
   * @param packet  Packet
   */
  public void sendVanillaPacket(Entity player, Packet<?> packet) {
    if (player instanceof ServerPlayer serverPlayer) {
      serverPlayer.connection.send(packet);
    }
  }

  /**
   * Same as {@link #sendToClientsAround(Object, ServerLevel, BlockPos)}, but checks that the world is a serverworld
   * @param msg       Packet to send
   * @param world     World instance
   * @param position  Target position
   */
  public void sendToClientsAround(Object msg, @Nullable LevelAccessor world, BlockPos position) {
    if (world instanceof ServerLevel server) {
      sendToClientsAround(msg, server, position);
    }
  }

  /**
   * Sends a packet to all entities tracking the given entity
   * @param msg     Packet
   * @param entity  Entity to check
   */
  @Override
  public void sendToTrackingAndSelf(Object msg, Entity entity) {
    this.network.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> entity), msg);
  }

  /**
   * Sends a packet to all entities tracking the given entity
   * @param msg     Packet
   * @param entity  Entity to check
   */
  @Override
  public void sendToTracking(Object msg, Entity entity) {
    this.network.send(PacketDistributor.TRACKING_ENTITY.with(() -> entity), msg);
  }

  /**
   * Sends a packet to the whole player list
   * @param targetedPlayer  Main player to target, if null uses whole list
   * @param playerList      Player list to use if main player is null
   * @param msg             Message to send
   */
  public void sendToPlayerList(@Nullable ServerPlayer targetedPlayer, PlayerList playerList, Object msg) {
    if (targetedPlayer != null) {
      sendTo(msg, targetedPlayer);
    } else {
      for (ServerPlayer player : playerList.getPlayers()) {
        sendTo(msg, player);
      }
    }
  }
}

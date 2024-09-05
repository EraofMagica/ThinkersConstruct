package dev.tocraft.thconstruct.common.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent.Context;
import dev.tocraft.eomantle.network.packet.IThreadsafePacket;
import dev.tocraft.thconstruct.library.tools.capability.PersistentDataCapability;

/** Packet to sync player persistent data to the client */
@RequiredArgsConstructor
public class SyncPersistentDataPacket implements IThreadsafePacket {
  private final CompoundTag data;

  public SyncPersistentDataPacket(FriendlyByteBuf buffer) {
    data = buffer.readNbt();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeNbt(data);
  }

  @Override
  public void handleThreadsafe(Context context) {
    HandleClient.handle(this);
  }

  /** Handles client side only code safely */
  private static class HandleClient {
    private static void handle(SyncPersistentDataPacket packet) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
        player.getCapability(PersistentDataCapability.CAPABILITY).ifPresent(data -> data.copyFrom(packet.data));
      }
    }
  }
}

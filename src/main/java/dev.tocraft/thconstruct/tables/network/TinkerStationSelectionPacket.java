package dev.tocraft.thconstruct.tables.network;

import lombok.RequiredArgsConstructor;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent.Context;
import dev.tocraft.eomantle.network.packet.IThreadsafePacket;
import dev.tocraft.thconstruct.library.tools.layout.StationSlotLayoutLoader;
import dev.tocraft.thconstruct.tables.menu.TinkerStationContainerMenu;

@RequiredArgsConstructor
public class TinkerStationSelectionPacket implements IThreadsafePacket {
  private final ResourceLocation layoutName;
  public TinkerStationSelectionPacket(FriendlyByteBuf buffer) {
    this.layoutName = buffer.readResourceLocation();
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeResourceLocation(this.layoutName);
  }

  @Override
  public void handleThreadsafe(Context context) {
    ServerPlayer sender = context.getSender();
    if (sender != null) {
      AbstractContainerMenu container = sender.containerMenu;
      if (container instanceof TinkerStationContainerMenu tinker) {
        tinker.setToolSelection(StationSlotLayoutLoader.getInstance().get(layoutName));
      }
    }
  }
}

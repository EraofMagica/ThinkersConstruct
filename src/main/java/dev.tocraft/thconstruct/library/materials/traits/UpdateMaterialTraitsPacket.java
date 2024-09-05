package dev.tocraft.thconstruct.library.materials.traits;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent.Context;
import dev.tocraft.eomantle.network.packet.IThreadsafePacket;
import dev.tocraft.thconstruct.library.materials.MaterialRegistry;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;

import java.util.HashMap;
import java.util.Map;

@Getter
@AllArgsConstructor
public class UpdateMaterialTraitsPacket implements IThreadsafePacket {
  protected final Map<MaterialId,MaterialTraits> materialToTraits;

  public UpdateMaterialTraitsPacket(FriendlyByteBuf buffer) {
    int materialCount = buffer.readInt();
    materialToTraits = new HashMap<>(materialCount);
    for (int i = 0; i < materialCount; i++) {
      MaterialId id = new MaterialId(buffer.readResourceLocation());
      MaterialTraits traits = MaterialTraits.read(buffer);
      materialToTraits.put(id, traits);
    }
  }

  @Override
  public void encode(FriendlyByteBuf buffer) {
    buffer.writeInt(materialToTraits.size());
    materialToTraits.forEach((materialId, traits) -> {
      buffer.writeResourceLocation(materialId);
      traits.write(buffer);
    });
  }

  @Override
  public void handleThreadsafe(Context context) {
    MaterialRegistry.updateMaterialTraitsFromServer(this);
  }
}

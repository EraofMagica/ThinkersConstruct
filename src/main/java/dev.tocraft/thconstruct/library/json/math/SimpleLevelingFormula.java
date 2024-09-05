package dev.tocraft.thconstruct.library.json.math;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import dev.tocraft.thconstruct.library.json.LevelingValue;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;

/** Implementation of {@link ModifierFormula} that uses a simpler formula for more efficiency, since not everything needs the flexibility of post fix */
record SimpleLevelingFormula(LevelingValue leveling, FallbackFormula formula) implements ModifierFormula {
  @Override
  public float apply(float... arguments) {
    return formula.apply(arguments);
  }

  @Override
  public float processLevel(ModifierEntry modifier) {
    return leveling.compute(modifier.getEffectiveLevel());
  }

  @Override
  public JsonObject serialize(JsonObject json, String[] variableNames) {
    return leveling.serialize(json);
  }

  @Override
  public void toNetwork(FriendlyByteBuf buffer) {
    // -1 means simple formula, 0+ means post fix formula
    buffer.writeShort(-1);
    leveling.toNetwork(buffer);
  }
}

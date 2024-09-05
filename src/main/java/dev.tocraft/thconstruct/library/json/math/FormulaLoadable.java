package dev.tocraft.thconstruct.library.json.math;

import com.google.gson.JsonObject;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import net.minecraft.network.FriendlyByteBuf;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.util.typed.TypedMap;
import dev.tocraft.thconstruct.library.json.math.ModifierFormula.FallbackFormula;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.function.BiFunction;

/** Loadable for a modifier formula */
public record FormulaLoadable(FallbackFormula fallback, String... variables) implements RecordLoadable<ModifierFormula> {
  @Override
  public ModifierFormula deserialize(JsonObject json, TypedMap context) {
    return ModifierFormula.deserialize(json, variables, fallback);
  }

  @Override
  public void serialize(ModifierFormula object, JsonObject json) {
    object.serialize(json, variables);
  }

  @Override
  public ModifierFormula decode(FriendlyByteBuf buffer, TypedMap context) throws DecoderException {
    return ModifierFormula.fromNetwork(buffer, variables.length, fallback);
  }

  @Override
  public void encode(FriendlyByteBuf buffer, ModifierFormula object) throws EncoderException {
    object.toNetwork(buffer);
  }

  /** Creates a builder instance */
  public <T> Builder<T> builder(BiFunction<ModifierFormula,ModifierCondition<IToolStackView>,T> constructor) {
    return new Builder<>(constructor, variables);
  }

  /** Builder for this module */
  public static class Builder<T> extends ModifierFormula.Builder<Builder<T>,T> {
    private final BiFunction<ModifierFormula,ModifierCondition<IToolStackView>,T> constructor;
    private Builder(BiFunction<ModifierFormula,ModifierCondition<IToolStackView>,T> constructor, String[] variableNames) {
      super(variableNames);
      this.constructor = constructor;
    }

    @Override
    protected T build(ModifierFormula formula) {
      return constructor.apply(formula, condition);
    }
  }
}

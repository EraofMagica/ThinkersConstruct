package dev.tocraft.thconstruct.library.materials.stats;

import net.minecraft.network.chat.Component;
import dev.tocraft.eomantle.data.loadable.primitive.FloatLoadable;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.primitive.StringLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

/** Stat type testing weird fields like strings along with a variable type */
public record ComplexTestStats(MaterialStatType<?> type, int num, float floating, String text) implements IMaterialStats {
  public static final RecordLoadable<ComplexTestStats> LOADER = RecordLoadable.create(
    MaterialStatType.CONTEXT_KEY.requiredField(),
    IntLoadable.ANY_SHORT.defaultField("num", 0, ComplexTestStats::num),
    FloatLoadable.ANY.defaultField("floating", 0f, ComplexTestStats::floating),
    StringLoadable.DEFAULT.nullableField("text", ComplexTestStats::text),
    ComplexTestStats::new);

  @Override
  public MaterialStatType<?> getType() {
    return type;
  }

  /** Makes a new type with the given name */
  public static MaterialStatType<ComplexTestStats> makeType(MaterialStatsId name) {
    return new MaterialStatType<ComplexTestStats>(name, type -> new ComplexTestStats(type, 0, 0f, ""), LOADER);
  }

  /** Makes a new type with the given name */
  public static MaterialStatType<ComplexTestStats> makeType(MaterialStatsId name, int num, float floating, String text) {
    return new MaterialStatType<ComplexTestStats>(name, type -> new ComplexTestStats(type, num, floating, text), LOADER);
  }

  @Override
  public List<Component> getLocalizedInfo() {
    return List.of();
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return List.of();
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {}
}

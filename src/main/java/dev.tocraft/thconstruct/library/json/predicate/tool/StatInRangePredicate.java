package dev.tocraft.thconstruct.library.json.predicate.tool;

import dev.tocraft.eomantle.data.loadable.primitive.FloatLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.StatsNBT;
import dev.tocraft.thconstruct.library.tools.stat.INumericToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

import java.util.function.Predicate;

/**
 * Predicate to check if a tool has the given stat within the range.
 * @see dev.tocraft.thconstruct.library.json.predicate.tool.StatInSetPredicate
 */
public record StatInRangePredicate(INumericToolStat<?> stat, float min, float max) implements Predicate<StatsNBT>, ToolStackPredicate {
  public static final RecordLoadable<StatInRangePredicate> LOADER = RecordLoadable.create(
    ToolStats.NUMERIC_LOADER.requiredField("stat", StatInRangePredicate::stat),
    FloatLoadable.ANY.defaultField("min", Float.NEGATIVE_INFINITY, StatInRangePredicate::min),
    FloatLoadable.ANY.defaultField("max", Float.POSITIVE_INFINITY, StatInRangePredicate::max),
    StatInRangePredicate::new);

  /**
   * Creates a predicate matching the exact value
   * @param stat  Stat
   * @param value Value to match
   * @return Predicate
   */
  public static StatInRangePredicate match(INumericToolStat<?> stat, float value) {
    return new StatInRangePredicate(stat, value, value);
  }

  /**
   * Creates a predicate matching the exact value
   * @param stat Stat
   * @param min  Min value
   * @return Predicate
   */
  public static StatInRangePredicate min(INumericToolStat<?> stat, float min) {
    return new StatInRangePredicate(stat, min, Float.POSITIVE_INFINITY);
  }

  /**
   * Creates a predicate matching the exact value
   * @param stat Stat
   * @param max  Max value
   * @return Predicate
   */
  public static StatInRangePredicate max(INumericToolStat<?> stat, float max) {
    return new StatInRangePredicate(stat, Float.NEGATIVE_INFINITY, max);
  }

  @Override
  public boolean test(StatsNBT statsNBT) {
    float value = statsNBT.get(stat).floatValue();
    return value >= min && value <= max;
  }

  @Override
  public boolean matches(IToolStackView tool) {
    return test(tool.getStats());
  }

  @Override
  public RecordLoadable<StatInRangePredicate> getLoader() {
    return LOADER;
  }
}

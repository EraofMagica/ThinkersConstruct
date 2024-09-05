package dev.tocraft.thconstruct.library.modifiers.modules.build;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import dev.tocraft.eomantle.data.loadable.primitive.EnumLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.json.LevelingValue;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModuleBuilder;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;
import dev.tocraft.thconstruct.library.tools.stat.INumericToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ModifierStatsBuilder;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

import java.util.List;
import java.util.Locale;

/** Module that boosts a tool stat */
public record StatBoostModule(INumericToolStat<?> stat, StatOperation operation, LevelingValue amount, ModifierCondition<IToolContext> condition) implements ToolStatsModifierHook, ModifierModule, ConditionalModule<IToolContext> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<StatBoostModule>defaultHooks(ModifierHooks.TOOL_STATS);
  public static RecordLoadable<StatBoostModule> LOADER = RecordLoadable.create(
    ToolStats.NUMERIC_LOADER.requiredField("stat", StatBoostModule::stat),
    new EnumLoadable<>(StatOperation.class).requiredField("operation", StatBoostModule::operation),
    LevelingValue.LOADABLE.directField(StatBoostModule::amount),
    ModifierCondition.CONTEXT_FIELD,
    StatBoostModule::new);

  @Override
  public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    if (condition.matches(context, modifier)) {
      operation.apply(builder, stat, amount.compute(modifier.getEffectiveLevel()));
    }
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<StatBoostModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder for adding stats */
  public static Builder add(INumericToolStat<?> stat) {
    return new Builder(stat, StatOperation.ADD);
  }

  /** Creates a builder for adding stats */
  public static Builder multiplyBase(INumericToolStat<?> stat) {
    return new Builder(stat, StatOperation.MULTIPLY_BASE);
  }

  /** Creates a builder for adding stats */
  public static Builder multiplyConditional(INumericToolStat<?> stat) {
    return new Builder(stat, StatOperation.MULTIPLY_CONDITIONAL);
  }

  /** Creates a builder for adding stats */
  public static Builder multiplyAll(INumericToolStat<?> stat) {
    return new Builder(stat, StatOperation.MULTIPLY_ALL);
  }

  /** enum representing a single stat boost */
  public enum StatOperation {
    ADD {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        stat.add(builder, value);
      }
    },
    PERCENT {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        stat.percent(builder, value);
      }
    },
    MULTIPLY_BASE {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        stat.multiply(builder, 1 + value);
      }
    },
    MULTIPLY_CONDITIONAL {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        builder.multiplier(stat, 1 + value);
      }
    },
    MULTIPLY_ALL {
      @Override
      public void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value) {
        stat.multiplyAll(builder, 1 + value);
      }
    };

    @Getter
    private final String name = name().toLowerCase(Locale.ROOT);

    /** Applies this boost type for the given values. */
    public abstract void apply(ModifierStatsBuilder builder, INumericToolStat<?> stat, float value);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Context<Builder> implements LevelingValue.Builder<StatBoostModule> {
    private final INumericToolStat<?> stat;
    private final StatOperation operation;

    @Override
    public StatBoostModule amount(float flat, float eachLevel) {
      return new StatBoostModule(stat, operation, new LevelingValue(flat, eachLevel), condition);
    }
  }
}

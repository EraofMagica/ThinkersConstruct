package dev.tocraft.thconstruct.library.json.variable.tool;

import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.stat.INumericToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

/**
 * Variable to get a stat from the tool
 */
public record ToolStatVariable(INumericToolStat<?> stat) implements ToolVariable {
  public static final RecordLoadable<ToolStatVariable> LOADER = RecordLoadable.create(ToolStats.NUMERIC_LOADER.requiredField("stat", ToolStatVariable::stat), ToolStatVariable::new);

  @Override
  public float getValue(IToolStackView tool) {
    return tool.getStats().get(stat).floatValue();
  }

  @Override
  public IGenericLoader<? extends ToolVariable> getLoader() {
    return LOADER;
  }
}

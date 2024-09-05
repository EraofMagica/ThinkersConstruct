package dev.tocraft.thconstruct.library.tools.definition;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import dev.tocraft.eomantle.data.loadable.ErrorFactory;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.module.WithHooks;
import dev.tocraft.thconstruct.library.tools.SlotType;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.ToolSlotsModule;
import dev.tocraft.thconstruct.library.tools.nbt.StatsNBT;
import dev.tocraft.thconstruct.library.tools.stat.IToolStat;

import java.util.List;

/**
 * Builder for a tool definition data
 */
@NoArgsConstructor(staticName = "builder")
@Accessors(fluent = true)
public class ToolDefinitionDataBuilder {
  private final StatsNBT.Builder bonuses = StatsNBT.builder();
  private final ImmutableList.Builder<WithHooks<ToolModule>> modules = ImmutableList.builder();


  /* Stats */

  /**
   * Adds a bonus to the builder
   */
  public <T> ToolDefinitionDataBuilder stat(IToolStat<T> stat, T value) {
    bonuses.set(stat, value);
    return this;
  }

  /**
   * Sets the starting slots to default
   */
  public ToolDefinitionDataBuilder smallToolStartingSlots() {
    module(new ToolSlotsModule(ImmutableMap.of(SlotType.UPGRADE, 3, SlotType.ABILITY, 1)));
    return this;
  }

  /**
   * Sets the starting slots to default
   */
  public ToolDefinitionDataBuilder largeToolStartingSlots() {
    module(new ToolSlotsModule(ImmutableMap.of(SlotType.UPGRADE, 2, SlotType.ABILITY, 1)));
    return this;
  }


  /* Modules */

  /** Adds a module to the definition with the given hooks */
  @SafeVarargs
  public final <T extends ToolModule> ToolDefinitionDataBuilder module(T module, ModuleHook<? super T>... hooks) {
    modules.add(new WithHooks<>(module, List.of(hooks)));
    return this;
  }

  /** Adds a module to the definition */
  public ToolDefinitionDataBuilder module(ToolModule module) {
    modules.add(new WithHooks<>(module, List.of()));
    return this;
  }

  /** Adds a module to the definition */
  public ToolDefinitionDataBuilder module(ToolModule... modules) {
    for (ToolModule module : modules) {
      module(module);
    }
    return this;
  }


  /** Builds the final definition JSON to serialize */
  public ToolDefinitionData build() {
    return new ToolDefinitionData(modules.build(), ErrorFactory.RUNTIME);
  }
}

package dev.tocraft.thconstruct.test;

import net.minecraft.world.item.Items;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinitionData;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.nbt.DummyToolStack;
import dev.tocraft.thconstruct.library.tools.nbt.MaterialNBT;
import dev.tocraft.thconstruct.library.tools.nbt.ModDataNBT;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.MultiplierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.StatsNBT;
import dev.tocraft.thconstruct.library.tools.stat.INumericToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ModifierStatsBuilder;

import java.util.List;

/** Helpers for running tests */
public class TestHelper {
  private TestHelper() {}

  /** Helper to fetch traits from the trait hook */
  public static List<ModifierEntry> getTraits(ToolDefinitionData data) {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    data.getHook(ToolHooks.TOOL_TRAITS).addTraits(ToolDefinition.EMPTY, MaterialNBT.EMPTY, builder);
    return builder.build().getModifiers();
  }

  public record ToolDefinitionStats(StatsNBT base, MultiplierNBT multipliers) {}

  /** Computes the stats for the given tool */
  public static ToolDefinitionStats buildStats(ToolDefinitionData data) {
    ModifierStatsBuilder builder = ModifierStatsBuilder.builder();
    data.getHook(ToolHooks.TOOL_STATS).addToolStats(new DummyToolStack(Items.AIR, ModifierNBT.EMPTY, new ModDataNBT()), builder);
    MultiplierNBT multipliers = builder.buildMultipliers();
    // cancel out multipliers on the base stats, as people expect base stats to be comparable to be usable in the modifier stats builder
    for (INumericToolStat<?> stat : multipliers.getContainedStats()) {
      stat.multiply(builder, 1 / multipliers.get(stat));
    }
    return new ToolDefinitionStats(builder.build(), multipliers);
  }
}

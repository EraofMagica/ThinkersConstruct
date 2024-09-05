package dev.tocraft.thconstruct.tools.modifiers.ability.tool;

import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;
import dev.tocraft.thconstruct.library.tools.stat.ModifierStatsBuilder;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

public class DuelWieldingModifier extends OffhandAttackModifier implements ToolStatsModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.TOOL_STATS);
  }

  @Override
  public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    // on two handed tools, take a larger hit to attack damage, smaller to attack speed
    if (context.hasTag(TinkerTags.Items.BROAD_TOOLS)) {
      ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 0.7);
      ToolStats.ATTACK_SPEED.multiplyAll(builder, 0.9);
    } else {
      // on one handed tools, 80% on both
      ToolStats.ATTACK_DAMAGE.multiplyAll(builder, 0.8);
      ToolStats.ATTACK_SPEED.multiplyAll(builder, 0.8);
    }
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }
}

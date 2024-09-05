package dev.tocraft.thconstruct.library.modifiers.hook.mining;

import net.minecraft.core.Direction;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.hook.display.TooltipModifierHook;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.Collection;

/** Hook used to increase mining speed of a block conditioned on the environment or the player */
public interface BreakSpeedModifierHook {
  /**
   * Called when break speed is being calculated to affect mining speed conditionally.
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link dev.tocraft.thconstruct.library.modifiers.hook.build.ToolStatsModifierHook}: Limited context, but effect shows in the tooltip automatically.</li>
   *   <li>{@link TooltipModifierHook}: Allows adding bonus effects to the tooltip.</li>
   * </ul>
   * @param tool                 Current tool instance
   * @param modifier             Modifier level
   * @param event                Event instance
   * @param sideHit              Side of the block that was hit
   * @param isEffective          If true, the tool is effective against this block type
   * @param miningSpeedModifier  Calculated modifier from potion effects such as haste and environment such as water, use for additive bonuses to ensure consistency with the mining speed stat
   */
  void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier);

  /** Merger that runs each hook in succession */
  record AllMerger(Collection<BreakSpeedModifierHook> modules) implements BreakSpeedModifierHook {
    @Override
    public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
      for (BreakSpeedModifierHook module : modules) {
        module.onBreakSpeed(tool, modifier, event, sideHit, isEffective, miningSpeedModifier);
      }
    }
  }
}

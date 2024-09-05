package dev.tocraft.thconstruct.library.modifiers.hook.mining;

import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.tools.context.ToolHarvestContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;
import java.util.Collection;

/** Hook called when a block is removed to allow the modifier to control the details of the block's removal. */
public interface RemoveBlockModifierHook {
  /**
   * Removes the block from the world
   * <br>
   * Alternatives:
   * <ul>
   *   <li>{@link BlockBreakModifierHook}: Called after the block is successfully removed.</li>
   * </ul>
   * @param tool      Tool used
   * @param modifier  Modifier level
   * @param context   Harvest context
   * @return  True to override the default block removing logic and stop all later modifiers from running. False to override default without breaking the block. Null to let default logic run
   */
  @Nullable
  Boolean removeBlock(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context);

  /** Merger that returns when the first hook finishes the action */
  record FirstMerger(Collection<RemoveBlockModifierHook> modules) implements RemoveBlockModifierHook {
    @Nullable
    @Override
    public Boolean removeBlock(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
      for (RemoveBlockModifierHook module : modules) {
        Boolean handled = module.removeBlock(tool, modifier, context);
        if (handled != null) {
          return handled;
        }
      }
      return null;
    }
  }
}

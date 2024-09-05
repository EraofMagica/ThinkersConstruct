package dev.tocraft.thconstruct.library.tools.capability.inventory;

import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.tools.capability.CompoundIndexHookIterator;
import dev.tocraft.thconstruct.library.tools.capability.fluid.ToolFluidCapability;
import dev.tocraft.thconstruct.library.tools.capability.inventory.ToolInventoryCapability.InventoryModifierHook;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

/**
 * Shared logic to iterate fluid capabilities for {@link ToolFluidCapability}
 */
abstract class InventoryModifierHookIterator<I> extends CompoundIndexHookIterator<InventoryModifierHook,I> {
  /** Entry from {@link #findHook(IToolStackView, int)}, will be set during or before iteration */
  protected ModifierEntry indexEntry = null;

  @Override
  protected int getSize(IToolStackView tool, InventoryModifierHook hook) {
    return hook.getSlots(tool, indexEntry);
  }
}

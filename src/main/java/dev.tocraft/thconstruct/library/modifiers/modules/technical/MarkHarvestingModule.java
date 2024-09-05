package dev.tocraft.thconstruct.library.modifiers.modules.technical;

import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.mining.BlockHarvestModifierHook;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;

import java.util.List;

/** Simple module with hooks form of {@link BlockHarvestModifierHook.MarkHarvesting}. */
public enum MarkHarvestingModule implements BlockHarvestModifierHook.MarkHarvesting, HookProvider {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MarkHarvestingModule>defaultHooks(ModifierHooks.BLOCK_HARVEST);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}

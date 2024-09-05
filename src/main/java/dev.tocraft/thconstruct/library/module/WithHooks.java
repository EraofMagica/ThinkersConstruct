package dev.tocraft.thconstruct.library.module;

import dev.tocraft.eomantle.data.loadable.Loadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IHaveLoader;

import java.util.List;

/**
 * Represents a module with a list of hooks, either defaulting or provided
 * @param module  Module instance
 * @param hooks   List of hooks, if empty uses default hooks
 */
public record WithHooks<T extends HookProvider>(T module, List<ModuleHook<?>> hooks) {
  /** Gets the list of hooks to use for this module */
  public List<ModuleHook<?>> getModuleHooks() {
    if (hooks.isEmpty()) {
      return module.getDefaultHooks();
    }
    return hooks;
  }

  /** Makes a loadable for a module with hooks */
  public static <T extends HookProvider & IHaveLoader> RecordLoadable<WithHooks<T>> makeLoadable(GenericLoaderRegistry<T> modules, Loadable<ModuleHook<?>> hooks) {
    return RecordLoadable.create(modules.directField(WithHooks::module), hooks.list(0).defaultField("hooks", List.of(), WithHooks::hooks), WithHooks::new);
  }
}

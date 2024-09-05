package dev.tocraft.thconstruct.library.tools.definition.module;

import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.WithHooks;

/**
 * Base interface for modules within the tool definition data
 */
public interface ToolModule extends IHaveLoader, HookProvider {
  /** Loader instance for any modules loadable in tools */
  GenericLoaderRegistry<ToolModule> LOADER = new GenericLoaderRegistry<>("Tool Module", false);
  /** Loadable for modules including hooks */
  RecordLoadable<WithHooks<ToolModule>> WITH_HOOKS = WithHooks.makeLoadable(LOADER, ToolHooks.LOADER);

}

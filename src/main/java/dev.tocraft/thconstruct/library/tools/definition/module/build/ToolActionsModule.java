package dev.tocraft.thconstruct.library.tools.definition.module.build;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.common.ToolAction;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Set;

/** Module that allows a tool to perform tool actions */
public record ToolActionsModule(Set<ToolAction> actions) implements ToolActionToolHook, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolActionsModule>defaultHooks(ToolHooks.TOOL_ACTION);
  public static final RecordLoadable<ToolActionsModule> LOADER = RecordLoadable.create(Loadables.TOOL_ACTION.set().requiredField("tool_actions", ToolActionsModule::actions), ToolActionsModule::new);

  public static ToolActionsModule of(ToolAction... actions) {
    return new ToolActionsModule(ImmutableSet.copyOf(actions));
  }

  @Override
  public RecordLoadable<ToolActionsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ToolAction toolAction) {
    return actions.contains(toolAction);
  }
}

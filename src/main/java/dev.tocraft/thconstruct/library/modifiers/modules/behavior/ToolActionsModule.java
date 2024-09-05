package dev.tocraft.thconstruct.library.modifiers.modules.behavior;

import com.google.common.collect.ImmutableSet;
import net.minecraftforge.common.ToolAction;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.behavior.ToolActionModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Set;

/**
 * Module that allows a modifier to perform tool actions
 */
public record ToolActionsModule(Set<ToolAction> actions, ModifierCondition<IToolStackView> condition) implements ToolActionModifierHook, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolActionsModule>defaultHooks(ModifierHooks.TOOL_ACTION);
  public static final RecordLoadable<ToolActionsModule> LOADER = RecordLoadable.create(
    Loadables.TOOL_ACTION.set().requiredField("tool_actions", ToolActionsModule::actions),
    ModifierCondition.TOOL_FIELD,
    ToolActionsModule::new);

  public ToolActionsModule(ToolAction... actions) {
    this(ImmutableSet.copyOf(actions), ModifierCondition.ANY_TOOL);
  }

  @Override
  public boolean canPerformAction(IToolStackView tool, ModifierEntry modifier, ToolAction toolAction) {
    return condition.matches(tool, modifier) && actions.contains(toolAction);
  }

  @Override
  public RecordLoadable<ToolActionsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}

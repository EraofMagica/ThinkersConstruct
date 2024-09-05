package dev.tocraft.thconstruct.library.modifiers.modules.build;

import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.SlotType;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;
import dev.tocraft.thconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;

/**
 * Module that adds extra modifier slots to a tool.
 */
public record ModifierSlotModule(SlotType type, int count, ModifierCondition<IToolContext> condition) implements VolatileDataModifierHook, ModifierModule, ConditionalModule<IToolContext> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ModifierSlotModule>defaultHooks(ModifierHooks.VOLATILE_DATA);
  public static final RecordLoadable<ModifierSlotModule> LOADER = RecordLoadable.create(
    SlotType.LOADABLE.requiredField("name", ModifierSlotModule::type),
    IntLoadable.ANY_SHORT.defaultField("count", 1, true, ModifierSlotModule::count),
    ModifierCondition.CONTEXT_FIELD,
    ModifierSlotModule::new);

  public ModifierSlotModule(SlotType type, int count) {
    this(type, count, ModifierCondition.ANY_CONTEXT);
  }

  public ModifierSlotModule(SlotType type) {
    this(type, 1);
  }

  @Override
  public Integer getPriority() {
    // show lower priority so they group together
    return 50;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    if (condition.matches(context, modifier)) {
      volatileData.addSlots(type, count * modifier.getLevel());
    }
  }

  @Override
  public RecordLoadable<ModifierSlotModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}

package dev.tocraft.thconstruct.library.modifiers.modules.behavior;

import net.minecraft.world.entity.EquipmentSlot;
import dev.tocraft.eomantle.data.loadable.primitive.EnumLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataKeys;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.function.Function;

/** Module to show the offhand for a tool that can interact using the offhand */
public enum ShowOffhandModule implements ModifierModule, EquipmentChangeModifierHook {
  /** Mode which will always show the offhand */
  ALLOW_BROKEN,
  /** Mode which will only show the offhand when the tool is not broken */
  DISALLOW_BROKEN;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ShowOffhandModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<ShowOffhandModule> LOADER = RecordLoadable.create(new EnumLoadable<>(ShowOffhandModule.class).requiredField("mode", Function.identity()), Function.identity());

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST && (!tool.isBroken() || this == ALLOW_BROKEN)) {
      ArmorLevelModule.addLevels(context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, 1);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.CHEST && (!tool.isBroken() || this == ALLOW_BROKEN)) {
      ArmorLevelModule.addLevels(context, TinkerDataKeys.SHOW_EMPTY_OFFHAND, -1);
    }
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }
}

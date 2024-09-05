package dev.tocraft.thconstruct.tools.modules;

import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.SingletonLoader;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.RawDataModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.utils.RestrictedCompoundTag;

import java.util.List;

/** Module implementing the one probe on held tools and helmets */
public enum TheOneProbeModule implements ModifierModule, RawDataModifierHook {
  INSTANCE;

  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<TheOneProbeModule>defaultHooks(ModifierHooks.RAW_DATA);
  public static final SingletonLoader<TheOneProbeModule> LOADER = new SingletonLoader<>(INSTANCE);
  public static final String TOP_NBT_HELMET = "theoneprobe";
  public static final String TOP_NBT_HAND = "theoneprobe_hand";

  @Override
  public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
    if (tool.hasTag(TinkerTags.Items.HELD)) {
      tag.putBoolean(TOP_NBT_HAND, true);
    }
    if (tool.hasTag(TinkerTags.Items.HELMETS)) {
      tag.putBoolean(TOP_NBT_HELMET, true);
    }
  }

  @Override
  public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {
    tag.remove(TOP_NBT_HAND);
    tag.remove(TOP_NBT_HELMET);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public SingletonLoader<TheOneProbeModule> getLoader() {
    return LOADER;
  }
}

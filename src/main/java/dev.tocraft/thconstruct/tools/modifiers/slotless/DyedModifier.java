package dev.tocraft.thconstruct.tools.modifiers.slotless;

import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import dev.tocraft.thconstruct.library.modifiers.impl.NoLevelsModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class DyedModifier extends NoLevelsModifier implements ModifierRemovalHook {
  private static final String FORMAT_KEY = dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("modifier", "dyed.formatted");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.REMOVE);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry) {
    ModDataNBT persistentData = tool.getPersistentData();
    ResourceLocation key = getId();
    if (persistentData.contains(key, Tag.TAG_INT)) {
      int color = persistentData.getInt(key);
      return applyStyle(Component.translatable(FORMAT_KEY, String.format("%06X", color)));
    }
    return super.getDisplayName();
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(getId());
    return null;
  }
}

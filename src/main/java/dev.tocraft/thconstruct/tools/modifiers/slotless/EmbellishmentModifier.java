package dev.tocraft.thconstruct.tools.modifiers.slotless;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import dev.tocraft.thconstruct.library.client.materials.MaterialTooltipCache;
import dev.tocraft.thconstruct.library.materials.MaterialRegistry;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariantId;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import dev.tocraft.thconstruct.library.modifiers.hook.build.RawDataModifierHook;
import dev.tocraft.thconstruct.library.modifiers.impl.NoLevelsModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModDataNBT;
import dev.tocraft.thconstruct.library.utils.RestrictedCompoundTag;

import javax.annotation.Nullable;

public class EmbellishmentModifier extends NoLevelsModifier implements ModifierRemovalHook, RawDataModifierHook {
  private static final String FORMAT_KEY = dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("modifier", "embellishment.formatted");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.REMOVE);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry) {
    MaterialVariantId materialVariant = MaterialVariantId.tryParse(tool.getPersistentData().getString(getId()));
    if (materialVariant != null) {
      return Component.translatable(FORMAT_KEY, MaterialTooltipCache.getDisplayName(materialVariant)).withStyle(style -> style.withColor(MaterialTooltipCache.getColor(materialVariant)));
    }
    return super.getDisplayName();
  }

  @Override
  public void addRawData(IToolStackView tool, ModifierEntry modifier, RestrictedCompoundTag tag) {
    // on build, migrate material redirects
    ModDataNBT data = tool.getPersistentData();
    ResourceLocation key = getId();
    MaterialVariantId materialVariant = MaterialVariantId.tryParse(data.getString(key));
    if (materialVariant != null) {
      MaterialId original = materialVariant.getId();
      MaterialId resolved = MaterialRegistry.getInstance().resolve(original);
      // instance check is safe here as resolve returns same instance if no redirect
      if (resolved != original) {
        data.putString(key, MaterialVariantId.create(resolved, materialVariant.getVariant()).toString());
      }
    }
  }

  @Override
  public void removeRawData(IToolStackView tool, Modifier modifier, RestrictedCompoundTag tag) {}

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(getId());
    return null;
  }
}

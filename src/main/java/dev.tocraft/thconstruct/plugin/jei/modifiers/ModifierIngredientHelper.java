package dev.tocraft.thconstruct.plugin.jei.modifiers;

import mezz.jei.api.ingredients.IIngredientHelper;
import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.subtypes.UidContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.plugin.jei.TConstructJEIConstants;
import dev.tocraft.thconstruct.tools.item.ModifierCrystalItem;

import javax.annotation.Nullable;

public class ModifierIngredientHelper implements IIngredientHelper<ModifierEntry> {
  @Override
  public IIngredientType<ModifierEntry> getIngredientType() {
    return TConstructJEIConstants.MODIFIER_TYPE;
  }

  @Override
  public String getDisplayName(ModifierEntry entry) {
    return entry.getDisplayName().getString();
  }

  @Override
  public String getUniqueId(ModifierEntry entry, UidContext context) {
    return entry.getId().toString();
  }

  @Override
  public ResourceLocation getResourceLocation(ModifierEntry entry) {
    return entry.getId();
  }
  @Override
  public ModifierEntry copyIngredient(ModifierEntry entry) {
    return entry;
  }

  @Override
  public String getErrorInfo(@Nullable ModifierEntry entry) {
    if (entry == null) {
      return "null";
    }
    return entry.getId().toString();
  }

  @Override
  public ItemStack getCheatItemStack(ModifierEntry ingredient) {
    return ModifierCrystalItem.withModifier(ingredient.getId());
  }
}

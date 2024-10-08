package dev.tocraft.thconstruct.library.recipe.partbuilder;

import net.minecraft.core.Registry;
import net.minecraft.world.item.ItemStack;
import dev.tocraft.eomantle.util.RegistryHelper;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariant;

import java.util.List;

/**
 * Part builder recipes that can show in JEI
 */
public interface IDisplayPartBuilderRecipe extends IPartBuilderRecipe {
  /** Gets the material variant required to craft this recipe */
  MaterialVariant getMaterial();

  /**
   * Gets a list of pattern items to display in the pattern slot
   * @return  Pattern items
   */
  default List<ItemStack> getPatternItems() {
    return RegistryHelper.getTagValueStream(Registry.ITEM, TinkerTags.Items.DEFAULT_PATTERNS).map(ItemStack::new).toList();
  }
}

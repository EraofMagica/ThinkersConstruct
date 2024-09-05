package dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing;

import net.minecraft.world.item.crafting.Ingredient;
import dev.tocraft.eomantle.data.loadable.common.IngredientLoadable;
import dev.tocraft.eomantle.data.loadable.field.LoadableField;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;

/**
 * Interface for serializing the recipe
 */
public interface ISpecializedRepairRecipe {
  /* Fields */
  LoadableField<Ingredient,ISpecializedRepairRecipe> TOOL_FIELD = IngredientLoadable.DISALLOW_EMPTY.requiredField("tool", ISpecializedRepairRecipe::getTool);
  LoadableField<MaterialId,ISpecializedRepairRecipe> REPAIR_MATERIAL_FIELD = MaterialId.PARSER.requiredField("repair_material", ISpecializedRepairRecipe::getRepairMaterial);

  /** Gets the tool ingredient from the recipe */
  Ingredient getTool();
  /** Gets the material ID from the recipe */
  MaterialId getRepairMaterial();
}

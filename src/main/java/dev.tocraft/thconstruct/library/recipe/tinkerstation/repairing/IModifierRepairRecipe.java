package dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing;

import net.minecraft.world.item.crafting.Ingredient;
import dev.tocraft.eomantle.data.loadable.common.IngredientLoadable;
import dev.tocraft.eomantle.data.loadable.field.LoadableField;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;

/**
 * Interface for serializing the recipe
 */
public interface IModifierRepairRecipe {
  /* Fields */
  LoadableField<ModifierId,IModifierRepairRecipe> MODIFIER_FIELD = ModifierId.PARSER.requiredField("modifier", IModifierRepairRecipe::getModifier);
  LoadableField<Ingredient,IModifierRepairRecipe> INGREDIENT_FIELD = IngredientLoadable.DISALLOW_EMPTY.requiredField("ingredient", IModifierRepairRecipe::getIngredient);
  LoadableField<Integer,IModifierRepairRecipe> REPAIR_AMOUNT_FIELD = IntLoadable.FROM_ONE.requiredField("repair_amount", IModifierRepairRecipe::getRepairAmount);

  /** Gets the modifier needed to perform this recipe */
  ModifierId getModifier();
  /** Gets the ingredient used to repair this item */
  Ingredient getIngredient();
  /** Gets the amount repaired per item */
  int getRepairAmount();
}

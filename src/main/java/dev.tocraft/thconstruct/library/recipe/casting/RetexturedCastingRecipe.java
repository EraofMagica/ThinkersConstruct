package dev.tocraft.thconstruct.library.recipe.casting;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.item.RetexturedBlockItem;
import dev.tocraft.eomantle.recipe.helper.ItemOutput;
import dev.tocraft.eomantle.recipe.helper.LoadableRecipeSerializer;
import dev.tocraft.eomantle.recipe.helper.TypeAwareRecipeSerializer;
import dev.tocraft.eomantle.recipe.ingredient.FluidIngredient;

/** Extension of item recipe that sets the result block to the input block */
public class RetexturedCastingRecipe extends ItemCastingRecipe {
  /** Loader instance */
  public static final RecordLoadable<RetexturedCastingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(), ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP, CAST_FIELD, FLUID_FIELD, RESULT_FIELD, COOLING_TIME_FIELD, CAST_CONSUMED_FIELD, SWITCH_SLOTS_FIELD,
    RetexturedCastingRecipe::new);

  public RetexturedCastingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, String group, Ingredient cast, FluidIngredient fluid, ItemOutput result, int coolingTime, boolean consumed, boolean switchSlots) {
    super(serializer, id, group, cast, fluid, result, coolingTime, consumed, switchSlots);
  }

  @Override
  public ItemStack assemble(ICastingContainer inv) {
    ItemStack result = getResultItem().copy();
    if (inv.getStack().getItem() instanceof BlockItem blockItem ) {
      return RetexturedBlockItem.setTexture(result, blockItem.getBlock());
    }
    return result;
  }
}

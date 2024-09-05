package dev.tocraft.thconstruct.library.recipe.entitymelting;

import lombok.RequiredArgsConstructor;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import dev.tocraft.eomantle.recipe.data.AbstractRecipeBuilder;
import dev.tocraft.eomantle.recipe.ingredient.EntityIngredient;

import java.util.function.Consumer;

/** Builder for entity melting recipes */
@RequiredArgsConstructor(staticName = "melting")
public class EntityMeltingRecipeBuilder extends AbstractRecipeBuilder<EntityMeltingRecipeBuilder> {
  private final EntityIngredient ingredient;
  private final FluidStack output;
  private final int damage;

  /** Creates a new builder doing 2 damage */
  public static EntityMeltingRecipeBuilder melting(EntityIngredient ingredient, FluidStack output) {
    return melting(ingredient, output, 2);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, Registry.FLUID.getKey(output.getFluid()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "entity_melting");
    consumer.accept(new LoadableFinishedRecipe<>(new EntityMeltingRecipe(id, ingredient, output, damage), EntityMeltingRecipe.LOADER, advancementId));
  }
}

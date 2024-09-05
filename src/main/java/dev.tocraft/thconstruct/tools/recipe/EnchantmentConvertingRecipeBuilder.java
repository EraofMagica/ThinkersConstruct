package dev.tocraft.thconstruct.tools.recipe;

import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.thconstruct.library.json.predicate.modifier.ModifierPredicate;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.recipe.worktable.AbstractSizedIngredientRecipeBuilder;

import java.util.function.Consumer;

/** Builder for an enchantment converting recipe */
@RequiredArgsConstructor(staticName = "converting")
public class EnchantmentConvertingRecipeBuilder extends AbstractSizedIngredientRecipeBuilder<EnchantmentConvertingRecipeBuilder> {
  private final String name;
  private final boolean matchBook;
  private boolean returnInput = false;
  @Setter
  @Accessors(fluent = true)
  private IJsonPredicate<ModifierId> modifierPredicate = ModifierPredicate.ANY;

  /**
   * If true, returns the unenchanted form of the item as an extra result
   */
  public EnchantmentConvertingRecipeBuilder returnInput() {
    returnInput = true;
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, dev.tocraft.thconstruct.ThConstruct.getResource(name));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    if (inputs.isEmpty()) {
      throw new IllegalStateException("Must have at least one input");
    }
    ResourceLocation advancementId = buildOptionalAdvancement(id, "modifiers");
    consumer.accept(new LoadableFinishedRecipe<>(new EnchantmentConvertingRecipe(id, name, inputs, matchBook, returnInput, modifierPredicate), EnchantmentConvertingRecipe.LOADER, advancementId));
  }
}

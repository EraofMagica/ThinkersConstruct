package dev.tocraft.thconstruct.library.recipe.molding;

import lombok.Getter;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import dev.tocraft.eomantle.data.loadable.common.IngredientLoadable;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.primitive.BooleanLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.recipe.ICommonRecipe;
import dev.tocraft.eomantle.recipe.helper.ItemOutput;
import dev.tocraft.eomantle.recipe.helper.LoadableRecipeSerializer;
import dev.tocraft.eomantle.recipe.helper.TypeAwareRecipeSerializer;

/** Recipe to combine two items on the top of a casting table, changing the first */
public class MoldingRecipe implements ICommonRecipe<IMoldingContainer> {
  public static final RecordLoadable<MoldingRecipe> LOADER = RecordLoadable.create(
    LoadableRecipeSerializer.TYPED_SERIALIZER.requiredField(),
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("material", MoldingRecipe::getMaterial),
    IngredientLoadable.ALLOW_EMPTY.defaultField("pattern", Ingredient.EMPTY, MoldingRecipe::getPattern),
    BooleanLoadable.INSTANCE.defaultField("pattern_consumed", false, false, MoldingRecipe::isPatternConsumed),
    ItemOutput.Loadable.REQUIRED_STACK.requiredField("result", r -> r.recipeOutput),
    MoldingRecipe::new);

  @Getter
  private final RecipeType<?> type;
  @Getter
  private final RecipeSerializer<?> serializer;
  @Getter
  private final ResourceLocation id;
  @Getter
  private final Ingredient material;
  @Getter
  private final Ingredient pattern;
  @Getter
  private final boolean patternConsumed;
  private final ItemOutput recipeOutput;

  public MoldingRecipe(TypeAwareRecipeSerializer<?> serializer, ResourceLocation id, Ingredient material, Ingredient pattern, boolean patternConsumed, ItemOutput recipeOutput) {
    this.type = serializer.getType();
    this.serializer = serializer;
    this.id = id;
    this.material = material;
    this.pattern = pattern;
    this.patternConsumed = pattern != Ingredient.EMPTY && patternConsumed;
    this.recipeOutput = recipeOutput;
  }

  @Override
  public boolean matches(IMoldingContainer inv, Level worldIn) {
    return material.test(inv.getMaterial()) && pattern.test(inv.getPattern());
  }

  @Override
  public NonNullList<Ingredient> getIngredients() {
    return NonNullList.of(Ingredient.EMPTY, material, pattern);
  }

  @Override
  public ItemStack getResultItem() {
    return recipeOutput.get();
  }
}

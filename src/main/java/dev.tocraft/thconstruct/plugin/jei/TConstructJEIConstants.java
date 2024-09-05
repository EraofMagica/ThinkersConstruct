package dev.tocraft.thconstruct.plugin.jei;

import mezz.jei.api.ingredients.IIngredientType;
import mezz.jei.api.ingredients.IIngredientTypeWithSubtypes;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.resources.ResourceLocation;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.recipe.alloying.AlloyRecipe;
import dev.tocraft.thconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import dev.tocraft.thconstruct.library.recipe.entitymelting.EntityMeltingRecipe;
import dev.tocraft.thconstruct.library.recipe.melting.MeltingRecipe;
import dev.tocraft.thconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe;
import dev.tocraft.thconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import dev.tocraft.thconstruct.library.recipe.molding.MoldingRecipe;
import dev.tocraft.thconstruct.library.recipe.partbuilder.IDisplayPartBuilderRecipe;
import dev.tocraft.thconstruct.library.recipe.partbuilder.Pattern;
import dev.tocraft.thconstruct.library.recipe.worktable.IModifierWorktableRecipe;

public class TConstructJEIConstants {
  public static final ResourceLocation PLUGIN = dev.tocraft.thconstruct.ThConstruct.getResource("jei_plugin");

  // ingredient types
  public static final IIngredientTypeWithSubtypes<Modifier,ModifierEntry> MODIFIER_TYPE = new IIngredientTypeWithSubtypes<>() {
    @Override
    public Class<? extends ModifierEntry> getIngredientClass() {
      return ModifierEntry.class;
    }

    @Override
    public Class<? extends Modifier> getIngredientBaseClass() {
      return Modifier.class;
    }

    @Override
    public Modifier getBase(ModifierEntry ingredient) {
      return ingredient.getModifier();
    }
  };
  public static final IIngredientType<Pattern> PATTERN_TYPE = () -> Pattern.class;

  // casting
  public static final RecipeType<IDisplayableCastingRecipe> CASTING_BASIN = type("casting_basin", IDisplayableCastingRecipe.class);
  public static final RecipeType<IDisplayableCastingRecipe> CASTING_TABLE = type("casting_table", IDisplayableCastingRecipe.class);
  public static final RecipeType<MoldingRecipe> MOLDING = type("molding", MoldingRecipe.class);

  // melting
  public static final RecipeType<MeltingRecipe> MELTING = type("melting", MeltingRecipe.class);
  public static final RecipeType<EntityMeltingRecipe> ENTITY_MELTING = type("entity_melting", EntityMeltingRecipe.class);
  public static final RecipeType<AlloyRecipe> ALLOY = type("alloy", AlloyRecipe.class);
  public static final RecipeType<MeltingRecipe> FOUNDRY = type("foundry", MeltingRecipe.class);

  // tinker station
  public static final RecipeType<IDisplayModifierRecipe> MODIFIERS = type("modifiers", IDisplayModifierRecipe.class);
  public static final RecipeType<SeveringRecipe> SEVERING = type("severing", SeveringRecipe.class);

  // part builder
  public static final RecipeType<IDisplayPartBuilderRecipe> PART_BUILDER = type("part_builder", IDisplayPartBuilderRecipe.class);

  // modifier workstation
  public static final RecipeType<IModifierWorktableRecipe> MODIFIER_WORKTABLE = type("worktable", IModifierWorktableRecipe.class);

  private static <T> RecipeType<T> type(String name, Class<T> clazz) {
    return RecipeType.create(dev.tocraft.thconstruct.ThConstruct.MOD_ID, name, clazz);
  }
}

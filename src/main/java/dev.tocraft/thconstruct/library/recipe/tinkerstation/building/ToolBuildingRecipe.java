package dev.tocraft.thconstruct.library.recipe.tinkerstation.building;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import dev.tocraft.eomantle.data.loadable.common.IngredientLoadable;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.recipe.helper.LoadableRecipeSerializer;
import dev.tocraft.eomantle.util.LogicHelper;
import dev.tocraft.thconstruct.library.json.TinkerLoadables;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariant;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import dev.tocraft.thconstruct.library.tools.definition.module.material.ToolPartsHook;
import dev.tocraft.thconstruct.library.tools.item.IModifiable;
import dev.tocraft.thconstruct.library.tools.nbt.MaterialNBT;
import dev.tocraft.thconstruct.library.tools.nbt.ToolStack;
import dev.tocraft.thconstruct.library.tools.part.IMaterialItem;
import dev.tocraft.thconstruct.library.tools.part.IToolPart;
import dev.tocraft.thconstruct.tables.TinkerTables;

import java.util.List;
import java.util.stream.IntStream;

/**
 * This recipe is used for crafting a set of parts into a tool
 */
@AllArgsConstructor
public class ToolBuildingRecipe implements ITinkerStationRecipe {
  public static final RecordLoadable<ToolBuildingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    LoadableRecipeSerializer.RECIPE_GROUP,
    TinkerLoadables.MODIFIABLE_ITEM.requiredField("result", r -> r.output),
    IntLoadable.FROM_ONE.defaultField("result_count", 1, true, r -> r.outputCount),
    IngredientLoadable.DISALLOW_EMPTY.list(0).defaultField("extra_requirements", List.of(), r -> r.ingredients),
    ToolBuildingRecipe::new);

  @Getter
  protected final ResourceLocation id;
  @Getter
  protected final String group;
  @Getter
  protected final IModifiable output;
  protected final int outputCount;
  protected final List<Ingredient> ingredients;

  /** Gets the additional recipe requirements beyond the tool parts */
  public List<Ingredient> getExtraRequirements() {
    return ingredients;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.toolBuildingRecipeSerializer.get();
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level worldIn) {
    if (!inv.getTinkerableStack().isEmpty()) {
      return false;
    }
    List<IToolPart> parts = ToolPartsHook.parts(output.getToolDefinition());
    int requiredInputs = parts.size() + ingredients.size();
    int maxInputs = inv.getInputCount();
    // disallow if we have no inputs, or if we have too few slots
    if (requiredInputs == 0 || requiredInputs > maxInputs) {
      return false;
    }
    // each part must match the given slot
    int i;
    int partSize = parts.size();
    for (i = 0; i < partSize; i++) {
      if (parts.get(i).asItem() != inv.getInput(i).getItem()) {
        return false;
      }
    }
    // remaining slots must match extra requirements
    for (; i < maxInputs; i++) {
      Ingredient ingredient = LogicHelper.getOrDefault(ingredients, i - partSize, Ingredient.EMPTY);
      if (!ingredient.test(inv.getInput(i))) {
        return false;
      }
    }

    return true;
  }

  @Override
  public ItemStack assemble(ITinkerStationContainer inv) {
    // first n slots contain parts
    List<MaterialVariant> materials = IntStream.range(0, ToolPartsHook.parts(output.getToolDefinition()).size())
                                               .mapToObj(i -> MaterialVariant.of(IMaterialItem.getMaterialFromStack(inv.getInput(i))))
                                               .toList();
    return ToolStack.createTool(output.asItem(), output.getToolDefinition(), new MaterialNBT(materials)).createStack(outputCount);
  }

  /** @deprecated Use {@link #assemble(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return new ItemStack(this.output);
  }
}

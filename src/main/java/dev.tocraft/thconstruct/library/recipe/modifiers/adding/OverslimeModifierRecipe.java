package dev.tocraft.thconstruct.library.recipe.modifiers.adding;

import lombok.Getter;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import dev.tocraft.eomantle.data.loadable.common.IngredientLoadable;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.util.RegistryHelper;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.recipe.RecipeResult;
import dev.tocraft.thconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import dev.tocraft.thconstruct.library.tools.nbt.ToolStack;
import dev.tocraft.thconstruct.tools.TinkerModifiers;
import dev.tocraft.thconstruct.tools.modifiers.slotless.OverslimeModifier;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static dev.tocraft.thconstruct.library.recipe.modifiers.adding.IDisplayModifierRecipe.withModifiers;

/**
 * Recipe to add overslime to a tool
 */
public class OverslimeModifierRecipe implements ITinkerStationRecipe, IDisplayModifierRecipe {
  private static final RecipeResult<ItemStack> AT_CAPACITY = RecipeResult.failure(dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("recipe", "overslime.at_capacity"));
  public static final RecordLoadable<OverslimeModifierRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("ingredient", r -> r.ingredient),
    IntLoadable.FROM_ONE.requiredField("restore_amount", r -> r.restoreAmount),
    OverslimeModifierRecipe::new);

  @Getter
  private final ResourceLocation id;
  private final Ingredient ingredient;
  private final int restoreAmount;

  public OverslimeModifierRecipe(ResourceLocation id, Ingredient ingredient, int restoreAmount) {
    this.id = id;
    this.ingredient = ingredient;
    this.restoreAmount = restoreAmount;
    ModifierRecipeLookup.addRecipeModifier(null, TinkerModifiers.overslime);
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    if (!inv.getTinkerableStack().is(TinkerTags.Items.DURABILITY)) {
      return false;
    }
    // must find at least one slime, but multiple is fine, as is empty slots
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public RecipeResult<ItemStack> getValidatedResult(ITinkerStationContainer inv) {
    ToolStack tool = inv.getTinkerable();
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    ModifierId overslimeId = TinkerModifiers.overslime.getId();
    ModifierEntry entry = tool.getModifier(overslimeId);
    // if the tool lacks true overslime, add overslime
    if (tool.getUpgrades().getLevel(overslimeId) == 0) {
      // however, if we have overslime though a trait and reached our cap, also do nothing
      if (entry.getLevel() > 0 && overslime.getShield(tool) >= overslime.getShieldCapacity(tool, entry)) {
        return AT_CAPACITY;
      }
      // truely add overslime, this will cost a slime crystal if full durability
      tool = tool.copy();
      tool.addModifier(TinkerModifiers.overslime.getId(), 1);
    } else {
      // ensure we are not at the cap already
      if (overslime.getShield(tool) >= overslime.getShieldCapacity(tool, entry)) {
        return AT_CAPACITY;
      }
      // copy the tool as we will change it later
      tool = tool.copy();
    }

    // see how much value is available, update overslime to the max possible
    int available = IncrementalModifierRecipe.getAvailableAmount(inv, ingredient, restoreAmount);
    overslime.addOverslime(tool, entry, available);
    return RecipeResult.success(tool.createStack(Math.min(inv.getTinkerableSize(), shrinkToolSlotBy())));
  }

  /**
   * Updates the input stacks upon crafting this recipe
   * @param result  Result from {@link #assemble(ITinkerStationContainer)}. Generally should not be modified
   * @param inv     Inventory instance to modify inputs
   */
  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    ToolStack tool = inv.getTinkerable();
    // if the original tool did not have overslime, its treated as having no slime
    int current = 0;
    OverslimeModifier overslime = TinkerModifiers.overslime.get();
    if (tool.getModifierLevel(overslime) != 0) {
      current = overslime.getShield(tool);
    }

    // how much did we actually consume?
    int maxNeeded = overslime.getShield(ToolStack.from(result)) - current;
    IncrementalModifierRecipe.updateInputs(inv, ingredient, maxNeeded, restoreAmount, ItemStack.EMPTY);
  }

  /** @deprecated use {@link #assemble(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.overslimeSerializer.get();
  }

  /* JEI display */
  /** Cache of modifier result, same for all overslime */
  private static final ModifierEntry RESULT = new ModifierEntry(TinkerModifiers.overslime, 1);
  /** Cache of input and output tools for display */
  private List<ItemStack> toolWithoutModifier, toolWithModifier = null;

  @Override
  public int getInputCount() {
    return 1;
  }

  @Override
  public List<ItemStack> getDisplayItems(int slot) {
    if (slot == 0) {
      return Arrays.asList(ingredient.getItems());
    }
    return Collections.emptyList();
  }
  @Override
  public List<ItemStack> getToolWithoutModifier() {
    if (toolWithoutModifier == null) {
      toolWithoutModifier = RegistryHelper.getTagValueStream(Registry.ITEM, TinkerTags.Items.DURABILITY).map(MAP_TOOL_FOR_RENDERING).toList();
    }
    return toolWithoutModifier;
  }

  @Override
  public List<ItemStack> getToolWithModifier() {
    if (toolWithModifier == null) {
      OverslimeModifier overslime = TinkerModifiers.overslime.get();
      List<ModifierEntry> result = List.of(RESULT);
      toolWithModifier = RegistryHelper.getTagValueStream(Registry.ITEM, TinkerTags.Items.DURABILITY)
                                       .map(MAP_TOOL_FOR_RENDERING)
                                       .map(stack -> withModifiers(stack, result, data -> overslime.setShield(data, restoreAmount)))
                                       .toList();
    }
    return toolWithModifier;
  }

  @Override
  public ModifierEntry getDisplayResult() {
    return RESULT;
  }
}

package dev.tocraft.thconstruct.tables.recipe;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import dev.tocraft.eomantle.data.loadable.common.IngredientLoadable;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.recipe.RecipeResult;
import dev.tocraft.thconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.IMutableTinkerStationContainer;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.nbt.ToolStack;
import dev.tocraft.thconstruct.tables.TinkerTables;

@RequiredArgsConstructor
public class TinkerStationDamagingRecipe implements ITinkerStationRecipe {
  public static final RecordLoadable<TinkerStationDamagingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("ingredient", r -> r.ingredient),
    IntLoadable.FROM_ONE.requiredField("damage_amount", r -> r.damageAmount),
    TinkerStationDamagingRecipe::new);
  private static final RecipeResult<ItemStack> BROKEN = RecipeResult.failure(dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("recipe", "damaging.broken"));

  @Getter
  private final ResourceLocation id;
  private final Ingredient ingredient;
  private final int damageAmount;

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    if (!inv.getTinkerableStack().is(TinkerTags.Items.DURABILITY)) {
      return false;
    }
    // must find at least one input, but multiple is fine, as is empty slots
    return IncrementalModifierRecipe.containsOnlyIngredient(inv, ingredient);
  }

  @Override
  public RecipeResult<ItemStack> getValidatedResult(ITinkerStationContainer inv) {
    ToolStack tool = inv.getTinkerable();
    if (tool.isBroken()) {
      return BROKEN;
    }
    // simply damage the tool directly
    tool = tool.copy();
    int maxDamage = IncrementalModifierRecipe.getAvailableAmount(inv, ingredient, damageAmount);
    ToolDamageUtil.directDamage(tool, maxDamage, null, inv.getTinkerableStack());
    return RecipeResult.success(tool.createStack());
  }

  @Override
  public int shrinkToolSlotBy() {
    return 1;
  }

  @Override
  public void updateInputs(ItemStack result, IMutableTinkerStationContainer inv, boolean isServer) {
    // how much did we actually consume?
    int damageTaken = ToolStack.from(result).getDamage() - inv.getTinkerable().getDamage();
    IncrementalModifierRecipe.updateInputs(inv, ingredient, damageTaken, damageAmount, ItemStack.EMPTY);
  }

  /** @deprecated Use {@link #getValidatedResult(ITinkerStationContainer)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationDamagingSerializer.get();
  }
}

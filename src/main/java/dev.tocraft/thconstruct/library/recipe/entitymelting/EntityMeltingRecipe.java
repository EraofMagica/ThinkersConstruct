package dev.tocraft.thconstruct.library.recipe.entitymelting;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import dev.tocraft.eomantle.data.loadable.common.FluidStackLoadable;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.recipe.ICustomOutputRecipe;
import dev.tocraft.eomantle.recipe.container.IEmptyContainer;
import dev.tocraft.eomantle.recipe.ingredient.EntityIngredient;
import dev.tocraft.thconstruct.library.recipe.TinkerRecipeTypes;
import dev.tocraft.thconstruct.smeltery.TinkerSmeltery;

import java.util.Collection;

/**
 * Recipe to melt an entity into a fluid
 */
@RequiredArgsConstructor
public class EntityMeltingRecipe implements ICustomOutputRecipe<IEmptyContainer> {
  public static final RecordLoadable<EntityMeltingRecipe> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    EntityIngredient.LOADABLE.requiredField("entity", r -> r.ingredient),
    FluidStackLoadable.REQUIRED_STACK.requiredField("result", r -> r.output),
    IntLoadable.FROM_ONE.defaultField("damage", 2, true, r -> r.damage),
    EntityMeltingRecipe::new);

  @Getter
  private final ResourceLocation id;
  @Getter
  private final EntityIngredient ingredient;
  @Getter
  private final FluidStack output;
  @Getter
  private final int damage;

  /**
   * Checks if the recipe matches the given type
   * @param type  Type
   * @return  True if it matches
   */
  public boolean matches(EntityType<?> type) {
    return ingredient.test(type);
  }

  /**
   * Gets the output for this recipe
   * @param entity  Entity being melted
   * @return  Fluid output
   */
  public FluidStack getOutput(LivingEntity entity) {
    return output.copy();
  }

  /**
   * Gets a collection of inputs for filtering in JEI
   * @return  Collection of types
   */
  public Collection<EntityType<?>> getInputs() {
    return ingredient.getTypes();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerSmeltery.entityMeltingSerializer.get();
  }

  @Override
  public RecipeType<?> getType() {
    return TinkerRecipeTypes.ENTITY_MELTING.get();
  }

  /** @deprecated use {@link #matches(EntityType)}*/
  @Deprecated
  @Override
  public boolean matches(IEmptyContainer inv, Level worldIn) {
    return false;
  }
}

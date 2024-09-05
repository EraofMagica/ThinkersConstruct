package dev.tocraft.thconstruct.library.recipe.casting.material;

import com.google.gson.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.crafting.RecipeSerializer;
import dev.tocraft.eomantle.recipe.data.AbstractRecipeBuilder;
import dev.tocraft.eomantle.recipe.helper.TypeAwareRecipeSerializer;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.tools.part.IMaterialItem;
import dev.tocraft.thconstruct.smeltery.TinkerSmeltery;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Builder for a composite part recipe, should exist for each part */
@RequiredArgsConstructor(staticName = "composite")
public class CompositeCastingRecipeBuilder extends AbstractRecipeBuilder<CompositeCastingRecipeBuilder> {
  private final IMaterialItem result;
  private final int itemCost;
  @Accessors(fluent = true)
  @Setter
  private MaterialStatsId castingStatConflict = null;
  private final TypeAwareRecipeSerializer<? extends CompositeCastingRecipe> serializer;

  public static CompositeCastingRecipeBuilder basin(IMaterialItem result, int itemCost) {
    return composite(result, itemCost, TinkerSmeltery.basinCompositeSerializer.get());
  }

  public static CompositeCastingRecipeBuilder table(IMaterialItem result, int itemCost) {
    return composite(result, itemCost, TinkerSmeltery.tableCompositeSerializer.get());
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, Registry.ITEM.getKey(result.asItem()));
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = this.buildOptionalAdvancement(id, "casting");
    consumer.accept(new LoadableFinishedRecipe<>(new CompositeCastingRecipe(serializer, id, group, result, itemCost, castingStatConflict), CompositeCastingRecipe.LOADER, advancementId));
  }

  private class Finished extends AbstractFinishedRecipe {
    public Finished(ResourceLocation ID, @Nullable ResourceLocation advancementID) {
      super(ID, advancementID);
    }

    @Override
    public void serializeRecipeData(JsonObject json) {
      if (!group.isEmpty()) {
        json.addProperty("group", group);
      }
      json.addProperty("result", Registry.ITEM.getKey(result.asItem()).toString());
      json.addProperty("item_cost", itemCost);
    }

    @Override
    public RecipeSerializer<?> getType() {
      return serializer;
    }
  }
}

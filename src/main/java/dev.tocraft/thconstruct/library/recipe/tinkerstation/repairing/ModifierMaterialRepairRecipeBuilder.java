package dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing;

import lombok.RequiredArgsConstructor;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import dev.tocraft.eomantle.recipe.data.AbstractRecipeBuilder;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.modifiers.util.LazyModifier;

import java.util.function.Consumer;

/** Builds a recipe to repair a tool using a modifier via a material */
@RequiredArgsConstructor(staticName = "repair")
public class ModifierMaterialRepairRecipeBuilder extends AbstractRecipeBuilder<ModifierMaterialRepairRecipeBuilder> {
  private final ModifierId modifier;
  private final MaterialId material;
  private final MaterialStatsId statType;

  public static ModifierMaterialRepairRecipeBuilder repair(LazyModifier modifier, MaterialId material, MaterialStatsId statType) {
    return repair(modifier.getId(), material, statType);
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer) {
    save(consumer, modifier);
  }

  /** Builds the recipe for the crafting table using a repair kit */
  public ModifierMaterialRepairRecipeBuilder saveCraftingTable(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new LoadableFinishedRecipe<>(new ModifierMaterialRepairKitRecipe(id, modifier, material, statType), ModifierMaterialRepairKitRecipe.LOADER, advancementId));
    return this;
  }

  @Override
  public void save(Consumer<FinishedRecipe> consumer, ResourceLocation id) {
    ResourceLocation advancementId = buildOptionalAdvancement(id, "tinker_station");
    consumer.accept(new LoadableFinishedRecipe<>(new ModifierMaterialRepairRecipe(id, modifier, material, statType), ModifierMaterialRepairRecipe.LOADER, advancementId));
  }
}

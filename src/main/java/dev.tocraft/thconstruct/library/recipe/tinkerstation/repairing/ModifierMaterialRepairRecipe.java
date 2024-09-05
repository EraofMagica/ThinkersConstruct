package dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing;

import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.materials.definition.LazyMaterial;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import dev.tocraft.thconstruct.library.tools.definition.module.material.MaterialRepairModule;
import dev.tocraft.thconstruct.library.tools.helper.ModifierUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.tables.recipe.TinkerStationRepairRecipe;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

/**
 * Recipe to repair a specialized tool in the tinker station
 */
public class ModifierMaterialRepairRecipe extends TinkerStationRepairRecipe implements IModifierMaterialRepairRecipe {
  public static final RecordLoadable<ModifierMaterialRepairRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), MODIFIER_FIELD, REPAIR_MATERIAL_FIELD, STAT_TYPE_FIELD, ModifierMaterialRepairRecipe::new);

  /** Tool that can be repaired with this recipe */
  @Getter
  private final ModifierId modifier;
  /** ID of material used in repairing */
  private final LazyMaterial repairMaterial;
  /** Stat type used for repairing, null means it will be fetched as the first available stat type */
  @Getter
  private MaterialStatsId statType;
  public ModifierMaterialRepairRecipe(ResourceLocation id, ModifierId modifier, MaterialId repairMaterialID, MaterialStatsId statType) {
    super(id);
    this.modifier = modifier;
    this.repairMaterial = LazyMaterial.of(repairMaterialID);
    this.statType = statType;
  }

  @Override
  public MaterialId getRepairMaterial() {
    return repairMaterial.getId();
  }

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    if (repairMaterial.isUnknown()) {
      return false;
    }
    // must have the modifier
    ItemStack tinkerable = inv.getTinkerableStack();
    if (!tinkerable.is(TinkerTags.Items.MODIFIABLE) || ModifierUtil.getModifierLevel(tinkerable, modifier) == 0) {
      return false;
    }
    return findMaterialItem(inv, repairMaterial.getId());
  }

  @Override
  protected float getRepairAmount(IToolStackView tool, MaterialId repairMaterial) {
    return MaterialRepairModule.getDurability(tool.getDefinition().getId(), repairMaterial, statType) * tool.getModifierLevel(modifier);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.modifierMaterialRepair.get();
  }


  /** Find the repair item in the inventory */
  private static boolean findMaterialItem(ITinkerStationContainer inv, MaterialId repairMaterial) {
    // validate that we have at least one material
    boolean found = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      // skip empty slots
      ItemStack stack = inv.getInput(i);
      if (stack.isEmpty()) {
        continue;
      }

      // ensure we have a material
      if (!repairMaterial.equals(TinkerStationRepairRecipe.getMaterialFrom(inv, i))) {
        return false;
      }
      found = true;
    }
    return found;
  }
}

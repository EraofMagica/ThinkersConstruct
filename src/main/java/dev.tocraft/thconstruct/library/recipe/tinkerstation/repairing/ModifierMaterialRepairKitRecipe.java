package dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing;

import com.mojang.datafixers.util.Pair;
import lombok.Getter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.tools.definition.module.material.MaterialRepairModule;
import dev.tocraft.thconstruct.library.tools.helper.ModifierUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ToolStack;
import dev.tocraft.thconstruct.library.tools.part.IMaterialItem;
import dev.tocraft.thconstruct.tables.recipe.CraftingTableRepairKitRecipe;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

/** Recipe for using a repair kit in a crafting station for a specialized tool */
public class ModifierMaterialRepairKitRecipe extends CraftingTableRepairKitRecipe implements IModifierMaterialRepairRecipe {
  public static final RecordLoadable<ModifierMaterialRepairKitRecipe> LOADER = RecordLoadable.create(ContextKey.ID.requiredField(), MODIFIER_FIELD, REPAIR_MATERIAL_FIELD, STAT_TYPE_FIELD, ModifierMaterialRepairKitRecipe::new);

  /** Tool that can be repaired with this recipe */
  @Getter
  private final ModifierId modifier;
  /** ID of material used in repairing */
  @Getter
  private final MaterialId repairMaterial;
  /** Stat type used for repairing, null means it will be fetched as the first available stat type */
  @Getter
  private final MaterialStatsId statType;
  public ModifierMaterialRepairKitRecipe(ResourceLocation id, ModifierId modifier, MaterialId repairMaterial, MaterialStatsId statType) {
    super(id);
    this.modifier = modifier;
    this.repairMaterial = repairMaterial;
    this.statType = statType;
  }

  @Override
  protected boolean toolMatches(ItemStack stack) {
    return stack.is(TinkerTags.Items.DURABILITY) && ModifierUtil.getModifierLevel(stack, modifier) > 0;
  }

  @Override
  public boolean matches(CraftingContainer inv, Level worldIn) {
    Pair<ToolStack, ItemStack> inputs = getRelevantInputs(inv);
    return inputs != null && repairMaterial.equals(IMaterialItem.getMaterialFromStack(inputs.getSecond()).getId());
  }

  @Override
  protected float getRepairAmount(IToolStackView tool, ItemStack repairStack) {
    return MaterialRepairModule.getDurability(tool.getDefinition().getId(), repairMaterial.getId(), statType) * tool.getModifierLevel(modifier);
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.craftingModifierMaterialRepair.get();
  }
}

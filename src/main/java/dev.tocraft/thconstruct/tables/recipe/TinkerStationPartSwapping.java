package dev.tocraft.thconstruct.tables.recipe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.materials.definition.IMaterial;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariant;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariantId;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import dev.tocraft.thconstruct.library.recipe.RecipeResult;
import dev.tocraft.thconstruct.library.recipe.casting.material.MaterialCastingLookup;
import dev.tocraft.thconstruct.library.recipe.material.MaterialRecipe;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationContainer;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.ITinkerStationRecipe;
import dev.tocraft.thconstruct.library.tools.definition.module.material.MaterialRepairModule;
import dev.tocraft.thconstruct.library.tools.definition.module.material.ToolPartsHook;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.item.IModifiable;
import dev.tocraft.thconstruct.library.tools.nbt.ToolStack;
import dev.tocraft.thconstruct.library.tools.part.IToolPart;
import dev.tocraft.thconstruct.tables.TinkerTables;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Recipe that replaces a tool part with another
 */
@AllArgsConstructor
public class TinkerStationPartSwapping implements ITinkerStationRecipe {
  private static final RecipeResult<ItemStack> TOO_MANY_PARTS = RecipeResult.failure(dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("recipe", "part_swapping.too_many_parts"));

  @Getter
  protected final ResourceLocation id;

  @Override
  public boolean matches(ITinkerStationContainer inv, Level world) {
    ItemStack tinkerable = inv.getTinkerableStack();
    if (tinkerable.isEmpty() || !tinkerable.is(TinkerTags.Items.MULTIPART_TOOL)|| !(tinkerable.getItem() instanceof IModifiable modifiable)) {
      return false;
    }
    // get the list of parts, empty means its not multipart
    List<IToolPart> parts = ToolPartsHook.parts(modifiable.getToolDefinition());
    if (parts.isEmpty()) {
      return false;
    }

    // we have two concerns on part swapping:
    // part must be valid in the tool, and only up to one part can be swapped at once
    boolean foundItem = false;
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // too many items
        if (foundItem) {
          return false;
        }
        // part not in list
        Item item = stack.getItem();
        if (!(item instanceof IToolPart) || parts.stream().noneMatch(p -> p.asItem() == item)) {
          return false;
        }
        foundItem = true;
      }
    }
    return foundItem;
  }

  /** @deprecated Use {@link #assemble(ITinkerStationContainer)}  */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeResult<ItemStack> getValidatedResult(ITinkerStationContainer inv) {
    // copy the tool NBT to ensure the original tool is intact
    ToolStack original = inv.getTinkerable();
    List<IToolPart> parts = ToolPartsHook.parts(original.getDefinition());

    // prevent part swapping on large tools in small tables
    if (parts.size() > inv.getInputCount()) {
      return TOO_MANY_PARTS;
    }

    // actual part swap logic
    for (int i = 0; i < inv.getInputCount(); i++) {
      ItemStack stack = inv.getInput(i);
      if (!stack.isEmpty()) {
        // not tool part, should never happen
        Item item = stack.getItem();
        if (!(item instanceof IToolPart part)) {
          return RecipeResult.pass();
        }

        // ensure the part is valid
        MaterialVariantId partVariant = part.getMaterial(stack);
        if (partVariant.equals(IMaterial.UNKNOWN_ID)) {
          return RecipeResult.pass();
        }

        // we have a part and its not at this index, find the first copy of this part
        // means slot only matters if a tool uses a part twice
        int index = i;
        if (i >= parts.size() || parts.get(i).asItem() != item) {
          index = IntStream.range(0, parts.size())
                           .filter(pi -> parts.get(pi).asItem() == item)
                           .findFirst().orElse(-1);
          if (index == -1) {
            return RecipeResult.pass();
          }
        }

        // ensure there is a change in the part or we are repairing the tool, note we compare variants so you could swap oak head for birch head
        MaterialVariant toolVariant = original.getMaterial(index);
        boolean didChange = !toolVariant.sameVariant(partVariant);
        float repairDurability = MaterialRepairModule.getDurability(null, partVariant.getId(), part.getStatType());
        if (!didChange && (original.getDamage() == 0 || repairDurability == 0)) {
          return RecipeResult.pass();
        }

        // actual update
        ToolStack tool = original.copy();

        // determine which modifiers are going to be removed
        if (didChange) {
          // do the actual part replacement
          tool.replaceMaterial(index, partVariant);
        }

        // if swapping in a new head, repair the tool (assuming the give stats type can repair)
        // ideally we would validate before repairing, but don't want to create the stack before repairing
        if (repairDurability > 0) {
          // must have a registered recipe
          int cost = MaterialCastingLookup.getItemCost(part);
          if (cost > 0) {
            // takes 3 ingots for a full repair, however count the head cost in the repair amount
            repairDurability *= cost / MaterialRecipe.INGOTS_PER_REPAIR;
            if (repairDurability > 0) {
              for (ModifierEntry entry : tool.getModifierList()) {
                repairDurability = entry.getHook(ModifierHooks.REPAIR_FACTOR).getRepairFactor(tool, entry, repairDurability);
                if (repairDurability <= 0) {
                  break;
                }
              }
            }
            if (repairDurability > 0) {
              ToolDamageUtil.repair(tool, (int)repairDurability);
            }
          }
        }

        // ensure no modifier problems after removing
        // modifier validation, handles modifier requirements
        Component error = tool.tryValidate();
        if (error != null) {
          return RecipeResult.failure(error);
        }
        if (didChange) {
          error = ModifierRemovalHook.onRemoved(original, tool);
          if (error != null) {
            return RecipeResult.failure(error);
          }
        }
        // everything worked, so good to go
        return RecipeResult.success(tool.createStack(Math.min(inv.getTinkerableSize(), shrinkToolSlotBy())));
      }
    }
    // no item found, should never happen
    return RecipeResult.pass();
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.tinkerStationPartSwappingSerializer.get();
  }
}

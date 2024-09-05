package dev.tocraft.thconstruct.tables.block.entity.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.thconstruct.library.tools.part.IMaterialItem;
import dev.tocraft.thconstruct.tables.TinkerTables;
import dev.tocraft.thconstruct.tables.block.entity.inventory.ScalingChestItemHandler;

/**
 * Chest that holds parts, up to 8 of a given material and type
 */
public class PartChestBlockEntity extends AbstractChestBlockEntity {
  private static final Component NAME = dev.tocraft.thconstruct.ThConstruct.makeTranslation("gui", "part_chest");
  public PartChestBlockEntity(BlockPos pos, BlockState state) {
    super(TinkerTables.partChestTile.get(), pos, state, NAME, new PartChestItemHandler());
  }

  /** Item handler for part chests */
  public static class PartChestItemHandler extends ScalingChestItemHandler {
    @Override
    public int getSlotLimit(int slot) {
      return 8;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
      // check if there is no other slot containing that item
      for (int i = 0; i < this.getSlots(); i++) {
        // don't compare count
        if (ItemStack.isSame(stack, this.getStackInSlot(i)) && ItemStack.tagMatches(stack, this.getStackInSlot(i))) {
          return i == slot; // only allowed in the same slot
        }
      }
      return stack.getItem() instanceof IMaterialItem;
    }
  }
}

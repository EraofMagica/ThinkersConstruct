package dev.tocraft.thconstruct.tables.block.entity.chest;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.tables.TinkerTables;
import dev.tocraft.thconstruct.tables.block.entity.inventory.ScalingChestItemHandler;

/**
 * Chest that holds casts, up to 4 of every type
 */
public class CastChestBlockEntity extends AbstractChestBlockEntity {
  private static final Component NAME = dev.tocraft.thconstruct.ThConstruct.makeTranslation("gui", "cast_chest");
  public CastChestBlockEntity(BlockPos pos, BlockState state) {
    super(TinkerTables.castChestTile.get(), pos, state, NAME, new CastChestIItemHandler());
  }

  /** Item handler for cast chests */
  public static class CastChestIItemHandler extends ScalingChestItemHandler {
    @Override
    public int getSlotLimit(int slot) {
      return 4;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
      for (int i = 0; i < this.getSlots(); i++) {
        if (ItemStack.isSame(stack, this.getStackInSlot(i))) {
          return i == slot;
        }
      }
      return stack.is(TinkerTags.Items.CASTS);
    }
  }
}

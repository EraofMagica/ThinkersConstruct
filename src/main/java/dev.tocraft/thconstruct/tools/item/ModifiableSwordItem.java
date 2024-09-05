package dev.tocraft.thconstruct.tools.item;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.item.ModifiableItem;

public class ModifiableSwordItem extends ModifiableItem {
  public ModifiableSwordItem(Properties properties, ToolDefinition toolDefinition) {
    super(properties, toolDefinition);
  }

  @Override
  public boolean canAttackBlock(BlockState state, Level worldIn, BlockPos pos, Player player) {
    return !player.isCreative();
  }
}

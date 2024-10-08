package dev.tocraft.thconstruct.tools.modifiers.ability.armor.walker;

import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseFireBlock;
import net.minecraft.world.level.block.Block;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

public class FlamewakeModifier extends AbstractWalkerModifier {
  @Override
  protected float getRadius(IToolStackView tool, int level) {
    return 1.5f + tool.getModifierLevel(TinkerModifiers.expanded.getId());
  }

  @Override
  protected void walkOn(IToolStackView tool, int level, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable) {
    // fire starting
    if (BaseFireBlock.canBePlacedAt(world, target, living.getDirection())) {
      world.playSound(null, target, SoundEvents.FLINTANDSTEEL_USE, SoundSource.BLOCKS, 1.0F, RANDOM.nextFloat() * 0.4F + 0.8F);
      world.setBlock(target, BaseFireBlock.getState(world, target), Block.UPDATE_ALL_IMMEDIATE);
      ToolDamageUtil.damageAnimated(tool, 1, living, EquipmentSlot.FEET);
    }
  }
}

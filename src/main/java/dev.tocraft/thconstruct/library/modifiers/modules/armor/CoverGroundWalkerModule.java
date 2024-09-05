package dev.tocraft.thconstruct.library.modifiers.modules.armor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.BlockPos.MutableBlockPos;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.eomantle.data.loadable.common.BlockStateLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.json.LevelingValue;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModuleBuilder;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

/**
 * Module to cover the ground in the given block
 * @param state      State used to cover the ground
 * @param radius     Radius to cover
 * @param condition  Standard module condition
 */
public record CoverGroundWalkerModule(BlockState state, LevelingValue radius, ModifierCondition<IToolStackView> condition) implements ModifierModule, ArmorWalkRadiusModule<Void>, ConditionalModule<IToolStackView> {
  public static final RecordLoadable<CoverGroundWalkerModule> LOADER = RecordLoadable.create(
    BlockStateLoadable.DIFFERENCE.directField(CoverGroundWalkerModule::state),
    LevelingValue.LOADABLE.requiredField("radius", CoverGroundWalkerModule::radius),
    ModifierCondition.TOOL_FIELD,
    CoverGroundWalkerModule::new);

  @Override
  public float getRadius(IToolStackView tool, ModifierEntry modifier) {
    return radius.compute(modifier.getLevel() + tool.getModifierLevel(TinkerModifiers.expanded.getId()));
  }

  @Override
  public void onWalk(IToolStackView tool, ModifierEntry modifier, LivingEntity living, BlockPos prevPos, BlockPos newPos) {
    if (condition.matches(tool, modifier)) {
      ArmorWalkRadiusModule.super.onWalk(tool, modifier, living, prevPos, newPos);
    }
  }

  @Override
  public void walkOn(IToolStackView tool, ModifierEntry entry, LivingEntity living, Level world, BlockPos target, MutableBlockPos mutable, Void context) {
    if (world.isEmptyBlock(target) && state.canSurvive(world, target)) {
      world.setBlockAndUpdate(target, state);
    }
  }

  @Override
  public RecordLoadable<CoverGroundWalkerModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder instance for the given state */
  public static Builder state(BlockState state) {
    return new Builder(state);
  }

  /** Creates a builder instance for the given block */
  public static Builder block(Block block) {
    return state(block.defaultBlockState());
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<Builder> implements LevelingValue.Builder<CoverGroundWalkerModule> {
    private final BlockState state;

    @Override
    public CoverGroundWalkerModule amount(float flat, float eachLevel) {
      return new CoverGroundWalkerModule(state, new LevelingValue(flat, eachLevel), condition);
    }
  }
}

package dev.tocraft.thconstruct.library.json.variable.block;

import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.predicate.block.BlockPredicate;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.json.variable.ConditionalVariable;

/**
 * Gets one of two block properties based on the condition
 */
public record ConditionalBlockVariable(IJsonPredicate<BlockState> condition, BlockVariable ifTrue, BlockVariable ifFalse) implements BlockVariable, ConditionalVariable<IJsonPredicate<BlockState>,BlockVariable> {
  public static final IGenericLoader<ConditionalBlockVariable> LOADER = ConditionalVariable.loadable(BlockPredicate.LOADER, BlockVariable.LOADER, ConditionalBlockVariable::new);

  @Override
  public float getValue(BlockState state) {
    return condition.matches(state) ? ifTrue.getValue(state) : ifFalse.getValue(state);
  }

  @Override
  public IGenericLoader<? extends BlockVariable> getLoader() {
    return LOADER;
  }
}

package dev.tocraft.thconstruct.library.json.variable.tool;

import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.json.predicate.tool.ToolContextPredicate;
import dev.tocraft.thconstruct.library.json.variable.ConditionalVariable;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

/**
 * Gets one of two entity properties based on the condition
 */
public record ConditionalToolVariable(IJsonPredicate<IToolContext> condition, ToolVariable ifTrue, ToolVariable ifFalse) implements ToolVariable, ConditionalVariable<IJsonPredicate<IToolContext>,ToolVariable> {
  public static final IGenericLoader<ConditionalToolVariable> LOADER = ConditionalVariable.loadable(ToolContextPredicate.LOADER, ToolVariable.LOADER, ConditionalToolVariable::new);

  @Override
  public float getValue(IToolStackView tool) {
    return condition.matches(tool) ? ifTrue.getValue(tool) : ifFalse.getValue(tool);
  }

  @Override
  public IGenericLoader<? extends ToolVariable> getLoader() {
    return LOADER;
  }
}

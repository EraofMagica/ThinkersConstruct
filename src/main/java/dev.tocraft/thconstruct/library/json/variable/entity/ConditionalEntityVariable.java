package dev.tocraft.thconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.predicate.entity.LivingEntityPredicate;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.json.variable.ConditionalVariable;

/**
 * Gets one of two entity properties based on the condition
 */
public record ConditionalEntityVariable(IJsonPredicate<LivingEntity> condition, EntityVariable ifTrue, EntityVariable ifFalse)
    implements EntityVariable, ConditionalVariable<IJsonPredicate<LivingEntity>,EntityVariable> {
  public static final IGenericLoader<ConditionalEntityVariable> LOADER = ConditionalVariable.loadable(LivingEntityPredicate.LOADER, EntityVariable.LOADER, ConditionalEntityVariable::new);

  public ConditionalEntityVariable(IJsonPredicate<LivingEntity> condition, float ifTrue, float ifFalse) {
    this(condition, new EntityVariable.Constant(ifTrue), new EntityVariable.Constant(ifFalse));
  }

  @Override
  public float getValue(LivingEntity entity) {
    return condition.matches(entity) ? ifTrue.getValue(entity) : ifFalse.getValue(entity);
  }

  @Override
  public IGenericLoader<? extends EntityVariable> getLoader() {
    return LOADER;
  }

}

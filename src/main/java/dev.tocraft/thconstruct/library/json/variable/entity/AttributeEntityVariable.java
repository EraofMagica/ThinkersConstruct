package dev.tocraft.thconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;

/** Variable that fetches an attribute value */
public record AttributeEntityVariable(Attribute attribute) implements EntityVariable {
  public static final RecordLoadable<AttributeEntityVariable> LOADER = RecordLoadable.create(Loadables.ATTRIBUTE.requiredField("attribute", AttributeEntityVariable::attribute), AttributeEntityVariable::new);

  @Override
  public float getValue(LivingEntity entity) {
    return (float)entity.getAttributeValue(attribute);
  }

  @Override
  public IGenericLoader<? extends EntityVariable> getLoader() {
    return LOADER;
  }
}

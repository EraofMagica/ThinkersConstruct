package dev.tocraft.thconstruct.library.json.variable.stat;

import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.eomantle.data.loadable.primitive.FloatLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.json.variable.entity.EntityVariable;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Gets a variable from the entity in the variable context
 * @param entity    Entity variable getter
 * @param fallback  Fallback if entity is null (happens when the tooltip is called serverside mainly)
 */
public record EntityConditionalStatVariable(EntityVariable entity, float fallback) implements ConditionalStatVariable {
  public static final RecordLoadable<EntityConditionalStatVariable> LOADER = RecordLoadable.create(
    EntityVariable.LOADER.directField("entity_type", EntityConditionalStatVariable::entity),
    FloatLoadable.ANY.requiredField("fallback", EntityConditionalStatVariable::fallback),
    EntityConditionalStatVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable LivingEntity entity) {
    if (entity != null) {
      return this.entity.getValue(entity);
    }
    return fallback;
  }

  @Override
  public IGenericLoader<? extends EntityConditionalStatVariable> getLoader() {
    return LOADER;
  }
}

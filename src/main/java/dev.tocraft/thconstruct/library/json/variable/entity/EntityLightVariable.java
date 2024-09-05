package dev.tocraft.thconstruct.library.json.variable.entity;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.LightLayer;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.json.TinkerLoadables;

/** Gets the light level at the entity position */
public record EntityLightVariable(LightLayer lightLayer) implements EntityVariable {
  public static final RecordLoadable<EntityLightVariable> LOADER = RecordLoadable.create(TinkerLoadables.LIGHT_LAYER.requiredField("light_layer", EntityLightVariable::lightLayer), EntityLightVariable::new);

  @Override
  public float getValue(LivingEntity entity) {
    return entity.level.getBrightness(lightLayer, entity.blockPosition());
  }

  @Override
  public IGenericLoader<? extends EntityVariable> getLoader() {
    return LOADER;
  }
}

package dev.tocraft.thconstruct.library.json.variable.entity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;

/** Gets the level of the mob effect on an entity */
public record EntityEffectLevelVariable(MobEffect effect) implements EntityVariable {
  public static final RecordLoadable<EntityEffectLevelVariable> LOADER = RecordLoadable.create(Loadables.MOB_EFFECT.requiredField("effect", EntityEffectLevelVariable::effect), EntityEffectLevelVariable::new);

  @Override
  public float getValue(LivingEntity entity) {
    MobEffectInstance instance = entity.getEffect(effect);
    if (instance != null) {
      return instance.getAmplifier() + 1;
    }
    return 0;
  }

  @Override
  public IGenericLoader<? extends EntityVariable> getLoader() {
    return LOADER;
  }
}

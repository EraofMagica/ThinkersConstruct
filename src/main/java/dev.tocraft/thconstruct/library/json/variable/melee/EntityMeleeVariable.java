package dev.tocraft.thconstruct.library.json.variable.melee;

import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.eomantle.data.loadable.primitive.EnumLoadable;
import dev.tocraft.eomantle.data.loadable.primitive.FloatLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.json.variable.entity.EntityVariable;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/**
 * Reads an entity variable from melee content
 * @param entity   Entity variable
 * @param which    Determines whether to read the attacker or the target
 * @param fallback Fallback if the entity is not found
 */
public record EntityMeleeVariable(EntityVariable entity, WhichEntity which, float fallback) implements MeleeVariable {
  public static final RecordLoadable<EntityMeleeVariable> LOADER = RecordLoadable.create(
    EntityVariable.LOADER.directField("entity_type", EntityMeleeVariable::entity),
    new EnumLoadable<>(WhichEntity.class).requiredField("which", EntityMeleeVariable::which),
    FloatLoadable.ANY.requiredField("fallback", EntityMeleeVariable::fallback),
    EntityMeleeVariable::new);

  @Override
  public float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker) {
    LivingEntity entity = null;
    if (which == WhichEntity.ATTACKER) {
      entity = attacker;
    } else if (context != null) {
      entity = context.getLivingTarget();
    }
    if (entity != null) {
      return this.entity.getValue(entity);
    }
    return fallback;
  }

  @Override
  public IGenericLoader<? extends MeleeVariable> getLoader() {
    return LOADER;
  }

  public enum WhichEntity { ATTACKER, TARGET }
}

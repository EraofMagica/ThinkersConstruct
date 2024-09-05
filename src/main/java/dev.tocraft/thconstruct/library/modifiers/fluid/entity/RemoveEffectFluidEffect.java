package dev.tocraft.thconstruct.library.modifiers.fluid.entity;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.modifiers.fluid.EffectLevel;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffectContext;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffectContext.Entity;

/** Spilling effect to remove a specific effect */
public record RemoveEffectFluidEffect(MobEffect effect) implements FluidEffect<FluidEffectContext.Entity> {
  public static final RecordLoadable<RemoveEffectFluidEffect> LOADER = RecordLoadable.create(Loadables.MOB_EFFECT.requiredField("effect", e -> e.effect), RemoveEffectFluidEffect::new);

  @Override
  public RecordLoadable<RemoveEffectFluidEffect> getLoader() {
    return LOADER;
  }

  @Override
  public float apply(FluidStack fluid, EffectLevel level, Entity context, FluidAction action) {
    LivingEntity living = context.getLivingTarget();
    if (living != null && level.isFull()) {
      if (action.simulate()) {
        return living.hasEffect(effect) ? 1 : 0;
      }
      return living.removeEffect(effect) ? 1 : 0;
    }
    return 0;
  }
}

package dev.tocraft.thconstruct.tools.modifiers.effect;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.effect.MobEffectCategory;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.general.MagneticModifier;

public class MagneticEffect extends NoMilkEffect {
  public MagneticEffect() {
    super(MobEffectCategory.BENEFICIAL, 0x720000, false);
  }

  @Override
  public boolean isDurationEffectTick(int duration, int amplifier) {
    return (duration & 1) == 0;
  }

  @Override
  public void applyEffectTick(LivingEntity entity, int amplifier) {
    MagneticModifier.applyMagnet(entity, amplifier);
  }
}

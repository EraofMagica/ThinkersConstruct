package dev.tocraft.thconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import dev.tocraft.thconstruct.library.modifiers.impl.NoLevelsModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

public class FrosttouchModifier extends NoLevelsModifier implements DamageDealtModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.DAMAGE_DEALT);
    hookBuilder.addModule(StrongBonesModifier.CALCIFIABLE_MODULE);
  }

  @Override
  public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    // must drink milk to melee slowness. Always can range slowness
    if (isDirectDamage) {
      boolean isCalcified = context.getEntity().hasEffect(TinkerModifiers.calcifiedEffect.get());
      if (isCalcified || source.isProjectile()) {
        target.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300, isCalcified ? 1 : 0));
      }
    }
  }
}

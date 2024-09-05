package dev.tocraft.thconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.DamageDealtModifierHook;
import dev.tocraft.thconstruct.library.modifiers.impl.NoLevelsModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

public class BreathtakingModifier extends NoLevelsModifier implements DamageDealtModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.DAMAGE_DEALT);
  }

  @Override
  public void onDamageDealt(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, LivingEntity target, DamageSource source, float amount, boolean isDirectDamage) {
    if (isDirectDamage && !source.isProjectile()) {
      LivingEntity attacker = context.getEntity();
      int attackerAir = attacker.getAirSupply();
      int maxAir = attacker.getMaxAirSupply();
      if (attackerAir < maxAir) {
        attacker.setAirSupply(Math.min(attackerAir + 60, maxAir));
      }
      target.setAirSupply(Math.max(-20, target.getAirSupply() - 60));
    }
  }
}

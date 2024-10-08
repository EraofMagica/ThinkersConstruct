package dev.tocraft.thconstruct.tools.modifiers.ability.fluid;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffectContext;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.ModifyDamageModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Modifier to handle spilling recipes onto self when attacked */
public class WettingModifier extends UseFluidOnHitModifier implements ModifyDamageModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.MODIFY_DAMAGE);
  }

  @Override
  public FluidEffectContext.Entity createContext(LivingEntity self, @Nullable Player player, @Nullable Entity attacker) {
    return new FluidEffectContext.Entity(self.level, self, player, null, self, self);
  }

  @Override
  public float modifyDamageTaken(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    if (!source.isBypassMagic() && !source.isBypassInvul()) {
      useFluid(tool, modifier, context, slotType, source);
    }
    return amount;
  }
}

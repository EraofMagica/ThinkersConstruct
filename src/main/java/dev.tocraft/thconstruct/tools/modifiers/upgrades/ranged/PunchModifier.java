package dev.tocraft.thconstruct.tools.modifiers.upgrades.ranged;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;
import dev.tocraft.thconstruct.tools.entity.FluidEffectProjectile;

import javax.annotation.Nullable;

public class PunchModifier extends Modifier implements ProjectileLaunchModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH);
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    if (arrow != null) {
      arrow.setKnockback(modifier.getLevel());
    } else if (projectile instanceof FluidEffectProjectile spit) {
      spit.setKnockback(spit.getKnockback() + modifier.getLevel());
    }
  }
}

package dev.tocraft.thconstruct.tools.modifiers.traits.ranged;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobType;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;

public class HolyModifier extends Modifier implements ProjectileHitModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT);
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null && target.getMobType() == MobType.UNDEAD && projectile instanceof AbstractArrow arrow) {
      arrow.setBaseDamage(arrow.getBaseDamage() + modifier.getLevel() / 2f);
    }
    return false;
  }
}

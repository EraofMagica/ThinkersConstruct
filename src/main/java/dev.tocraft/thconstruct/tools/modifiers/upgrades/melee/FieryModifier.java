package dev.tocraft.thconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;

public class FieryModifier extends Modifier implements ProjectileLaunchModifierHook, ProjectileHitModifierHook, OnAttackedModifierHook, MeleeHitModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT, ModifierHooks.ON_ATTACKED, ModifierHooks.MELEE_HIT);
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // vanilla hack: apply fire so the entity drops the proper items on instant kill
    LivingEntity target = context.getLivingTarget();
    if (target != null && !target.isOnFire()) {
      target.setRemainingFireTicks(1);
    }
    return knockback;
  }

  @Override
  public void failedMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageAttempted) {
    // conclusion of vanilla hack: we don't want the target on fire if we did not hit them
    LivingEntity target = context.getLivingTarget();
    if (target != null && target.isOnFire()) {
      target.clearFire();
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    LivingEntity target = context.getLivingTarget();
    if (target != null) {
      target.setSecondsOnFire(Math.round(modifier.getEffectiveLevel() * 5));
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    projectile.setSecondsOnFire(Math.round(modifier.getEffectiveLevel() * 20));
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    hit.getEntity().setSecondsOnFire(Math.round(modifier.getEffectiveLevel() * 5));
    return false;
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker != null) {
      // 15% chance of working per level, doubled bonus on shields
      int level = modifier.getLevel();
      if (slotType.getType() == Type.HAND) {
        level *= 2;
      }
      if (RANDOM.nextFloat() < (level * 0.15f)) {
        attacker.setSecondsOnFire(Math.round(modifier.getEffectiveLevel() * 5));
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }
}

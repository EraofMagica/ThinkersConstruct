package dev.tocraft.thconstruct.tools.modifiers.traits.melee;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;

public class DecayModifier extends Modifier implements ProjectileLaunchModifierHook, ProjectileHitModifierHook, MeleeHitModifierHook {
  /* gets the effect for the given level, including a random time */
  private static MobEffectInstance makeDecayEffect(int level) {
    // potions are 0 indexed instead of 1 indexed
    // wither skeletons apply 10 seconds of wither for comparison
    return new MobEffectInstance(MobEffects.WITHER, 20 * (5 + (RANDOM.nextInt(level * 3))), level - 1);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT);
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (context.isFullyCharged()) {
      // note the time of each effect is calculated independently

      // 25% chance to poison yourself
      if (RANDOM.nextInt(3) == 0) {
        context.getAttacker().addEffect(makeDecayEffect(modifier.getLevel()));
      }

      // always poison the target, means it works twice as often as lacerating
      LivingEntity target = context.getLivingTarget();
      if (target != null && target.isAlive()) {
        target.addEffect(makeDecayEffect(modifier.getLevel()));
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null && (!(projectile instanceof AbstractArrow arrow) || arrow.isCritArrow())) {
      // always poison the target, means it works twice as often as lacerating
      target.addEffect(makeDecayEffect(modifier.getLevel()));
    }
    return false;
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    if (primary && (arrow == null || arrow.isCritArrow()) && RANDOM.nextInt(3) == 0) {
      // 25% chance to poison yourself
      shooter.addEffect(makeDecayEffect(modifier.getLevel()));
    }
  }
}

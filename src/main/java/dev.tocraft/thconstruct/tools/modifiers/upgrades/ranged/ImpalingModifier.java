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

import javax.annotation.Nullable;

public class ImpalingModifier extends Modifier implements ProjectileLaunchModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH);
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    if (arrow != null) {
      arrow.setPierceLevel((byte)modifier.getLevel());
    }
  }
}

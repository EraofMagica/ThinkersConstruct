package dev.tocraft.thconstruct.tools.modules.armor;

import lombok.Getter;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.EquipmentSlot.Type;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.SingletonLoader;
import dev.tocraft.thconstruct.library.events.teleport.EnderclearanceTeleportEvent;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;
import dev.tocraft.thconstruct.library.utils.TeleportHelper;

import javax.annotation.Nullable;
import java.util.List;

/** Module making the target teleport */
public enum EnderclearanceModule implements ModifierModule, ProjectileHitModifierHook, MeleeHitModifierHook, OnAttackedModifierHook {
  INSTANCE;

  @Getter
  private final List<ModuleHook<?>> defaultHooks = HookProvider.<EnderclearanceModule>defaultHooks(ModifierHooks.PROJECTILE_HIT, ModifierHooks.MELEE_HIT, ModifierHooks.ON_ATTACKED);
  @Getter
  private final SingletonLoader<EnderclearanceModule> loader = new SingletonLoader<>(this);


  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    LivingEntity entity = context.getLivingTarget();
    if (entity != null) {
      TeleportHelper.randomNearbyTeleport(entity, EnderclearanceTeleportEvent.TELEPORT_FACTORY);
    }
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // this works like vanilla, damage is capped due to the hurt immunity mechanics, so if multiple pieces apply thorns between us and vanilla, damage is capped at 4
    if (isDirectDamage && source.getEntity() instanceof LivingEntity attacker) {
      // 15% chance of working per level, doubled bonus on shields
      int level = modifier.getLevel();
      if (slotType.getType() == Type.HAND) {
        level *= 2;
      }
      if (dev.tocraft.thconstruct.ThConstruct.RANDOM.nextFloat() < (level * 0.25f)) {
        TeleportHelper.randomNearbyTeleport(attacker, EnderclearanceTeleportEvent.TELEPORT_FACTORY);
      }
    }
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (target != null) {
      TeleportHelper.randomNearbyTeleport(target, EnderclearanceTeleportEvent.TELEPORT_FACTORY);
    }
    return false;
  }
}

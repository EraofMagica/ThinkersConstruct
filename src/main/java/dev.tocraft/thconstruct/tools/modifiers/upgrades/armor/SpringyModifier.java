package dev.tocraft.thconstruct.tools.modifiers.upgrades.armor;

import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.technical.SlotInChargeModule;
import dev.tocraft.thconstruct.library.modifiers.modules.technical.SlotInChargeModule.SlotInCharge;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.definition.ModifiableArmorMaterial;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

public class SpringyModifier extends Modifier implements MeleeHitModifierHook, OnAttackedModifierHook {
  private static final TinkerDataKey<SlotInCharge> SLOT_IN_CHARGE = dev.tocraft.thconstruct.ThConstruct.createKey("springy");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.MELEE_HIT, ModifierHooks.ON_ATTACKED);
    hookBuilder.addModule(new SlotInChargeModule(SLOT_IN_CHARGE));
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    LivingEntity user = context.getEntity();
    Entity attacker = source.getEntity();
    if (isDirectDamage && !user.level.isClientSide && attacker instanceof LivingEntity livingAttacker && SlotInChargeModule.isInCharge(context.getTinkerData(), SLOT_IN_CHARGE, slotType)) {
      // each slot attempts to apply, we keep the largest one, consistent with other counter attack modifiers
      float bestBonus = 0;
      for (EquipmentSlot bouncingSlot : ModifiableArmorMaterial.ARMOR_SLOTS) {
        IToolStackView bouncingTool = context.getToolInSlot(bouncingSlot);
        if (bouncingTool != null && !bouncingTool.isBroken()) {
          // 15% chance per level of it applying
          int level = modifier.getLevel();
          if (RANDOM.nextFloat() < (level * 0.25f)) {
            // does 0.5 base, plus up to 0.5f per level -- for comparison, 0.4 is normal knockback, 0.9 is with knockback 1
            float newBonus = 0.5f * RANDOM.nextFloat() * level;
            if (newBonus > bestBonus) {
              bestBonus = newBonus;
            }
          }
        }
      }
      // did we end up with any bonus?
      if (bestBonus > 0) {
        float angle = attacker.getYRot() * (float)Math.PI / 180F;
        livingAttacker.knockback(bestBonus, -Mth.sin(angle), Mth.cos(angle));
      }
    }
  }

  @Override
  public float beforeMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damage, float baseKnockback, float knockback) {
    // unarmed bonus
    return knockback + modifier.getLevel() * 0.5f;
  }

}

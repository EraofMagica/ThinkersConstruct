package dev.tocraft.thconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.IndirectEntityDamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.thconstruct.library.events.teleport.EnderdodgingTeleportEvent;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import dev.tocraft.thconstruct.library.modifiers.impl.NoLevelsModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.utils.TeleportHelper;
import dev.tocraft.thconstruct.library.utils.TeleportHelper.ITeleportEventFactory;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

public class EnderdodgingModifier extends NoLevelsModifier implements DamageBlockModifierHook, OnAttackedModifierHook {
  private static final ITeleportEventFactory FACTORY = EnderdodgingTeleportEvent::new;

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.DAMAGE_BLOCK, ModifierHooks.ON_ATTACKED);
  }

  @Override
  public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    // teleport always from projectiles
    LivingEntity self = context.getEntity();
    if (!self.hasEffect(TinkerModifiers.teleportCooldownEffect.get()) && source instanceof IndirectEntityDamageSource) {
      if (TeleportHelper.randomNearbyTeleport(context.getEntity(), FACTORY)) {
        TinkerModifiers.teleportCooldownEffect.get().apply(self, 15 * 20, 0, true);
        ToolDamageUtil.damageAnimated(tool, (int)amount, self, slotType);
        return true;
      }
      return false;
    }
    return false;
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    // teleport randomly from other damage
    LivingEntity self = context.getEntity();
    if (!self.hasEffect(TinkerModifiers.teleportCooldownEffect.get()) && source.getEntity() instanceof LivingEntity && RANDOM.nextInt(10) == 0) {
      if (TeleportHelper.randomNearbyTeleport(context.getEntity(), FACTORY)) {
        TinkerModifiers.teleportCooldownEffect.get().apply(self, 15 * 20, 1, true);
      }
    }
  }
}

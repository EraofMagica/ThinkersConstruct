package dev.tocraft.thconstruct.tools.modifiers.ability.armor;

import net.minecraft.world.entity.EquipmentSlot;
import dev.tocraft.eomantle.util.OffhandCooldownTracker;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.OffhandAttackModifier;

public class AmbidextrousModifier extends OffhandAttackModifier implements EquipmentChangeModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
    hookBuilder.addModule(ShowOffhandModule.DISALLOW_BROKEN);
  }

  @Override
  public boolean shouldDisplay(boolean advanced) {
    return true;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlot.CHEST) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(true));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && context.getChangedSlot() == EquipmentSlot.CHEST) {
      context.getEntity().getCapability(OffhandCooldownTracker.CAPABILITY).ifPresent(cap -> cap.setEnabled(false));
    }
  }
}

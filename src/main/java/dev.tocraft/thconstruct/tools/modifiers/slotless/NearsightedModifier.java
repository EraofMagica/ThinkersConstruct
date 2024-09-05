package dev.tocraft.thconstruct.tools.modifiers.slotless;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataKeys;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.Arrays;
import java.util.Comparator;

public class NearsightedModifier extends Modifier implements EquipmentChangeModifierHook {
  private final ResourceLocation[] SLOT_KEYS = Arrays.stream(EquipmentSlot.values())
                                                     .sorted(Comparator.comparing(EquipmentSlot::getFilterFlag))
                                                     .map(slot -> dev.tocraft.thconstruct.ThConstruct.getResource("nearsighted_" + slot.getName()))
                                                     .toArray(ResourceLocation[]::new);

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      ResourceLocation key = SLOT_KEYS[context.getChangedSlot().getFilterFlag()];
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(key, 1 + 0.05f * modifier.getLevel()));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken()) {
      ResourceLocation key = SLOT_KEYS[context.getChangedSlot().getFilterFlag()];
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(key));
    }
  }
}

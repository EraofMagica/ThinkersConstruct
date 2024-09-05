package dev.tocraft.thconstruct.tools.modifiers.defense;

import lombok.RequiredArgsConstructor;
import net.minecraft.world.entity.EquipmentSlot;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.data.ModifierMaxLevel;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.helper.ModifierUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

/** Base class for protection modifiers that want to keep track of the largest level for a bonus */
@RequiredArgsConstructor
public abstract class AbstractProtectionModifier<T extends ModifierMaxLevel> extends Modifier implements EquipmentChangeModifierHook {
  private final ComputableDataKey<T> key;
  private final boolean allowClient;

  public AbstractProtectionModifier(ComputableDataKey<T> key) {
    this(key, false);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
  }

  /** Called when the last piece of equipment is removed to reset the data */
  protected void reset(T data, EquipmentChangeContext context) {}

  /** Called to apply updates to the piece */
  protected void set(T data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    data.set(slot, scaledLevel);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    EquipmentSlot slot = context.getChangedSlot();
    if ((allowClient || !context.getEntity().level.isClientSide) && ModifierUtil.validArmorSlot(tool, slot)) {
      context.getTinkerData().ifPresent(data -> {
        T modData = data.get(key);
        if (modData != null) {
          set(modData, slot, 0, context);
          if (modData.getMax() == 0) {
            reset(modData, context);
          }
        }
      });
    }
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    EquipmentSlot slot = context.getChangedSlot();
    if ((allowClient || !context.getEntity().level.isClientSide) && ModifierUtil.validArmorSlot(tool, slot) && !tool.isBroken()) {
      float scaledLevel = modifier.getEffectiveLevel();
      context.getTinkerData().ifPresent(data -> {
        // add ourself to the data
        set(data.computeIfAbsent(key), slot, scaledLevel, context);
      });
    }
  }
}

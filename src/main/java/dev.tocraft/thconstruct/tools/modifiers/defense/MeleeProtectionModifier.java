package dev.tocraft.thconstruct.tools.modifiers.defense;

import net.minecraft.world.entity.EquipmentSlot;
import dev.tocraft.eomantle.data.predicate.damage.DamageSourcePredicate;
import dev.tocraft.thconstruct.library.modifiers.data.ModifierMaxLevel;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ProtectionModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataKeys;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;

public class MeleeProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  private static final ComputableDataKey<ModifierMaxLevel> KEY = dev.tocraft.thconstruct.ThConstruct.createKey("melee_protection", ModifierMaxLevel::new);

  public MeleeProtectionModifier() {
    super(KEY);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.MELEE).eachLevel(2.5f));
  }

  @Override
  protected void set(ModifierMaxLevel data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    float oldMax = data.getMax();
    super.set(data, slot, scaledLevel, context);
    float newMax = data.getMax();
    if (oldMax != newMax) {
      context.getTinkerData().ifPresent(d -> d.add(TinkerDataKeys.USE_ITEM_SPEED, (newMax - oldMax) * 0.05f));
    }
  }
}

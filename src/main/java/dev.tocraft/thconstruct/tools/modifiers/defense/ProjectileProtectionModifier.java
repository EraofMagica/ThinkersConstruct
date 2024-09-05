package dev.tocraft.thconstruct.tools.modifiers.defense;

import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import dev.tocraft.eomantle.data.predicate.damage.DamageSourcePredicate;
import dev.tocraft.thconstruct.library.modifiers.data.ModifierMaxLevel;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ProtectionModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;

import java.util.UUID;

public class ProjectileProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  private static final UUID ATTRIBUTE_UUID = UUID.fromString("6f030b1e-e9e1-11ec-8fea-0242ac120002");
  /** Entity data key for the data associated with this modifier */
  private static final ComputableDataKey<ModifierMaxLevel> PROJECTILE_DATA = dev.tocraft.thconstruct.ThConstruct.createKey("projectile_protection", ModifierMaxLevel::new);
  public ProjectileProtectionModifier() {
    super(PROJECTILE_DATA, true);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.PROJECTILE).eachLevel(2.5f));
  }

  @Override
  protected void set(ModifierMaxLevel data, EquipmentSlot slot, float scaledLevel, EquipmentChangeContext context) {
    float oldMax = data.getMax();
    super.set(data, slot, scaledLevel, context);
    float newMax = data.getMax();
    // 5% bonus attack speed for the largest level
    if (oldMax != newMax) {
      AttributeInstance instance = context.getEntity().getAttribute(Attributes.KNOCKBACK_RESISTANCE);
      if (instance != null) {
        instance.removeModifier(ATTRIBUTE_UUID);
        if (newMax != 0) {
          instance.addTransientModifier(new AttributeModifier(ATTRIBUTE_UUID, "thconstruct.melee_protection", 0.05 * newMax, Operation.ADDITION));
        }
      }
    }
  }
}

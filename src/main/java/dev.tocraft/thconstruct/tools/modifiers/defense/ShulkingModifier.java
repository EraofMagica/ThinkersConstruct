package dev.tocraft.thconstruct.tools.modifiers.defense;

import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import dev.tocraft.eomantle.data.predicate.damage.DamageSourcePredicate;
import dev.tocraft.eomantle.data.predicate.entity.LivingEntityPredicate;
import dev.tocraft.thconstruct.library.modifiers.data.ModifierMaxLevel;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ProtectionModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;

public class ShulkingModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  private static final ComputableDataKey<ModifierMaxLevel> KEY = dev.tocraft.thconstruct.ThConstruct.createKey("shulking", ModifierMaxLevel::new);
  public ShulkingModifier() {
    super(KEY);
    // TODO: move to data key registry and ModifierEvent
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, LivingHurtEvent.class, ShulkingModifier::onAttack);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT).entity(LivingEntityPredicate.CROUCHING).eachLevel(2.5f));
  }

  private static void onAttack(LivingHurtEvent event) {
    // if the attacker is crouching, deal less damage
    Entity attacker = event.getSource().getEntity();
    if (attacker != null && attacker.isCrouching()) {
      attacker.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        ModifierMaxLevel max = data.get(KEY);
        if (max != null) {
          event.setAmount(event.getAmount() * (1 - (max.getMax() * 0.1f)));
        }
      });
    }
  }
}

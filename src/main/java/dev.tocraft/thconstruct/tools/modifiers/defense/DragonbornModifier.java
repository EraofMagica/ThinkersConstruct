package dev.tocraft.thconstruct.tools.modifiers.defense;

import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.Event.Result;
import net.minecraftforge.eventbus.api.EventPriority;
import dev.tocraft.thconstruct.library.json.predicate.TinkerPredicate;
import dev.tocraft.thconstruct.library.modifiers.data.ModifierMaxLevel;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ProtectionModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;

public class DragonbornModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  private static final ComputableDataKey<ModifierMaxLevel> DRAGONBORN = dev.tocraft.thconstruct.ThConstruct.createKey("dragonborn", ModifierMaxLevel::new);
  public DragonbornModifier() {
    super(DRAGONBORN);
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, CriticalHitEvent.class, DragonbornModifier::onCritical);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ProtectionModule.builder().entity(TinkerPredicate.AIRBORNE).eachLevel(2.5f));
  }

  /** Boosts critical hit damage */
  private static void onCritical(CriticalHitEvent event) {
    if (event.getResult() != Result.DENY) {
      // force critical if not already critical and in the air
      LivingEntity living = event.getEntity();

      // check dragonborn first, faster check
      living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        ModifierMaxLevel dragonborn = data.get(DRAGONBORN);
        if (dragonborn != null) {
          float max = dragonborn.getMax();
          if (max > 0) {
            // make it critical if we meet our simpler conditions, note this does not boost attack damage
            boolean isCritical = event.isVanillaCritical() || event.getResult() == Result.ALLOW;
            if (!isCritical && TinkerPredicate.AIRBORNE.matches(living)) {
              isCritical = true;
              event.setResult(Result.ALLOW);
            }

            // if we either were or became critical, time to boost
            if (isCritical) {
              // adds +5% critical hit per level
              event.setDamageModifier(event.getDamageModifier() + max * 0.05f);
            }
          }
        }
      });
    }
  }
}

package dev.tocraft.thconstruct.tools.modifiers.defense;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import dev.tocraft.eomantle.data.predicate.damage.DamageSourcePredicate;
import dev.tocraft.thconstruct.library.modifiers.data.ModifierMaxLevel;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ProtectionModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;

public class MagicProtectionModifier extends AbstractProtectionModifier<ModifierMaxLevel> {
  /** Entity data key for the data associated with this modifier */
  private static final ComputableDataKey<ModifierMaxLevel> MAGIC_DATA = dev.tocraft.thconstruct.ThConstruct.createKey("magic_protection", ModifierMaxLevel::new);
  public MagicProtectionModifier() {
    super(MAGIC_DATA);
    // TODO: extract to data key module using ModifierEvents
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, MobEffectEvent.Added.class, MagicProtectionModifier::onPotionStart);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ProtectionModule.builder().sources(DamageSourcePredicate.CAN_PROTECT, DamageSourcePredicate.MAGIC).eachLevel(2.5f));
  }

  private static void onPotionStart(MobEffectEvent.Added event) {
    MobEffectInstance newEffect = event.getEffectInstance();
    if (!newEffect.getEffect().isBeneficial() && !newEffect.getCurativeItems().isEmpty()) {
      LivingEntity living = event.getEntity();
      living.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> {
        ModifierMaxLevel magicData = data.get(MAGIC_DATA);
        if (magicData != null) {
          float max = magicData.getMax();
          if (max > 0) {
            // decrease duration by 5% per level
            int duration = (int)(newEffect.getDuration() * (1 - (max * 0.05f)));
            if (duration < 0) {
              duration = 0;
            }
            newEffect.duration = duration;
          }
        }
      });
    }
  }
}

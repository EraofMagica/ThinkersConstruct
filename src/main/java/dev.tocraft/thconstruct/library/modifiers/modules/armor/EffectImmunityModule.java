package dev.tocraft.thconstruct.library.modifiers.modules.armor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.world.effect.MobEffect;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module for armor modifiers that makes the wearer immune to a mob effect
 */
public record EffectImmunityModule(MobEffect effect, ModifierCondition<IToolStackView> condition) implements ModifierModule, EquipmentChangeModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MobDisguiseModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final ComputableDataKey<Multiset<MobEffect>> EFFECT_IMMUNITY = dev.tocraft.thconstruct.ThConstruct.createKey("effect_immunity", HashMultiset::create);
  public static final RecordLoadable<EffectImmunityModule> LOADER = RecordLoadable.create(
    Loadables.MOB_EFFECT.requiredField("effect", EffectImmunityModule::effect),
    ModifierCondition.TOOL_FIELD,
    EffectImmunityModule::new);

  public EffectImmunityModule(MobEffect effect) {
    this(effect, ModifierCondition.ANY_TOOL);
  }

  @Override
  public RecordLoadable<EffectImmunityModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && ArmorLevelModule.validSlot(tool, context.getChangedSlot(), TinkerTags.Items.HELD_ARMOR) && condition.matches(tool, modifier)) {
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(EFFECT_IMMUNITY).add(effect));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (!tool.isBroken() && ArmorLevelModule.validSlot(tool, context.getChangedSlot(), TinkerTags.Items.HELD_ARMOR) && condition.matches(tool, modifier)) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<MobEffect> effects = data.get(EFFECT_IMMUNITY);
        if (effects != null) {
          effects.remove(effect);
        }
      });
    }
  }
}

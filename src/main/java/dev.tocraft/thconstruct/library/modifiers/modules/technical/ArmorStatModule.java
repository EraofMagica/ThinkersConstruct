package dev.tocraft.thconstruct.library.modifiers.modules.technical;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.TooltipFlag;
import dev.tocraft.eomantle.client.TooltipKey;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.primitive.BooleanLoadable;
import dev.tocraft.eomantle.data.loadable.primitive.EnumLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.json.LevelingValue;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.display.TooltipModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModuleBuilder;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataKeys;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.utils.Util;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Modifier that to keep track of a stat that is contributed to by all armor pieces. Can scale the stat on different modifiers or for incremental and can use float values unlike {@link ArmorLevelModule}.
 * @see ArmorLevelModule
 * @see TinkerDataKey
 */
public record ArmorStatModule(TinkerDataKey<Float> key, LevelingValue amount, boolean allowBroken, @Nullable TagKey<Item> heldTag, TooltipStyle tooltipStyle, ModifierCondition<IToolStackView> condition) implements HookProvider, EquipmentChangeModifierHook, ModifierModule, TooltipModifierHook, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> TOOLTIP_HOOKS = HookProvider.<ArmorStatModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE, ModifierHooks.TOOLTIP);
  private static final List<ModuleHook<?>> NO_TOOLTIP_HOOKS = HookProvider.<ArmorStatModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<ArmorStatModule> LOADER = RecordLoadable.create(
    TinkerDataKeys.FLOAT_REGISTRY.requiredField("key", ArmorStatModule::key),
    LevelingValue.LOADABLE.directField(ArmorStatModule::amount),
    BooleanLoadable.INSTANCE.defaultField("allow_broken", false, ArmorStatModule::allowBroken),
    Loadables.ITEM_TAG.nullableField("held_tag", ArmorStatModule::heldTag),
    new EnumLoadable<>(TooltipStyle.class).defaultField("tooltip_style", TooltipStyle.NONE, ArmorStatModule::tooltipStyle),
    ModifierCondition.TOOL_FIELD,
    ArmorStatModule::new);

  @Override
  public RecordLoadable<ArmorStatModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return tooltipStyle == TooltipStyle.NONE ? NO_TOOLTIP_HOOKS : TOOLTIP_HOOKS;
  }

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      ArmorStatModule.addStatIfArmor(tool, context, key, amount.compute(modifier.getEffectiveLevel()), allowBroken, heldTag);
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (condition.matches(tool, modifier)) {
      ArmorStatModule.addStatIfArmor(tool, context, key, -amount.compute(modifier.getEffectiveLevel()), allowBroken, heldTag);
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    if (condition.matches(tool, modifier) && (tool.hasTag(TinkerTags.Items.WORN_ARMOR) || heldTag != null && tool.hasTag(heldTag)) && (!tool.isBroken() || allowBroken)) {
      float value = amount.compute(modifier.getEffectiveLevel());
      if (value != 0) {
        Component name = Component.translatable(Util.makeTranslationKey("armor_stat", key.getId()));
        switch (tooltipStyle) {
          case BOOST -> TooltipModifierHook.addFlatBoost(modifier.getModifier(), name, value, tooltip);
          case PERCENT -> TooltipModifierHook.addPercentBoost(modifier.getModifier(), name, value, tooltip);
        }
      }
    }
  }


  /* Helpers */

  public enum TooltipStyle { NONE, BOOST, PERCENT }

  /**
   * Adds to the armor stat for the given key. Make sure to subtract on unequip if you add on equip, it will not automatically be removed.
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   */
  public static void addStat(EquipmentChangeContext context, TinkerDataKey<Float> key, float amount) {
    context.getTinkerData().ifPresent(data -> {
      float totalLevels = data.get(key, 0f) + amount;
      if (totalLevels <= 0.005f) {
        data.remove(key);
      } else {
        data.put(key, totalLevels);
      }
    });
  }

  /**
   * Adds to the armor stat for the given key if the tool is in a valid armor slot
   * @param tool     Tool instance
   * @param context  Equipment change context
   * @param key      Key to modify
   * @param amount   Amount to add
   * @param heldTag  Tag to check to validate held items, null means held disallowed
   */
  public static void addStatIfArmor(IToolStackView tool, EquipmentChangeContext context, TinkerDataKey<Float> key, float amount, boolean allowBroken, @Nullable TagKey<Item> heldTag) {
    if (ArmorLevelModule.validSlot(tool, context.getChangedSlot(), heldTag) && (!tool.isBroken() || allowBroken)) {
      addStat(context, key, amount);
    }
  }

  /**
   * Gets the total level from the key in the entity modifier data
   * @param living  Living entity
   * @param key     Key to get
   * @return  Level from the key
   */
  public static float getStat(LivingEntity living, TinkerDataKey<Float> key) {
    return living.getCapability(TinkerDataCapability.CAPABILITY).resolve().map(data -> data.get(key)).orElse(0f);
  }


  /* Builder */

  public static Builder builder(TinkerDataKey<Float> key) {
    return new Builder(key);
  }

  @Setter
  @Accessors(fluent = true)
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<Builder> implements LevelingValue.Builder<ArmorStatModule> {
    private final TinkerDataKey<Float> key;
    private boolean allowBroken = false;
    @Nullable
    private TagKey<Item> heldTag;
    private TooltipStyle tooltipStyle = TooltipStyle.NONE;

    public Builder allowBroken() {
      this.allowBroken = true;
      return this;
    }

    @Override
    public ArmorStatModule amount(float flat, float eachLevel) {
      return new ArmorStatModule(key, new LevelingValue(flat, eachLevel), allowBroken, heldTag, tooltipStyle, condition);
    }
  }
}

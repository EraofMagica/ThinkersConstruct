package dev.tocraft.thconstruct.library.modifiers.modules.combat;

import com.google.common.collect.ImmutableSet;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.eomantle.data.loadable.field.LoadableField;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.predicate.damage.DamageSourcePredicate;
import dev.tocraft.eomantle.data.predicate.entity.LivingEntityPredicate;
import dev.tocraft.thconstruct.library.json.TinkerLoadables;
import dev.tocraft.thconstruct.library.json.predicate.TinkerPredicate;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.ArmorLootingModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.LootingModifierHook;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.IntLevelModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModuleBuilder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.context.LootingContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;
import java.util.Set;

/**
 * Module for increasing the looting level, used for tools, on pants, and from bows
 * Currently, does not support incremental.
 */
public interface LootingModule extends ModifierModule, IntLevelModule, ConditionalModule<IToolStackView> {
  /* Common fields */
  LoadableField<IJsonPredicate<LivingEntity>,LootingModule> HOLDER = LivingEntityPredicate.LOADER.defaultField("holder", LootingModule::holder);
  LoadableField<IJsonPredicate<LivingEntity>,LootingModule> TARGET = LivingEntityPredicate.LOADER.defaultField("target", LootingModule::target);
  LoadableField<IJsonPredicate<DamageSource>,LootingModule> DAMAGE_SOURCE = DamageSourcePredicate.LOADER.defaultField("damage_source", LootingModule::damageSource);

  /** Condition on the entity attacking */
  IJsonPredicate<LivingEntity> holder();
  /** Condition on the target */
  IJsonPredicate<LivingEntity> target();
  /** Condition on the damage source used */
  IJsonPredicate<DamageSource> damageSource();

  /** Checks if the conditions match the given context */
  default boolean matchesConditions(IToolStackView tool, ModifierEntry modifier, LootingContext context) {
    return condition().matches(tool, modifier) && holder().matches(context.getHolder()) && TinkerPredicate.matches(target(), context.getLivingTarget()) && TinkerPredicate.matches(damageSource(), context.getDamageSource());
  }

  /** Creates a new builder instance */
  static Builder builder() {
    return new Builder();
  }

  /** Shared builder instance */
  @SuppressWarnings("unused") // API
  @Setter
  @Accessors(fluent = true)
  class Builder extends ModuleBuilder.Stack<Builder> {
    private int level = 1;
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    private IJsonPredicate<DamageSource> damageSource = DamageSourcePredicate.ANY;

    private Builder() {}

    /** Builds a module for weapon looting */
    public Weapon weapon() {
      return new Weapon(level, holder, target, damageSource, condition);
    }

    /**
     * Creates a new armor harvest module
     * @param slots  Slots to allow this to run
     * @return  Module instance
     */
    public Armor armor(EquipmentSlot... slots) {
      if (slots.length == 0) {
        throw new IllegalArgumentException("Must have at least 1 slot");
      }
      // immutable set preserves insertion order
      return new Armor(level, holder, target, damageSource, condition, ImmutableSet.copyOf(slots));
    }

    /** Creates a new armor harvest module with the default slots */
    public Armor armor() {
      return armor(EquipmentSlot.values());
    }
  }

  /** Implementation for weapon looting */
  record Weapon(int level, IJsonPredicate<LivingEntity> holder, IJsonPredicate<LivingEntity> target, IJsonPredicate<DamageSource> damageSource, ModifierCondition<IToolStackView> condition) implements LootingModule, LootingModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Weapon>defaultHooks(ModifierHooks.WEAPON_LOOTING);
    public static final RecordLoadable<Weapon> LOADER = RecordLoadable.create(IntLevelModule.FIELD, HOLDER, TARGET, DAMAGE_SOURCE, ModifierCondition.TOOL_FIELD, Weapon::new);

    @Override
    public int updateLooting(IToolStackView tool, ModifierEntry modifier, LootingContext context, int looting) {
      if (matchesConditions(tool, modifier, context)) {
        looting += getLevel(modifier);
      }
      return looting;
    }

    @Override
    public RecordLoadable<Weapon> getLoader() {
      return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }
  }

  /** Implementation for armor looting */
  record Armor(int level, IJsonPredicate<LivingEntity> holder, IJsonPredicate<LivingEntity> target, IJsonPredicate<DamageSource> damageSource, ModifierCondition<IToolStackView> condition, Set<EquipmentSlot> slots) implements LootingModule, ArmorLootingModifierHook {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<Armor>defaultHooks(ModifierHooks.ARMOR_LOOTING);
    public static final RecordLoadable<Armor> LOADER = RecordLoadable.create(IntLevelModule.FIELD, HOLDER, TARGET, DAMAGE_SOURCE, ModifierCondition.TOOL_FIELD, TinkerLoadables.EQUIPMENT_SLOT_SET.requiredField("slots", Armor::slots), Armor::new);

    @Override
    public int updateArmorLooting(IToolStackView tool, ModifierEntry modifier, LootingContext context, EquipmentContext equipment, EquipmentSlot slot, int looting) {
      if (slots.contains(slot) && matchesConditions(tool, modifier, context)) {
        looting += getLevel(modifier);
      }
      return looting;
    }

    @Override
    public RecordLoadable<Armor> getLoader() {
      return LOADER;
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }
  }
}


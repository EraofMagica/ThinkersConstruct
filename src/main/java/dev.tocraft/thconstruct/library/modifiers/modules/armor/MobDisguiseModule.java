package dev.tocraft.thconstruct.library.modifiers.modules.armor;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot.Type;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module for armor modifiers that makes this entity appear to be another entity from afar
 */
public record MobDisguiseModule(EntityType<?> entity) implements EquipmentChangeModifierHook, ModifierModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MobDisguiseModule>defaultHooks(ModifierHooks.EQUIPMENT_CHANGE);
  public static final RecordLoadable<MobDisguiseModule> LOADER = RecordLoadable.create(Loadables.ENTITY_TYPE.requiredField("entity", MobDisguiseModule::entity), MobDisguiseModule::new);

  /**
   * Data key for all disguises on an entity
   */
  public static final ComputableDataKey<Multiset<EntityType<?>>> DISGUISES = dev.tocraft.thconstruct.ThConstruct.createKey("mob_disguise", HashMultiset::create);

  @Override
  public void onEquip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> data.computeIfAbsent(DISGUISES).add(entity, modifier.getLevel()));
    }
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot().getType() == Type.ARMOR) {
      context.getTinkerData().ifPresent(data -> {
        Multiset<EntityType<?>> disguises = data.get(DISGUISES);
        if (disguises != null) {
          disguises.remove(entity, modifier.getLevel());
        }
      });
    }
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<MobDisguiseModule> getLoader() {
    return LOADER;
  }
}

package dev.tocraft.thconstruct.library.modifiers.modules.armor;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EquipmentSlot;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.predicate.damage.DamageSourcePredicate;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.DamageBlockModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModuleBuilder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Module to block damage of the passed sources
 * @param source  Predicate of sources to block
 */
public record BlockDamageSourceModule(IJsonPredicate<DamageSource> source, ModifierCondition<IToolStackView> condition) implements DamageBlockModifierHook, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = List.of(ModifierHooks.DAMAGE_BLOCK);
  public static final RecordLoadable<BlockDamageSourceModule> LOADER = RecordLoadable.create(
    DamageSourcePredicate.LOADER.defaultField("damage_source", BlockDamageSourceModule::source),
    ModifierCondition.TOOL_FIELD,
    BlockDamageSourceModule::new);

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public boolean isDamageBlocked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount) {
    return condition.matches(tool, modifier) && this.source.matches(source);
  }

  @Override
  public RecordLoadable<BlockDamageSourceModule> getLoader() {
    return LOADER;
  }
  

  /* Builder */

  public static Builder source(IJsonPredicate<DamageSource> source) {
    return new Builder(source);
  }

  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  public static class Builder extends ModuleBuilder.Stack<Builder> {
    private final IJsonPredicate<DamageSource> source;

    public BlockDamageSourceModule build() {
      return new BlockDamageSourceModule(source, condition);
    }
  }
}

package dev.tocraft.thconstruct.library.modifiers.modules.behavior;

import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.json.math.FormulaLoadable;
import dev.tocraft.thconstruct.library.json.math.ModifierFormula;
import dev.tocraft.thconstruct.library.json.math.ModifierFormula.FallbackFormula;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.behavior.RepairFactorModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for multiplying tool repair */
public record RepairModule(ModifierFormula formula, ModifierCondition<IToolStackView> condition) implements RepairFactorModifierHook, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<RepairModule>defaultHooks(ModifierHooks.REPAIR_FACTOR);
  public static final int FACTOR = 1;
  /** Formula instance for the loader */
  private static final FormulaLoadable FORMULA = new FormulaLoadable(FallbackFormula.PERCENT, "level", "factor");
  /** Loader instance */
  public static final RecordLoadable<RepairModule> LOADER = RecordLoadable.create(FORMULA.directField(RepairModule::formula), ModifierCondition.TOOL_FIELD, RepairModule::new);

  /** Creates a builder instance */
  public static FormulaLoadable.Builder<RepairModule> builder() {
    return FORMULA.builder(RepairModule::new);
  }

  @Override
  public float getRepairFactor(IToolStackView tool, ModifierEntry entry, float factor) {
    return formula.apply(formula.processLevel(entry), factor);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<RepairModule> getLoader() {
    return LOADER;
  }
}

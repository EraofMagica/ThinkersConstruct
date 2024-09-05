package dev.tocraft.thconstruct.library.modifiers.modules.mining;

import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import dev.tocraft.eomantle.data.loadable.primitive.BooleanLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.predicate.block.BlockPredicate;
import dev.tocraft.eomantle.data.predicate.entity.LivingEntityPredicate;
import dev.tocraft.thconstruct.library.json.math.ModifierFormula;
import dev.tocraft.thconstruct.library.json.variable.VariableFormula;
import dev.tocraft.thconstruct.library.json.variable.mining.MiningSpeedFormula;
import dev.tocraft.thconstruct.library.json.variable.mining.MiningSpeedVariable;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ConditionalStatTooltip;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.stat.INumericToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Implementation of attack damage conditioned on the attacker or target's properties
 * @param block      Blocks to boost speed
 * @param holder     Condition on the entity holding this tool
 * @param formula    Damage formula
 * @param condition  Standard modifier conditions
 */
public record ConditionalMiningSpeedModule(
  IJsonPredicate<BlockState> block, IJsonPredicate<LivingEntity> holder, boolean requireEffective,
  MiningSpeedFormula formula, ModifierCondition<IToolStackView> condition
) implements BreakSpeedModifierHook, ConditionalStatTooltip, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ConditionalMiningSpeedModule>defaultHooks(ModifierHooks.BREAK_SPEED, ModifierHooks.TOOLTIP);
  public static final RecordLoadable<ConditionalMiningSpeedModule> LOADER = RecordLoadable.create(
    BlockPredicate.LOADER.defaultField("blocks", ConditionalMiningSpeedModule::block),
    LivingEntityPredicate.LOADER.defaultField("entity", ConditionalMiningSpeedModule::holder),
    BooleanLoadable.INSTANCE.defaultField("require_effective", true, ConditionalMiningSpeedModule::requireEffective),
    MiningSpeedFormula.LOADER.directField(ConditionalMiningSpeedModule::formula),
    ModifierCondition.TOOL_FIELD,
    ConditionalMiningSpeedModule::new);

  @Override
  public boolean percent() {
    return formula.percent();
  }

  @Nullable
  @Override
  public Integer getPriority() {
    // run multipliers a bit later
    return percent() ? 75 : null;
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    Player player = event.getEntity();
    if ((isEffective || !requireEffective) && condition.matches(tool, modifier) && block.matches(event.getState()) && holder.matches(player)) {
      event.setNewSpeed(formula.apply(tool, modifier, event, player, sideHit, event.getOriginalSpeed(), event.getNewSpeed(), miningSpeedModifier));
    }
  }

  @Override
  public INumericToolStat<?> stat() {
    return ToolStats.MINING_SPEED;
  }

  @Override
  public float computeTooltipValue(IToolStackView tool, ModifierEntry entry, @Nullable Player player) {
    return formula.apply(tool, entry, null, player, null, 1, 1, 1);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<ConditionalMiningSpeedModule> getLoader() {
    return LOADER;
  }


  /* Builder */

  /** Creates a builder instance */
  public static Builder builder() {
    return new Builder();
  }

  /** Builder class */
  @Accessors(fluent = true)
  public static class Builder extends VariableFormula.Builder<Builder,ConditionalMiningSpeedModule,MiningSpeedVariable> {
    @Setter
    private IJsonPredicate<BlockState> blocks = BlockPredicate.ANY;
    @Setter
    private IJsonPredicate<LivingEntity> holder = LivingEntityPredicate.ANY;
    private boolean requireEffective = true;

    private Builder() {
      super(MiningSpeedFormula.VARIABLES);
    }

    /** Sets this to a percent boost formula */
    public Builder allowIneffective() {
      this.requireEffective = false;
      return this;
    }

    @Override
    protected ConditionalMiningSpeedModule build(ModifierFormula formula) {
      return new ConditionalMiningSpeedModule(blocks, holder, requireEffective, new MiningSpeedFormula(formula, variables, percent), condition);
    }
  }
}

package dev.tocraft.thconstruct.library.tools.definition.module.mining;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.eomantle.data.loadable.primitive.FloatLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.predicate.block.BlockPredicate;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for adjusting the mining speed */
public record MiningSpeedModifierModule(float modifier, IJsonPredicate<BlockState> predicate) implements MiningSpeedToolHook, ToolModule {
  public static final RecordLoadable<MiningSpeedModifierModule> LOADER = RecordLoadable.create(
    FloatLoadable.ANY.requiredField("modifier", MiningSpeedModifierModule::modifier),
    BlockPredicate.LOADER.directField("predicate_type", MiningSpeedModifierModule::predicate),
    MiningSpeedModifierModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MiningSpeedModifierModule>defaultHooks(ToolHooks.MINING_SPEED);

  /** Modifies the given tag */
  public static MiningSpeedModifierModule tag(TagKey<Block> tag, float modifier) {
    return new MiningSpeedModifierModule(modifier, BlockPredicate.tag(tag));
  }

  /** Modifies the given blocks */
  public static MiningSpeedModifierModule blocks(float modifier, Block... blocks) {
    return new MiningSpeedModifierModule(modifier, BlockPredicate.set(blocks));
  }

  @Override
  public RecordLoadable<MiningSpeedModifierModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float modifyDestroySpeed(IToolStackView tool, BlockState state, float speed) {
    if (predicate.matches(state)) {
      speed *= modifier;
    }
    return speed;
  }
}

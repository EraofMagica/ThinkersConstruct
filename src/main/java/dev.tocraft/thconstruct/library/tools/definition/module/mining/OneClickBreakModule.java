package dev.tocraft.thconstruct.library.tools.definition.module.mining;

import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.predicate.block.BlockPredicate;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/** Module for making tool break the given predicate in one click without instant breaking */
public record OneClickBreakModule(IJsonPredicate<BlockState> predicate) implements MiningSpeedToolHook, ToolModule {
  public static final RecordLoadable<OneClickBreakModule> LOADER = RecordLoadable.create(BlockPredicate.LOADER.directField("predicate_type", OneClickBreakModule::predicate), OneClickBreakModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<OneClickBreakModule>defaultHooks(ToolHooks.MINING_SPEED);

  /** Modifies the given tag */
  public static OneClickBreakModule tag(TagKey<Block> tag) {
    return new OneClickBreakModule(BlockPredicate.tag(tag));
  }

  @Override
  public RecordLoadable<OneClickBreakModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public float modifyDestroySpeed(IToolStackView tool, BlockState state, float speed) {
    if (predicate.matches(state)) {
      speed = state.getBlock().defaultDestroyTime() * 20;
    }
    return speed;
  }
}

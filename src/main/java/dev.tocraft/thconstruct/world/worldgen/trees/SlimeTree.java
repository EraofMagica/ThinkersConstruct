package dev.tocraft.thconstruct.world.worldgen.trees;

import net.minecraft.core.Holder;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import dev.tocraft.thconstruct.world.TinkerStructures;
import dev.tocraft.thconstruct.world.block.FoliageType;

public class SlimeTree extends AbstractTreeGrower {

  private final FoliageType foliageType;

  public SlimeTree(FoliageType foliageType) {
    this.foliageType = foliageType;
  }

  @Override
  protected Holder<? extends ConfiguredFeature<?, ?>> getConfiguredFeature(RandomSource random, boolean largeHive) {
    return (switch (this.foliageType) {
      case EARTH -> TinkerStructures.earthSlimeTree;
      case SKY -> TinkerStructures.skySlimeTree;
      case ENDER -> random.nextFloat() < 0.85f ? TinkerStructures.enderSlimeTreeTall : TinkerStructures.enderSlimeTree;
      case BLOOD -> TinkerStructures.bloodSlimeFungus;
      case ICHOR -> TinkerStructures.ichorSlimeFungus;
    }).getHolder().orElseThrow();
  }
}

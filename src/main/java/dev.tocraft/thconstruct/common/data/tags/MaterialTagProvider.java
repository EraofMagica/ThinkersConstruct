package dev.tocraft.thconstruct.common.data.tags;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.data.tinkering.AbstractMaterialTagProvider;
import dev.tocraft.thconstruct.tools.data.material.MaterialIds;

public class MaterialTagProvider extends AbstractMaterialTagProvider {
  public MaterialTagProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, dev.tocraft.thconstruct.ThConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    tag(TinkerTags.Materials.NETHER).add(
      // tier 1
      MaterialIds.wood, MaterialIds.flint, MaterialIds.rock, MaterialIds.bone,
      MaterialIds.leather, MaterialIds.vine, MaterialIds.string,
      // tier 2
      MaterialIds.iron, MaterialIds.scorchedStone, MaterialIds.slimewood, MaterialIds.necroticBone,
      // tier 3
      MaterialIds.nahuatl, MaterialIds.cobalt,
      MaterialIds.darkthread,
      // tier 4
      MaterialIds.manyullyn, MaterialIds.queensSlime, MaterialIds.blazingBone, MaterialIds.blazewood,
      MaterialIds.ancientHide
    );
  }

  @Override
  public String getName() {
    return "Tinkers' Construct Material Tag Provider";
  }
}

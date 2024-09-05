package dev.tocraft.thconstruct.common.data.tags;

import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.data.ExistingFileHelper;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.smeltery.TinkerSmeltery;
import dev.tocraft.thconstruct.tables.TinkerTables;

import javax.annotation.Nullable;

public class BlockEntityTypeTagProvider extends TagsProvider<BlockEntityType<?>> {
  @SuppressWarnings("deprecation")
  public BlockEntityTypeTagProvider(DataGenerator generatorIn, @Nullable ExistingFileHelper existingFileHelper) {
    super(generatorIn, Registry.BLOCK_ENTITY_TYPE, dev.tocraft.thconstruct.ThConstruct.MOD_ID, existingFileHelper);
  }

  @Override
  protected void addTags() {
    this.tag(TinkerTags.TileEntityTypes.CRAFTING_STATION_BLACKLIST)
        .add(
          BlockEntityType.FURNACE, BlockEntityType.BLAST_FURNACE, BlockEntityType.SMOKER, BlockEntityType.BREWING_STAND,
          TinkerTables.craftingStationTile.get(), TinkerTables.tinkerStationTile.get(), TinkerTables.partBuilderTile.get(),
          TinkerTables.partChestTile.get(), TinkerTables.tinkersChestTile.get(), TinkerTables.castChestTile.get(),
          TinkerSmeltery.basin.get(), TinkerSmeltery.table.get(),
          TinkerSmeltery.melter.get(), TinkerSmeltery.smeltery.get(), TinkerSmeltery.foundry.get()
        );

  }

  @Override
  public String getName() {
    return "Tinkers' Construct Block Entity Type Tags";
  }
}

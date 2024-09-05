package dev.tocraft.thconstruct.library.tools.helper;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import dev.tocraft.eomantle.data.predicate.block.BlockPredicate;
import dev.tocraft.thconstruct.fixture.MaterialItemFixture;
import dev.tocraft.thconstruct.fixture.MaterialStatsFixture;
import dev.tocraft.thconstruct.fixture.ToolDefinitionFixture;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinitionDataBuilder;
import dev.tocraft.thconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.material.PartStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import dev.tocraft.thconstruct.library.tools.definition.module.mining.MiningSpeedToolHook;
import dev.tocraft.thconstruct.library.tools.item.ModifiableItem;
import dev.tocraft.thconstruct.library.tools.item.ToolItemTest;
import dev.tocraft.thconstruct.library.tools.nbt.MultiplierNBT;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

import static org.assertj.core.api.Assertions.assertThat;

class ToolHarvestLogicTest extends ToolItemTest {
  private static ModifiableItem pickaxeTool;

  @BeforeAll
  synchronized static void beforeAllToolLogic() {
    MaterialItemFixture.init();
    if (pickaxeTool == null) {
      pickaxeTool = new ModifiableItem(new Item.Properties().stacksTo(1), ToolDefinitionFixture.getStandardToolDefinition());
      ForgeRegistries.ITEMS.register(new ResourceLocation("test", "pickaxe"), pickaxeTool);
    }
  }

  @Test
  void calcSpeed_dirt_notEffective() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.DIRT.defaultBlockState());

    assertThat(speed).isEqualTo(1f);
  }

  @Test
  void calcSpeed_cobble_effective() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.COBBLESTONE.defaultBlockState());

    assertThat(speed).isEqualTo(MaterialStatsFixture.MATERIAL_STATS_HEAD.miningSpeed());
  }

  @Test
  void calcSpeed_obsidian_notEnoughHarvestLevel() {
    ItemStack tool = buildTestTool(pickaxeTool);

    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.OBSIDIAN.defaultBlockState());

    assertThat(speed).isEqualTo(1f);
  }

  @Test
  void calcSpeed_broken_slowButNotZero() {
    ItemStack tool = buildTestTool(pickaxeTool);
    breakTool(tool);

    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.DIRT.defaultBlockState());

    assertThat(speed).isLessThan(1f);
    assertThat(speed).isGreaterThan(0f);
  }

  @Test
  void calcSpeed_effective_withMiningModifier() {
    float modifier = 2f;

    ToolDefinition definition = new ToolDefinition(new ResourceLocation("test", "mining_tool"));
    definition.setData(ToolDefinitionDataBuilder
                         .builder()
                         .module(new IsEffectiveModule(BlockPredicate.set(Blocks.COBBLESTONE), true))
                         .module(PartStatsModule.parts()
                                                .part(MaterialItemFixture.MATERIAL_ITEM_HEAD)
                                                .part(MaterialItemFixture.MATERIAL_ITEM_HANDLE)
                                                .part(MaterialItemFixture.MATERIAL_ITEM_EXTRA).build())
                         .module(new MultiplyStatsModule(MultiplierNBT.builder().set(ToolStats.MINING_SPEED, modifier).build()))
                         .build());

    ModifiableItem toolWithMiningModifier = new ModifiableItem(new Item.Properties(), definition);
    ForgeRegistries.ITEMS.register(new ResourceLocation("test", "tool_with_mining_modifier"), toolWithMiningModifier);
    ItemStack tool = buildTestTool(toolWithMiningModifier);

    // boosted by correct block
    float speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.COBBLESTONE.defaultBlockState());
    assertThat(speed).isEqualTo(MaterialStatsFixture.MATERIAL_STATS_HEAD.miningSpeed() * modifier);

    // default speed
    speed = MiningSpeedToolHook.getDestroySpeed(tool, Blocks.STONE.defaultBlockState());
    assertThat(speed).isEqualTo(1.0f);
  }
}

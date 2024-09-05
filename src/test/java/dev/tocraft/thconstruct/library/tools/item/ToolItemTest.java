package dev.tocraft.thconstruct.library.tools.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import dev.tocraft.thconstruct.fixture.MaterialFixture;
import dev.tocraft.thconstruct.fixture.MaterialItemFixture;
import dev.tocraft.thconstruct.fixture.ToolDefinitionFixture;
import dev.tocraft.thconstruct.library.materials.MaterialRegistryExtension;
import dev.tocraft.thconstruct.library.tools.helper.ToolBuildHandler;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.nbt.MaterialNBT;
import dev.tocraft.thconstruct.library.tools.stat.ModifierStatsBuilder;
import dev.tocraft.thconstruct.test.BaseMcTest;

@ExtendWith(MaterialRegistryExtension.class)
public abstract class ToolItemTest extends BaseMcTest {

  protected static ModifiableItem tool;
  protected ItemStack testItemStack;

  @BeforeAll
  synchronized static void beforeAllToolCore() {
    MaterialItemFixture.init();
    if (tool == null) {
      tool = new ModifiableItem(new Item.Properties().stacksTo(1), ToolDefinitionFixture.getStandardToolDefinition());
      ForgeRegistries.ITEMS.register(new ResourceLocation("test", "toolcore"), tool);
    }
    setupTierSorting();
    ModifierStatsBuilder.disableFilter();
  }

  @BeforeEach
  void setUpToolCore() {
    testItemStack = buildTestTool(tool);
  }

  protected ItemStack buildTestTool(IModifiable tool) {
    return ToolBuildHandler.buildItemFromMaterials(tool, MaterialNBT.of(MaterialFixture.MATERIAL_WITH_HEAD, MaterialFixture.MATERIAL_WITH_HANDLE, MaterialFixture.MATERIAL_WITH_EXTRA));
  }

  /** Checks if the test item is broken */
  protected boolean IsTestItemBroken() {
    return ToolDamageUtil.isBroken(testItemStack);
  }

  /** Breaks a tool using high damage */
  protected void breakTool(ItemStack stack) {
    stack.setDamageValue(99999);
  }
}

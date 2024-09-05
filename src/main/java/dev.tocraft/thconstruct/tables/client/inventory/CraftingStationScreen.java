package dev.tocraft.thconstruct.tables.client.inventory;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import dev.tocraft.thconstruct.tables.block.entity.table.CraftingStationBlockEntity;
import dev.tocraft.thconstruct.tables.menu.CraftingStationContainerMenu;

public class CraftingStationScreen extends BaseTabbedScreen<CraftingStationBlockEntity,CraftingStationContainerMenu> {
  private static final ResourceLocation CRAFTING_TABLE_GUI_TEXTURES = new ResourceLocation("textures/gui/container/crafting_table.png");

  public CraftingStationScreen(CraftingStationContainerMenu container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    addChestSideInventory(playerInventory);
  }

  @Override
  protected void renderBg(PoseStack matrices, float partialTicks, int mouseX, int mouseY) {
    this.drawBackground(matrices, CRAFTING_TABLE_GUI_TEXTURES);
    super.renderBg(matrices, partialTicks, mouseX, mouseY);
  }
}

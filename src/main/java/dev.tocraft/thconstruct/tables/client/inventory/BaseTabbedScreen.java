package dev.tocraft.thconstruct.tables.client.inventory;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.Rect2i;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.level.block.entity.BlockEntity;
import dev.tocraft.eomantle.client.screen.ElementScreen;
import dev.tocraft.eomantle.client.screen.MultiModuleScreen;
import dev.tocraft.thconstruct.library.client.GuiUtil;
import dev.tocraft.thconstruct.library.client.Icons;
import dev.tocraft.thconstruct.library.recipe.partbuilder.Pattern;
import dev.tocraft.thconstruct.tables.client.inventory.module.SideInventoryScreen;
import dev.tocraft.thconstruct.tables.client.inventory.widget.TinkerTabsWidget;
import dev.tocraft.thconstruct.tables.menu.TabbedContainerMenu;
import dev.tocraft.thconstruct.tables.menu.module.SideInventoryContainer;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class BaseTabbedScreen<TILE extends BlockEntity, CONTAINER extends TabbedContainerMenu<TILE>> extends MultiModuleScreen<CONTAINER> {
  protected static final Component COMPONENT_WARNING = dev.tocraft.thconstruct.ThConstruct.makeTranslation("gui", "warning");
  protected static final Component COMPONENT_ERROR = dev.tocraft.thconstruct.ThConstruct.makeTranslation("gui", "error");

  public static final ResourceLocation BLANK_BACK = dev.tocraft.thconstruct.ThConstruct.getResource("textures/gui/blank.png");

  @Nullable
  protected final TILE tile;
  protected TinkerTabsWidget tabsScreen;

  public BaseTabbedScreen(CONTAINER container, Inventory playerInventory, Component title) {
    super(container, playerInventory, title);
    this.tile = container.getTile();
  }

  @Override
  protected void init() {
    super.init();

    this.tabsScreen = addRenderableWidget(new TinkerTabsWidget(this));
  }

  @Nullable
  public TILE getTileEntity() {
    return this.tile;
  }

  protected void drawIcon(PoseStack matrices, Slot slot, ElementScreen element) {
    RenderSystem.setShaderTexture(0, Icons.ICONS);
    element.draw(matrices, slot.x + this.cornerX - 1, slot.y + this.cornerY - 1);
  }

  protected void drawIconEmpty(PoseStack matrices, Slot slot, ElementScreen element) {
    if (slot.hasItem()) {
      return;
    }

    this.drawIcon(matrices, slot, element);
  }

  protected void drawIconEmpty(PoseStack matrices, Slot slot, Pattern pattern) {
    if (!slot.hasItem()) {
      GuiUtil.renderPattern(matrices, pattern, slot.x + this.cornerX, slot.y + this.cornerY);
    }
  }

  public void error(Component message) {
  }

  public void warning(Component message) {
  }

  public void updateDisplay() {
  }

  protected void addChestSideInventory(Inventory inventory) {
    SideInventoryContainer<?> sideInventoryContainer = getMenu().getSubContainer(SideInventoryContainer.class);
    if (sideInventoryContainer != null) {
      // no title if missing one
      Component sideInventoryName = Component.empty();
      BlockEntity te = sideInventoryContainer.getTile();
      if (te instanceof MenuProvider) {
        sideInventoryName = Objects.requireNonNullElse(((MenuProvider)te).getDisplayName(), Component.empty());
      }

      this.addModule(new SideInventoryScreen<>(this, sideInventoryContainer, inventory, sideInventoryName, sideInventoryContainer.getSlotCount(), sideInventoryContainer.getColumns()));
    }
  }

  @Override
  public List<Rect2i> getModuleAreas() {
    List<Rect2i> areas = super.getModuleAreas();
    areas.add(tabsScreen.getArea());
    return areas;
  }

  @Override
  protected boolean hasClickedOutside(double mouseX, double mouseY, int guiLeft, int guiTop, int mouseButton) {
    return super.hasClickedOutside(mouseX, mouseY, guiLeft, guiTop, mouseButton)
      && !tabsScreen.isMouseOver(mouseX, mouseY);
  }
}

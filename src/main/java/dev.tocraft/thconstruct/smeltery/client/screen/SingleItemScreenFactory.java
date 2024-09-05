package dev.tocraft.thconstruct.smeltery.client.screen;

import net.minecraft.client.gui.screens.MenuScreens.ScreenConstructor;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.block.entity.BlockEntity;
import dev.tocraft.eomantle.client.screen.BackgroundContainerScreen;
import dev.tocraft.thconstruct.smeltery.menu.SingleItemContainerMenu;

import javax.annotation.Nullable;

/**
 * Screen factory for the single item container, one container for multiple backgrounds
 */
public class SingleItemScreenFactory implements ScreenConstructor<SingleItemContainerMenu,BackgroundContainerScreen<SingleItemContainerMenu>> {
  private static final int HEIGHT = 133;
  private static final ResourceLocation DEFAULT = dev.tocraft.thconstruct.ThConstruct.getResource("textures/gui/blank.png");

  /**
   * Gets the background path for the given tile
   * @param tile  Tile
   * @return  Background path
   */
  private static ResourceLocation getBackground(@Nullable BlockEntity tile) {
    if (tile != null) {
      ResourceLocation id = Registry.BLOCK_ENTITY_TYPE.getKey(tile.getType());
      if (id != null) {
        return new ResourceLocation(id.getNamespace(), String.format("textures/gui/%s.png", id.getPath()));
      }
    }
    return DEFAULT;
  }

  @Override
  public BackgroundContainerScreen<SingleItemContainerMenu> create(SingleItemContainerMenu container, Inventory inventory, Component name) {
    return new BackgroundContainerScreen<>(container, inventory, name, HEIGHT, getBackground(container.getTile()));
  }
}

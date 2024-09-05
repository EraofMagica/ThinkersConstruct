package dev.tocraft.thconstruct.plugin.jsonthings;

import dev.gigaherz.jsonthings.things.client.ItemColorHandler;
import net.minecraft.world.item.Item;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import dev.tocraft.thconstruct.library.client.model.TinkerItemProperties;
import dev.tocraft.thconstruct.library.client.model.tools.ToolModel;

/** Handles anything that requires clientside class loading */
public class PluginClient {
  public static void init() {
    ItemColorHandler.register(dev.tocraft.thconstruct.ThConstruct.resourceString("tool"), block -> ToolModel.COLOR_HANDLER);
    FMLJavaModLoadingContext.get().getModEventBus().addListener(PluginClient::clientSetup);
  }

  private static void clientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
      for (Item item : FlexItemTypes.TOOL_ITEMS) {
        TinkerItemProperties.registerToolProperties(item);
      }
      for (Item item : FlexItemTypes.CROSSBOW_ITEMS) {
        TinkerItemProperties.registerCrossbowProperties(item);
      }
      for (Item item : FlexItemTypes.ARMOR_ITEMS) {
        TinkerItemProperties.registerBrokenProperty(item);
      }
    });
  }
}

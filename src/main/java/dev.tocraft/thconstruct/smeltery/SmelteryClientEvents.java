package dev.tocraft.thconstruct.smeltery;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.ModelEvent.RegisterGeometryLoaders;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import dev.tocraft.eomantle.client.model.FaucetFluidLoader;
import dev.tocraft.thconstruct.common.ClientEventBase;
import dev.tocraft.thconstruct.library.client.model.block.CastingModel;
import dev.tocraft.thconstruct.library.client.model.block.ChannelModel;
import dev.tocraft.thconstruct.library.client.model.block.FluidTextureModel;
import dev.tocraft.thconstruct.library.client.model.block.MelterModel;
import dev.tocraft.thconstruct.library.client.model.block.TankModel;
import dev.tocraft.thconstruct.smeltery.client.render.CastingBlockEntityRenderer;
import dev.tocraft.thconstruct.smeltery.client.render.ChannelBlockEntityRenderer;
import dev.tocraft.thconstruct.smeltery.client.render.FaucetBlockEntityRenderer;
import dev.tocraft.thconstruct.smeltery.client.render.HeatingStructureBlockEntityRenderer;
import dev.tocraft.thconstruct.smeltery.client.render.MelterBlockEntityRenderer;
import dev.tocraft.thconstruct.smeltery.client.render.TankBlockEntityRenderer;
import dev.tocraft.thconstruct.smeltery.client.screen.AlloyerScreen;
import dev.tocraft.thconstruct.smeltery.client.screen.HeatingStructureScreen;
import dev.tocraft.thconstruct.smeltery.client.screen.MelterScreen;
import dev.tocraft.thconstruct.smeltery.client.screen.SingleItemScreenFactory;

@SuppressWarnings("unused")
@EventBusSubscriber(modid= dev.tocraft.thconstruct.ThConstruct.MOD_ID, value= Dist.CLIENT, bus= Bus.MOD)
public class SmelteryClientEvents extends ClientEventBase {
  @SubscribeEvent
  static void addResourceListener(RegisterClientReloadListenersEvent event) {
    FaucetFluidLoader.initialize(event);
  }

  @SubscribeEvent
  static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
    event.registerBlockEntityRenderer(TinkerSmeltery.tank.get(), TankBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.faucet.get(), FaucetBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.channel.get(), ChannelBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.table.get(), CastingBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.basin.get(), CastingBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.melter.get(), MelterBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.alloyer.get(), TankBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.smeltery.get(), HeatingStructureBlockEntityRenderer::new);
    event.registerBlockEntityRenderer(TinkerSmeltery.foundry.get(), HeatingStructureBlockEntityRenderer::new);
  }

  @SubscribeEvent
  static void clientSetup(final FMLClientSetupEvent event) {
    // render layers
    RenderType cutout = RenderType.cutout();
    RenderType translucent = RenderType.translucent();
    // seared
    // casting
    // TODO: migrate
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedFaucet.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedBasin.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedTable.get(), cutout);
    // controller
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedMelter.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.smelteryController.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.foundryController.get(), cutout);
    // peripherals
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedDrain.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedDuct.get(), cutout);
    TinkerSmeltery.searedTank.forEach(tank -> ItemBlockRenderTypes.setRenderLayer(tank, cutout));
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedLantern.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedGlass.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedSoulGlass.get(), translucent);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedTintedGlass.get(), translucent);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedGlassPane.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.searedSoulGlassPane.get(), translucent);
    // scorched
    // casting
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedFaucet.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedBasin.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedTable.get(), cutout);
    // controller
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedAlloyer.get(), cutout);
    // peripherals
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedDrain.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedDuct.get(), cutout);
    TinkerSmeltery.scorchedTank.forEach(tank -> ItemBlockRenderTypes.setRenderLayer(tank, cutout));
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedLantern.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedGlass.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedSoulGlass.get(), translucent);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedTintedGlass.get(), translucent);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedGlassPane.get(), cutout);
    ItemBlockRenderTypes.setRenderLayer(TinkerSmeltery.scorchedSoulGlassPane.get(), translucent);

    // screens
    MenuScreens.register(TinkerSmeltery.melterContainer.get(), MelterScreen::new);
    MenuScreens.register(TinkerSmeltery.smelteryContainer.get(), HeatingStructureScreen::new);
    MenuScreens.register(TinkerSmeltery.singleItemContainer.get(), new SingleItemScreenFactory());
    MenuScreens.register(TinkerSmeltery.alloyerContainer.get(), AlloyerScreen::new);
  }

  @SubscribeEvent
  static void registerModelLoaders(RegisterGeometryLoaders event) {
    event.register("tank", TankModel.LOADER);
    event.register("casting", CastingModel.LOADER);
    event.register("melter", MelterModel.LOADER);
    event.register("channel", ChannelModel.LOADER);
    event.register("fluid_texture", FluidTextureModel.LOADER);
  }
}

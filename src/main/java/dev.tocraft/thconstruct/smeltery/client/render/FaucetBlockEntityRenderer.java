package dev.tocraft.thconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import dev.tocraft.eomantle.client.model.FaucetFluidLoader;
import dev.tocraft.eomantle.client.model.fluid.FluidCuboid;
import dev.tocraft.eomantle.client.model.fluid.FluidsModel;
import dev.tocraft.eomantle.client.model.util.ModelHelper;
import dev.tocraft.eomantle.client.render.FluidRenderer;
import dev.tocraft.eomantle.client.render.MantleRenderTypes;
import dev.tocraft.eomantle.client.render.RenderingHelper;
import dev.tocraft.thconstruct.smeltery.block.FaucetBlock;
import dev.tocraft.thconstruct.smeltery.block.entity.FaucetBlockEntity;

import java.util.function.Function;

public class FaucetBlockEntityRenderer implements BlockEntityRenderer<FaucetBlockEntity> {
  public FaucetBlockEntityRenderer(Context context) {}

  @Override
  public void render(FaucetBlockEntity tileEntity, float partialTicks, PoseStack matrices, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
    FluidStack renderFluid = tileEntity.getRenderFluid();
    if (!tileEntity.isPouring() || renderFluid.isEmpty()) {
      return;
    }

    // safety
    Level world = tileEntity.getLevel();
    if (world == null) {
      return;
    }

    // fetch faucet model to determine where to render fluids
    BlockState state = tileEntity.getBlockState();
    FluidsModel.Baked model = ModelHelper.getBakedModel(state, FluidsModel.Baked.class);
    if (model != null) {
      // if side, rotate fluid model
      Direction direction = state.getValue(FaucetBlock.FACING);
      boolean isRotated = RenderingHelper.applyRotation(matrices, direction);

      // fluid props
      IClientFluidTypeExtensions attributes = IClientFluidTypeExtensions.of(renderFluid.getFluid());
      int color = attributes.getTintColor(renderFluid);
      Function<ResourceLocation, TextureAtlasSprite> spriteGetter = Minecraft.getInstance().getTextureAtlas(InventoryMenu.BLOCK_ATLAS);
      TextureAtlasSprite still = spriteGetter.apply(attributes.getStillTexture(renderFluid));
      TextureAtlasSprite flowing = spriteGetter.apply(attributes.getFlowingTexture(renderFluid));
      FluidType fluidType = renderFluid.getFluid().getFluidType();
      boolean isGas = fluidType.isLighterThanAir();
      combinedLightIn = FluidRenderer.withBlockLight(combinedLightIn, fluidType.getLightLevel(renderFluid));

      // render all cubes in the model
      VertexConsumer buffer = bufferIn.getBuffer(MantleRenderTypes.FLUID);
      for (FluidCuboid cube : model.getFluids()) {
        FluidRenderer.renderCuboid(matrices, buffer, cube, 0, still, flowing, color, combinedLightIn, isGas);
      }

      // render into the block(s) below
      FaucetFluidLoader.renderFaucetFluids(world, tileEntity.getBlockPos(), direction, matrices, buffer, still, flowing, color, combinedLightIn);

      // if rotated, pop back rotation
      if(isRotated) {
        matrices.popPose();
      }
    }
  }
}

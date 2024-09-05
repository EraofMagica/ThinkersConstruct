package dev.tocraft.thconstruct.smeltery.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.eomantle.client.model.inventory.ModelItem;
import dev.tocraft.eomantle.client.model.util.ModelHelper;
import dev.tocraft.eomantle.client.render.RenderingHelper;
import dev.tocraft.thconstruct.common.config.Config;
import dev.tocraft.thconstruct.library.client.RenderUtils;
import dev.tocraft.thconstruct.library.client.model.block.MelterModel.Baked;
import dev.tocraft.thconstruct.smeltery.block.entity.controller.MelterBlockEntity;

import java.util.List;

public class MelterBlockEntityRenderer implements BlockEntityRenderer<MelterBlockEntity> {
  public MelterBlockEntityRenderer(Context context) {}

  @Override
  public void render(MelterBlockEntity melter, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int light, int combinedOverlayIn) {
    BlockState state = melter.getBlockState();
    Baked model = ModelHelper.getBakedModel(state, Baked.class);
    if (model != null) {
      // rotate the matrix
      boolean isRotated = RenderingHelper.applyRotation(matrices, state);

      // render fluids
      if (!Config.CLIENT.tankFluidModel.get()) {
        RenderUtils.renderFluidTank(matrices, buffer, model.getFluid(), melter.getTank(), light, partialTicks, false);
      }

      // render items
      List<ModelItem> modelItems = model.getItems();
      for (int i = 0; i < modelItems.size(); i++) {
        RenderingHelper.renderItem(matrices, buffer, melter.getMeltingInventory().getStackInSlot(i), modelItems.get(i), light);
      }

      // pop back rotation
      if (isRotated) {
        matrices.popPose();
      }
    }
  }
}

package dev.tocraft.thconstruct.tables.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.world.Container;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import dev.tocraft.eomantle.client.model.inventory.ModelItem;
import dev.tocraft.eomantle.client.model.util.ModelHelper;
import dev.tocraft.eomantle.client.render.RenderingHelper;
import dev.tocraft.thconstruct.library.client.model.block.TableModel;

import java.util.List;

/**
 * Same as {@link dev.tocraft.eomantle.client.render.InventoryTileEntityRenderer}, but uses {@link TableModel}.
 * TODO: migrate to an interface in Mantle
 * @param <T>  Tile entity type
 */
public class TableTileEntityRenderer<T extends BlockEntity & Container> implements BlockEntityRenderer<T> {
  public TableTileEntityRenderer(Context context) {}

  @Override
  public void render(T inventory, float partialTicks, PoseStack matrices, MultiBufferSource buffer, int light, int combinedOverlayIn) {
    if (!inventory.isEmpty()) {
      BlockState state = inventory.getBlockState();
      TableModel.Baked model = ModelHelper.getBakedModel(state, TableModel.Baked.class);
      if (model != null) {
        boolean isRotated = RenderingHelper.applyRotation(matrices, state);
        List<ModelItem> modelItems = model.getItems();

        for(int i = 0; i < modelItems.size(); ++i) {
          RenderingHelper.renderItem(matrices, buffer, inventory.getItem(i), modelItems.get(i), light);
        }

        if (isRotated) {
          matrices.popPose();
        }
      }
    }
  }

  @Override
  public boolean shouldRenderOffScreen(T tile) {
    return !tile.isEmpty();
  }
}

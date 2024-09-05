package dev.tocraft.thconstruct.smeltery.menu;

import lombok.Getter;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import dev.tocraft.eomantle.inventory.SmartItemHandlerSlot;
import dev.tocraft.eomantle.util.sync.ValidZeroDataSlot;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.shared.inventory.TriggeringBaseContainerMenu;
import dev.tocraft.thconstruct.smeltery.TinkerSmeltery;
import dev.tocraft.thconstruct.smeltery.block.entity.controller.AlloyerBlockEntity;
import dev.tocraft.thconstruct.smeltery.block.entity.module.alloying.MixerAlloyTank;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class AlloyerContainerMenu extends TriggeringBaseContainerMenu<AlloyerBlockEntity> {
  public static final ResourceLocation TOOLTIP_FORMAT = dev.tocraft.thconstruct.ThConstruct.getResource("alloyer");

  @Getter
  private boolean hasFuelSlot = false;
  public AlloyerContainerMenu(int id, @Nullable Inventory inv, @Nullable AlloyerBlockEntity alloyer) {
    super(TinkerSmeltery.alloyerContainer.get(), id, inv, alloyer);

    // create slots
    if (alloyer != null) {
      // refresh cache of neighboring tanks
      Level world = alloyer.getLevel();
      if (world != null && world.isClientSide) {
        MixerAlloyTank alloyTank = alloyer.getAlloyTank();
        for (Direction direction : Direction.values()) {
          if (direction != Direction.DOWN) {
            alloyTank.refresh(direction, true);
          }
        }
      }

      // add fuel slot if present
      BlockPos down = alloyer.getBlockPos().below();
      if (world != null && world.getBlockState(down).is(TinkerTags.Blocks.FUEL_TANKS)) {
        BlockEntity te = world.getBlockEntity(down);
        if (te != null) {
          hasFuelSlot = te.getCapability(ForgeCapabilities.ITEM_HANDLER).filter(handler -> {
            this.addSlot(new SmartItemHandlerSlot(handler, 0, 151, 32));
            return true;
          }).isPresent();
        }
      }

      this.addInventorySlots();

      // syncing
      Consumer<DataSlot> referenceConsumer = this::addDataSlot;
      ValidZeroDataSlot.trackIntArray(referenceConsumer, alloyer.getFuelModule());
    }
  }

  public AlloyerContainerMenu(int id, Inventory inv, FriendlyByteBuf buf) {
    this(id, inv, getTileEntityFromBuf(buf, AlloyerBlockEntity.class));
  }
}

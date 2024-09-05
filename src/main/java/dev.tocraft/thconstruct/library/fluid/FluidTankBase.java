package dev.tocraft.thconstruct.library.fluid;

import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import dev.tocraft.eomantle.block.entity.MantleBlockEntity;
import dev.tocraft.thconstruct.common.network.TinkerNetwork;
import dev.tocraft.thconstruct.smeltery.network.FluidUpdatePacket;

public class FluidTankBase<T extends MantleBlockEntity> extends FluidTank {

  protected T parent;

  public FluidTankBase(int capacity, T parent) {
    super(capacity);
    this.parent = parent;
  }

  @Override
  protected void onContentsChanged() {
    if (parent instanceof IFluidTankUpdater) {
      ((IFluidTankUpdater) parent).onTankContentsChanged();
    }

    parent.setChanged();
    Level level = parent.getLevel();
    if(level != null && !level.isClientSide) {
      TinkerNetwork.getInstance().sendToClientsAround(new FluidUpdatePacket(parent.getBlockPos(), this.getFluid()), level, parent.getBlockPos());
    }
  }
}

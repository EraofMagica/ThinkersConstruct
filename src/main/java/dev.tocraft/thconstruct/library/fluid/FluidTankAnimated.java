package dev.tocraft.thconstruct.library.fluid;

import lombok.Getter;
import lombok.Setter;
import dev.tocraft.eomantle.block.entity.MantleBlockEntity;

public class FluidTankAnimated extends FluidTankBase<MantleBlockEntity> {
  @Getter @Setter
  private float renderOffset;

  public FluidTankAnimated(int capacity, MantleBlockEntity parent) {
    super(capacity, parent);
  }
}

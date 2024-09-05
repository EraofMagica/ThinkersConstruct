package dev.tocraft.thconstruct.library.recipe.fuel;

import net.minecraft.world.level.material.Fluid;
import dev.tocraft.eomantle.recipe.container.IEmptyContainer;

/**
 * Inventory containing just a single fluid
 */
public interface IFluidContainer extends IEmptyContainer {
  /**
   * Gets the fluid contained in this inventory
   * @return  Contained fluid
   */
  Fluid getFluid();
}

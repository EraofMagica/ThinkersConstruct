package dev.tocraft.thconstruct.smeltery.block.entity.module;

import net.minecraftforge.fluids.capability.IFluidHandler;
import dev.tocraft.eomantle.block.entity.MantleBlockEntity;
import dev.tocraft.thconstruct.library.recipe.melting.IMeltingContainer.IOreRate;
import dev.tocraft.thconstruct.library.recipe.melting.IMeltingRecipe;

public class ByproductMeltingModuleInventory extends MeltingModuleInventory {
  public ByproductMeltingModuleInventory(MantleBlockEntity parent, IFluidHandler fluidHandler, IOreRate oreRate, int size) {
    super(parent, fluidHandler, oreRate, size);
  }

  public ByproductMeltingModuleInventory(MantleBlockEntity parent, IFluidHandler fluidHandler, IOreRate oreRate) {
    super(parent, fluidHandler, oreRate);
  }

  @Override
  protected boolean tryFillTank(int index, IMeltingRecipe recipe) {
    if (super.tryFillTank(index, recipe)) {
      recipe.handleByproducts(getModule(index), fluidHandler);
      return true;
    }
    return false;
  }
}

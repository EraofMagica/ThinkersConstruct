package dev.tocraft.thconstruct.library.tools.capability.fluid;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.ModifierTraitModule;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.stat.INumericToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ToolStatId;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

import java.util.function.BiFunction;

/** Helper methods for handling fluids in tools */
@SuppressWarnings("ClassCanBeRecord")  // want to leave extendable
@Getter
@RequiredArgsConstructor
public class ToolTankHelper {
  /** Helper function to parse a fluid from NBT */
  public static final BiFunction<CompoundTag, String, FluidStack> PARSE_FLUID = (nbt, key) -> FluidStack.loadFluidStackFromNBT(nbt.getCompound(key));

  /** Stat controlling the max for the default helper */
  public static final TankCapacityStat CAPACITY_STAT = ToolStats.register(new TankCapacityStat(new ToolStatId(dev.tocraft.thconstruct.ThConstruct.MOD_ID, "tank_capacity"), 0xA0A0A0, 0, Integer.MAX_VALUE));
  /** Default tank helper for setting fluids */
  public static final ToolTankHelper TANK_HELPER = new ToolTankHelper(CAPACITY_STAT, dev.tocraft.thconstruct.ThConstruct.getResource("tank_fluid"));
  /** Module ensuring the tool has the tank */
  public static final ModifierModule TANK_HANDLER = new ModifierTraitModule(TinkerModifiers.tankHandler.getId(), 1, true);

  /** Tool stat handling max tank capacity */
  private final INumericToolStat<?> capacityStat;
  /** Key in persistent data storing the fluid */
  private final ResourceLocation fluidKey;

  /** Gets the capacity for the tool */
  public int getCapacity(IToolStackView tool) {
    return tool.getStats().getInt(capacityStat);
  }


  /* Fluid */

  /** Gets the fluid in the tank */
  public FluidStack getFluid(IToolStackView tool) {
    return tool.getPersistentData().get(getFluidKey(), PARSE_FLUID);
  }

  /** Sets the fluid in the tank */
  public FluidStack setFluid(IToolStackView tool, FluidStack fluid) {
    if (fluid.isEmpty()) {
      tool.getPersistentData().remove(fluidKey);
      return fluid;
    }
    int capacity = getCapacity(tool);
    if (fluid.getAmount() > capacity) {
      fluid.setAmount(capacity);
    }
    tool.getPersistentData().put(fluidKey, fluid.writeToNBT(new CompoundTag()));
    return fluid;
  }
}

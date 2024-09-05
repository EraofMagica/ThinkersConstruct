package dev.tocraft.thconstruct.tables.block.entity.inventory;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.items.IItemHandlerModifiable;
import dev.tocraft.eomantle.block.entity.MantleBlockEntity;

/** Interface for tinker chest TEs */
public interface IChestItemHandler extends IItemHandlerModifiable, INBTSerializable<CompoundTag>, IScalingContainer {
  /** Sets the parent of this block */
  void setParent(MantleBlockEntity parent);
}

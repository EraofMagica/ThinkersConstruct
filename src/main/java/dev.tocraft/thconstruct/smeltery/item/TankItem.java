package dev.tocraft.thconstruct.smeltery.item;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import dev.tocraft.eomantle.client.SafeClientAccess;
import dev.tocraft.eomantle.client.TooltipKey;
import dev.tocraft.eomantle.fluid.tooltip.FluidTooltipHandler;
import dev.tocraft.eomantle.item.BlockTooltipItem;
import dev.tocraft.thconstruct.library.recipe.FluidValues;
import dev.tocraft.thconstruct.library.utils.NBTTags;
import dev.tocraft.thconstruct.smeltery.block.entity.component.TankBlockEntity;

import javax.annotation.Nullable;
import java.util.List;

public class TankItem extends BlockTooltipItem {
  private static final String KEY_FLUID = dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("block", "tank.fluid");
  private static final String KEY_MB = dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("block", "tank.mb");
  private static final String KEY_INGOTS = dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("block", "tank.ingots");
  private static final String KEY_MIXED = dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("block", "tank.mixed");

  private final boolean limitStackSize;
  public TankItem(Block blockIn, Properties builder, boolean limitStackSize) {
    super(blockIn, builder);
    this.limitStackSize = limitStackSize;
  }

  /** Checks if the tank item is filled */
  private static boolean isFilled(ItemStack stack) {
    // has a container if not empty
    CompoundTag nbt = stack.getTag();
    return nbt != null && nbt.contains(NBTTags.TANK, Tag.TAG_COMPOUND);
  }

  @Override
  public boolean hasCraftingRemainingItem(ItemStack stack) {
    return isFilled(stack);
  }

  @Override
  public ItemStack getCraftingRemainingItem(ItemStack stack) {
    return isFilled(stack) ? new ItemStack(this) : ItemStack.EMPTY;
  }

  @Override
  public int getMaxStackSize(ItemStack stack) {
    if (!limitStackSize) {
      return super.getMaxStackSize(stack);
    }
    return isFilled(stack) ? 16: 64;
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
    if (stack.hasTag()) {
      FluidTank tank = getFluidTank(stack);
      if (tank.getFluidAmount() > 0) {
        // TODO: migrate to a fluid tooltip JSON?
        tooltip.add(Component.translatable(KEY_FLUID, tank.getFluid().getDisplayName()).withStyle(ChatFormatting.GRAY));
        int amount = tank.getFluidAmount();
        TooltipKey key = SafeClientAccess.getTooltipKey();
        if (tank.getCapacity() % FluidValues.INGOT != 0 || key == TooltipKey.SHIFT) {
          tooltip.add(Component.translatable(KEY_MB, amount).withStyle(ChatFormatting.GRAY));
        } else {
          int ingots = amount / FluidValues.INGOT;
          int mb = amount % FluidValues.INGOT;
          if (mb == 0) {
            tooltip.add(Component.translatable(KEY_INGOTS, ingots).withStyle(ChatFormatting.GRAY));
          } else {
            tooltip.add(Component.translatable(KEY_MIXED, ingots, mb).withStyle(ChatFormatting.GRAY));
          }
          if (key != TooltipKey.UNKNOWN) {
            tooltip.add(FluidTooltipHandler.HOLD_SHIFT);
          }
        }

      }
    }
    else {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
    }
  }

  @Nullable
  @Override
  public ICapabilityProvider initCapabilities(ItemStack stack, @Nullable CompoundTag nbt) {
    return new TankItemFluidHandler(stack);
  }

  /**
   * Sets the tank to the given stack
   * @param stack  Stack
   * @param tank   Tank instance
   * @return  Stack with tank
   */
  public static ItemStack setTank(ItemStack stack, FluidTank tank) {
    if (tank.isEmpty()) {
      CompoundTag nbt = stack.getTag();
      if (nbt != null) {
        nbt.remove(NBTTags.TANK);
        if (nbt.isEmpty()) {
          stack.setTag(null);
        }
      }
    } else {
      stack.getOrCreateTag().put(NBTTags.TANK, tank.writeToNBT(new CompoundTag()));
    }
    return stack;
  }

  /**
   * Gets the tank for the given stack
   * @param stack  Tank stack
   * @return  Tank stored in the stack
   */
  public static FluidTank getFluidTank(ItemStack stack) {
    FluidTank tank = new FluidTank(TankBlockEntity.getCapacity(stack.getItem()));
    if (stack.hasTag()) {
      assert stack.getTag() != null;
      tank.readFromNBT(stack.getTag().getCompound(NBTTags.TANK));
    }
    return tank;
  }
}

package dev.tocraft.thconstruct.tools.item;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import dev.tocraft.thconstruct.common.config.Config;
import dev.tocraft.thconstruct.library.materials.MaterialRegistry;
import dev.tocraft.thconstruct.library.materials.definition.IMaterial;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariantId;
import dev.tocraft.thconstruct.library.tools.helper.TooltipUtil;
import dev.tocraft.thconstruct.library.tools.part.IRepairKitItem;
import dev.tocraft.thconstruct.library.tools.part.MaterialItem;
import dev.tocraft.thconstruct.library.tools.part.ToolPartItem;
import dev.tocraft.thconstruct.tools.stats.StatlessMaterialStats;

import javax.annotation.Nullable;
import java.util.List;

public class RepairKitItem extends MaterialItem implements IRepairKitItem {
  public RepairKitItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canUseMaterial(MaterialId material) {
    return MaterialRegistry.getInstance()
                           .getAllStats(material)
                           .stream()
                           .anyMatch(stats -> stats == StatlessMaterialStats.REPAIR_KIT || stats.getType().canRepair());
  }

  @Override
  public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flag) {
    if (flag.isAdvanced() && !TooltipUtil.isDisplay(stack)) {
      MaterialVariantId materialVariant = this.getMaterial(stack);
      if (!materialVariant.equals(IMaterial.UNKNOWN_ID)) {
        tooltip.add((Component.translatable(ToolPartItem.MATERIAL_KEY, materialVariant.toString())).withStyle(ChatFormatting.DARK_GRAY));
      }
    }
  }

  @Override
  public float getRepairAmount() {
    return Config.COMMON.repairKitAmount.get().floatValue();
  }
}

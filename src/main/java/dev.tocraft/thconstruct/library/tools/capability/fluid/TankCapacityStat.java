package dev.tocraft.thconstruct.library.tools.capability.fluid;

import net.minecraft.network.chat.Component;
import dev.tocraft.eomantle.Mantle;
import dev.tocraft.thconstruct.library.tools.stat.FloatToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ToolStatId;
import dev.tocraft.thconstruct.library.utils.Util;

/** Tool stat formatting as millibuckets */
public class TankCapacityStat extends FloatToolStat {
  public static final String MB_FORMAT = Mantle.makeDescriptionId("gui", "fluid.millibucket");
  public TankCapacityStat(ToolStatId name, int color, float defaultValue, float maxValue) {
    super(name, color, defaultValue, 0, maxValue);
  }

  @Override
  public Component formatValue(float value) {
    return Component.translatable(getTranslationKey())
                    .append(Component.translatable(MB_FORMAT, Util.COMMA_FORMAT.format(value))
                                     .withStyle(style -> style.withColor(getColor())));
  }
}

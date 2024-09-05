package dev.tocraft.thconstruct.tools.stats;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import net.minecraft.network.chat.Component;
import dev.tocraft.eomantle.data.loadable.primitive.FloatLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.materials.stats.IMaterialStats;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatType;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.tools.stat.IToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ModifierStatsBuilder;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

import java.util.List;

import static dev.tocraft.thconstruct.tools.stats.LimbMaterialStats.ACCURACY_PREFIX;

/** Secondary stats for a bow */
public record GripMaterialStats(float durability, float accuracy, float meleeDamage) implements IMaterialStats {
  public static final MaterialStatsId ID = new MaterialStatsId(dev.tocraft.thconstruct.ThConstruct.getResource("grip"));
  public static final MaterialStatType<GripMaterialStats> TYPE = new MaterialStatType<>(ID, new GripMaterialStats(0f, 0f, 0f), RecordLoadable.create(
    FloatLoadable.ANY.defaultField("durability", 0f, true, GripMaterialStats::durability),
    FloatLoadable.ANY.defaultField("accuracy", 0f, true, GripMaterialStats::accuracy),
    FloatLoadable.FROM_ZERO.defaultField("melee_damage", 0f, true, GripMaterialStats::meleeDamage),
    GripMaterialStats::new));

  // tooltip prefixes
  private static final String DURABILITY_PREFIX = IMaterialStats.makeTooltipKey(dev.tocraft.thconstruct.ThConstruct.getResource("durability"));
  // description
  private static final List<Component> DESCRIPTION = ImmutableList.of(
    IMaterialStats.makeTooltip(dev.tocraft.thconstruct.ThConstruct.getResource("handle.durability.description")),
    ToolStats.ACCURACY.getDescription(),
    ToolStats.ATTACK_DAMAGE.getDescription());

  @Override
  public MaterialStatType<?> getType() {
    return TYPE;
  }

  @Override
  public List<Component> getLocalizedInfo() {
    List<Component> info = Lists.newArrayList();
    info.add(IToolStat.formatColoredPercentBoost(DURABILITY_PREFIX, this.durability));
    info.add(IToolStat.formatColoredBonus(ACCURACY_PREFIX, this.accuracy));
    info.add(ToolStats.ATTACK_DAMAGE.formatValue(this.meleeDamage));
    return info;
  }

  @Override
  public List<Component> getLocalizedDescriptions() {
    return DESCRIPTION;
  }

  @Override
  public void apply(ModifierStatsBuilder builder, float scale) {
    ToolStats.DURABILITY.percent(builder, durability * scale);
    ToolStats.ACCURACY.add(builder, accuracy * scale);
    ToolStats.ATTACK_DAMAGE.update(builder, meleeDamage * scale);
  }
}

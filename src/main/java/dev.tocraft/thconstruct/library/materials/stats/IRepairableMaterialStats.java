package dev.tocraft.thconstruct.library.materials.stats;

import dev.tocraft.eomantle.data.loadable.field.LoadableField;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;

/**
 * Material stats that support repairing, requires durability as part of the stats
 */
public interface IRepairableMaterialStats extends IMaterialStats {
  LoadableField<Integer,IRepairableMaterialStats> DURABILITY_FIELD = IntLoadable.FROM_ONE.requiredField("durability", IRepairableMaterialStats::durability);

  /**
   * Gets the amount of durability for this stat type
   * @return  Durability
   */
  int durability();
}

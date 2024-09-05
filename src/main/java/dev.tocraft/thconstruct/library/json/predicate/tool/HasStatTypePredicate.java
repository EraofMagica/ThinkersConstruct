package dev.tocraft.thconstruct.library.json.predicate.tool;

import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariantId;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.tools.definition.module.material.ToolMaterialHook;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Checks if the tool has the given stat type.
 * @param statType    Stat type to locate.
 * @param material    If non-null, requires the given material in that stat type. If null, materials are ignored.
 */
public record HasStatTypePredicate(MaterialStatsId statType, @Nullable MaterialVariantId material) implements ToolContextPredicate {
  public static final RecordLoadable<HasStatTypePredicate> LOADER = RecordLoadable.create(
    MaterialStatsId.PARSER.requiredField("stat_type", HasStatTypePredicate::statType),
    MaterialVariantId.LOADABLE.nullableField("material", HasStatTypePredicate::material),
    HasStatTypePredicate::new);

  public HasStatTypePredicate(MaterialStatsId statType) {
    this(statType, null);
  }

  @Override
  public boolean matches(IToolContext tool) {
    List<MaterialStatsId> parts = ToolMaterialHook.stats(tool.getDefinition());
    for (int i = 0; i < parts.size(); i++) {
      if (statType.equals(parts.get(i)) && (material == null || material.matchesVariant(tool.getMaterial(i)))) {
        return true;
      }
    }
    return false;
  }

  @Override
  public RecordLoadable<HasStatTypePredicate> getLoader() {
    return LOADER;
  }
}

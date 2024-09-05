package dev.tocraft.thconstruct.library.json.predicate.tool;

import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariant;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariantId;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;

/**
 * Tool predicate checking for the given material on the tool
 * @param material   Material variant to locate.
 * @param index      Index to check for the material. If -1, will check all materials on the tool.
 */
public record HasMaterialPredicate(MaterialVariantId material, int index) implements ToolContextPredicate {
  public static final RecordLoadable<HasMaterialPredicate> LOADER = RecordLoadable.create(
    MaterialVariantId.LOADABLE.requiredField("material", HasMaterialPredicate::material),
    IntLoadable.FROM_MINUS_ONE.defaultField("index", -1, HasMaterialPredicate::index),
    HasMaterialPredicate::new);

  public HasMaterialPredicate(MaterialVariantId material) {
    this(material, -1);
  }

  @Override
  public boolean matches(IToolContext input) {
    // if given an index, use exact location match
    if (index >= 0) {
      return material.matchesVariant(input.getMaterial(index));
    }
    // otherwise, search each material
    for (MaterialVariant variant : input.getMaterials().getList()) {
      if (material.matchesVariant(variant)) {
        return true;
      }
    }
    return false;
  }

  @Override
  public IGenericLoader<? extends ToolContextPredicate> getLoader() {
    return LOADER;
  }
}

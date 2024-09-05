package dev.tocraft.thconstruct.library.materials.stats;

import net.minecraft.resources.ResourceLocation;
import dev.tocraft.thconstruct.library.materials.MaterialRegistry;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.utils.IdParser;

import javax.annotation.Nullable;

/**
 * This is just a copy of ResourceLocation for type safety.
 */
public class MaterialStatsId extends ResourceLocation {
  public static final IdParser<MaterialStatsId> PARSER = new IdParser<>(MaterialStatsId::new, "Material Stat Type");

  public MaterialStatsId(String text) {
    super(text);
  }

  public MaterialStatsId(String namespaceIn, String pathIn) {
    super(namespaceIn, pathIn);
  }

  public MaterialStatsId(ResourceLocation resourceLocation) {
    super(resourceLocation.getNamespace(), resourceLocation.getPath());
  }

  /**
   * Creates a new material stat ID from the given string
   * @param string  String
   * @return  Material ID, or null if invalid
   */
  @Nullable
  public static MaterialStatsId tryParse(String string) {
    return PARSER.tryParse(string);
  }

  /** Checks if the given material can be used */
  public boolean canUseMaterial(MaterialId material) {
    return MaterialRegistry.getInstance().getMaterialStats(material.getId(), this).isPresent();
  }
}

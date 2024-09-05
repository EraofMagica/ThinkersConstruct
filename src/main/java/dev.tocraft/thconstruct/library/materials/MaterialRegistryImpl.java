package dev.tocraft.thconstruct.library.materials;

import net.minecraft.tags.TagKey;
import dev.tocraft.eomantle.data.loadable.Loadable;
import dev.tocraft.thconstruct.library.materials.definition.IMaterial;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.materials.definition.MaterialManager;
import dev.tocraft.thconstruct.library.materials.stats.IMaterialStats;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatType;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsManager;
import dev.tocraft.thconstruct.library.materials.traits.MaterialTraitsManager;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Holds all materials and the extra information registered for them (stat classes).
 * Materials are reset on every world load/join. Registered extra stuff is not.
 * <p>
 * For the Server, materials are loaded on server start/reload from the data packs.
 * For the Client, materials are synced from the server on server join.
 */
public class MaterialRegistryImpl implements IMaterialRegistry {
  private final MaterialManager materialManager;
  private final MaterialStatsManager materialStatsManager;
  private final MaterialTraitsManager materialTraitsManager;

  protected MaterialRegistryImpl(MaterialManager materialManager, MaterialStatsManager materialStatsManager, MaterialTraitsManager materialTraitsManager) {
    this.materialManager = materialManager;
    this.materialStatsManager = materialStatsManager;
    this.materialTraitsManager = materialTraitsManager;
  }


  /* Materials */

  @Override
  public MaterialId resolve(MaterialId id) {
    return materialManager.resolveRedirect(id);
  }

  @Override
  public IMaterial getMaterial(MaterialId id) {
    return materialManager.getMaterial(id).orElse(IMaterial.UNKNOWN);
  }

  @Override
  public Collection<IMaterial> getVisibleMaterials() {
    return materialManager.getVisibleMaterials();
  }

  @Override
  public Collection<IMaterial> getAllMaterials() {
    return materialManager.getAllMaterials();
  }


  /* Tags */

  @Override
  public boolean isInTag(MaterialId id, TagKey<IMaterial> tag) {
    return materialManager.isIn(id, tag);
  }

  @Override
  public List<IMaterial> getTagValues(TagKey<IMaterial> tag) {
    return materialManager.getValues(tag);
  }


  /* Stats */

  /** Gets the loader for all stat types */
  @Override
  public Loadable<MaterialStatType<?>> getStatTypeLoader() {
    return materialStatsManager.getStatTypes();
  }

  @Nullable
  @Override
  public <T extends IMaterialStats> MaterialStatType<T> getStatType(MaterialStatsId statsId) {
    return materialStatsManager.getStatType(statsId);
  }

  @Override
  public <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId) {
    return materialStatsManager.getStats(materialId, statsId);
  }

  @Override
  public Collection<IMaterialStats> getAllStats(MaterialId materialId) {
    return materialStatsManager.getAllStats(materialId);
  }

  @Override
  public void registerStatType(MaterialStatType<?> type) {
    materialStatsManager.registerStatType(type);
  }

  @Override
  public void registerStatType(MaterialStatType<?> type, @Nullable MaterialStatsId fallback) {
    registerStatType(type);
    if (fallback != null) {
      materialTraitsManager.registerStatTypeFallback(type.getId(), fallback);
    }
  }


  /* Traits */

  @Override
  public List<ModifierEntry> getDefaultTraits(MaterialId materialId) {
    return materialTraitsManager.getDefaultTraits(materialId);
  }

  @Override
  public boolean hasUniqueTraits(MaterialId materialId, MaterialStatsId statsId) {
    return materialTraitsManager.hasUniqueTraits(materialId, statsId);
  }

  @Override
  public List<ModifierEntry> getTraits(MaterialId materialId, MaterialStatsId statsId) {
    return materialTraitsManager.getTraits(materialId, statsId);
  }
}

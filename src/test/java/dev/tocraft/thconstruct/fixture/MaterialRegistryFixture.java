package dev.tocraft.thconstruct.fixture;

import lombok.AllArgsConstructor;
import net.minecraft.tags.TagKey;
import dev.tocraft.eomantle.data.loadable.Loadable;
import dev.tocraft.thconstruct.library.materials.IMaterialRegistry;
import dev.tocraft.thconstruct.library.materials.definition.IMaterial;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.materials.stats.IMaterialStats;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatType;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.materials.traits.MaterialTraits;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@AllArgsConstructor
public class MaterialRegistryFixture implements IMaterialRegistry {
  private final Map<MaterialId, IMaterial> materials;
  private final Map<MaterialId, Map<MaterialStatsId, IMaterialStats>> stats;
  private final Map<MaterialStatsId, IMaterialStats> defaultStats;
  private final Map<MaterialId,MaterialTraits> traits;


  /* Materials */

  @Override
  public IMaterial getMaterial(MaterialId id) {
    return materials.computeIfAbsent(id,
      materialId -> {throw new IllegalArgumentException("No material with id " + materialId + " set up");});
  }

  @Override
  public Collection<IMaterial> getVisibleMaterials() {
    return materials.values().stream().filter(mat -> !mat.isHidden()).collect(Collectors.toList());
  }

  @Override
  public Collection<IMaterial> getAllMaterials() {
    return materials.values();
  }


  /* Tags */

  @Override
  public boolean isInTag(MaterialId id, TagKey<IMaterial> tag) {
    return false;
  }

  @Override
  public List<IMaterial> getTagValues(TagKey<IMaterial> tag) {
    return Collections.emptyList();
  }


  /* Stats */

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> Optional<T> getMaterialStats(MaterialId materialId, MaterialStatsId statsId) {
    return Optional.ofNullable((T)stats.getOrDefault(materialId, Collections.emptyMap()).get(statsId));
  }

  @Override
  public Collection<IMaterialStats> getAllStats(MaterialId materialId) {
    return stats.getOrDefault(materialId, Collections.emptyMap()).values();
  }

  @Override
  public Loadable<MaterialStatType<?>> getStatTypeLoader() {
    throw new UnsupportedOperationException("No registration possible in test mock");
  }

  @Nullable
  @Override
  public <T extends IMaterialStats> MaterialStatType<T> getStatType(MaterialStatsId statsId) {
    return null;
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends IMaterialStats> T getDefaultStats(MaterialStatsId statsId) {
    return Objects.requireNonNull((T)defaultStats.get(statsId));
  }

  @Override
  public void registerStatType(MaterialStatType<?> type) {
    throw new UnsupportedOperationException("No registration possible in test mock");
  }


  /* Traits */

  @Override
  public List<ModifierEntry> getDefaultTraits(MaterialId materialId) {
    MaterialTraits traits = this.traits.get(materialId);
    return traits == null ? Collections.emptyList() : traits.getDefaultTraits();
  }

  @Override
  public boolean hasUniqueTraits(MaterialId materialId, MaterialStatsId statsId) {
    MaterialTraits traits = this.traits.get(materialId);
    return traits != null && traits.hasUniqueTraits(statsId);
  }

  @Override
  public List<ModifierEntry> getTraits(MaterialId materialId, MaterialStatsId statsId) {
    MaterialTraits traits = this.traits.get(materialId);
    return traits == null ? Collections.emptyList() : traits.getTraits(statsId);
  }
}

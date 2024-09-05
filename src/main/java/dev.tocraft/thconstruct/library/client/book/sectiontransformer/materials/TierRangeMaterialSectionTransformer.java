package dev.tocraft.thconstruct.library.client.book.sectiontransformer.materials;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.util.GsonHelper;
import dev.tocraft.eomantle.client.book.data.BookData;
import dev.tocraft.eomantle.client.book.data.SectionData;
import dev.tocraft.eomantle.client.book.transformer.BookTransformer;
import dev.tocraft.eomantle.util.JsonHelper;
import dev.tocraft.thconstruct.library.client.book.content.AbstractMaterialContent;
import dev.tocraft.thconstruct.library.json.IntRange;
import dev.tocraft.thconstruct.library.json.TinkerLoadables;
import dev.tocraft.thconstruct.library.materials.IMaterialRegistry;
import dev.tocraft.thconstruct.library.materials.MaterialRegistry;
import dev.tocraft.thconstruct.library.materials.definition.IMaterial;
import dev.tocraft.thconstruct.library.materials.definition.MaterialId;
import dev.tocraft.thconstruct.library.materials.definition.MaterialVariantId;
import dev.tocraft.thconstruct.library.materials.stats.IMaterialStats;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Section transformer to show a range of materials tiers in the book
 */
public class TierRangeMaterialSectionTransformer extends BookTransformer {
  private static final ResourceLocation KEY = dev.tocraft.thconstruct.ThConstruct.getResource("material_tier");
  private static final IntRange TIER = new IntRange(0, Short.MAX_VALUE);

  private static final Map<ResourceLocation,MaterialType> MATERIAL_TYPES = new HashMap<>();

  public static final TierRangeMaterialSectionTransformer INSTANCE = new TierRangeMaterialSectionTransformer();

  /** Registers a new group of stat types to show on a page */
  public static void registerMaterialType(ResourceLocation id, BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> constructor, MaterialStatsId... stats) {
    if (MATERIAL_TYPES.putIfAbsent(id, new MaterialType(constructor, ImmutableSet.copyOf(stats))) != null) {
      throw new IllegalArgumentException("Duplicate material stat group " + id);
    }
  }

  @Override
  public void transform(BookData book) {
    for (SectionData section : book.sections) {
      JsonElement element = section.extraData.get(KEY);
      if (element != null) {
        try {
          JsonObject json = GsonHelper.convertToJsonObject(element, KEY.toString());
          IntRange tier = TIER.getOrDefault(json, "tier");
          Function<MaterialVariantId,AbstractMaterialContent> pageBuilder;
          Set<MaterialStatsId> visibleStats;
          TagKey<IMaterial> tag = TinkerLoadables.MATERIAL_TAGS.getOrDefault(json, "tag", null);
          ResourceLocation type = JsonHelper.getResourceLocation(json, "type");
          MaterialType typeData = MATERIAL_TYPES.get(type);
          if (typeData == null) {
            throw new JsonSyntaxException("Invalid material section type " + type);
          }
          visibleStats = typeData.visibleStats();
          pageBuilder = typeData.getMapping(GsonHelper.getAsBoolean(json, "detailed", false));
          AbstractMaterialSectionTransformer.createPages(book, section, new ValidMaterial(visibleStats, tier, tag), pageBuilder);
        } catch (JsonSyntaxException e) {
          dev.tocraft.thconstruct.ThConstruct.LOG.error("Failed to parse material tier section data", e);
        }
      }
    }
  }

  /** Helper to create a material predicate */
  public record ValidMaterial(Set<MaterialStatsId> visibleStats, IntRange tier, @Nullable TagKey<IMaterial> tag) implements Predicate<IMaterial> {
    @Override
    public boolean test(IMaterial material) {
      if (!this.tier.test(material.getTier())) {
        return false;
      }
      IMaterialRegistry registry = MaterialRegistry.getInstance();
      MaterialId id = material.getIdentifier();
      if (tag != null && !registry.isInTag(id, tag)) {
        return false;
      }
      // only show material stats for types with the proper stat types, as otherwise the page will be empty
      // if you want to show another stat type, just override this method/implement the parent class
      for (IMaterialStats stats : registry.getAllStats(id)) {
        if (visibleStats.contains(stats.getIdentifier())) {
          return true;
        }
      }
      return false;
    }
  }

  /** Internal record from the registry */
  private record MaterialType(BiFunction<MaterialVariantId,Boolean,AbstractMaterialContent> pageConstructor, Set<MaterialStatsId> visibleStats) {
    public Function<MaterialVariantId,AbstractMaterialContent> getMapping(boolean detailed) {
      return id -> pageConstructor.apply(id, detailed);
    }
  }
}

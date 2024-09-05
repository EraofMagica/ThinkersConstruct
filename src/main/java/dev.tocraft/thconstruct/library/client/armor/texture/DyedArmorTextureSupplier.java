package dev.tocraft.thconstruct.library.client.armor.texture;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.common.ColorLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.util.IdExtender.LocationExtender;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.tools.helper.ModifierUtil;

import javax.annotation.Nullable;
import java.util.Objects;

/**
 * Armor texture supplier that supplies a fixed texture that is colored using the given persistent data key
 */
public class DyedArmorTextureSupplier implements ArmorTextureSupplier {
  public static final RecordLoadable<DyedArmorTextureSupplier> LOADER = RecordLoadable.create(
    Loadables.RESOURCE_LOCATION.requiredField("prefix", s -> s.prefix),
    ModifierId.PARSER.requiredField("modifier", s -> s.modifier),
    ColorLoadable.NO_ALPHA.nullableField("default_color", s -> s.alwaysRender ? s.defaultColor : null),
    DyedArmorTextureSupplier::new);

  private final ResourceLocation prefix;
  private final ModifierId modifier;
  private final boolean alwaysRender;
  private final int defaultColor;
  private final String[] textures;

  public DyedArmorTextureSupplier(ResourceLocation prefix, ModifierId modifier, @Nullable Integer defaultColor) {
    this.prefix = prefix;
    this.modifier = modifier;
    this.alwaysRender = defaultColor != null;
    this.defaultColor = Objects.requireNonNullElse(defaultColor, -1);
    this.textures = new String[] {
      getTexture(prefix, "armor"),
      getTexture(prefix, "leggings"),
      getTexture(prefix, "wings"),
    };
  }

  public DyedArmorTextureSupplier(ResourceLocation base, String variant, ModifierId modifier, @Nullable Integer defaultColor) {
    this(LocationExtender.INSTANCE.suffix(base, variant), modifier, defaultColor);
  }

  /** Gets a texture if it exists, empty otherwise */
  public static String getTexture(ResourceLocation base, String variant) {
    ResourceLocation name = LocationExtender.INSTANCE.suffix(base, variant);
    if (TEXTURE_VALIDATOR.test(name)) {
      return ArmorTextureSupplier.getTexturePath(name);
    }
    return "";
  }

  @Override
  public ArmorTexture getArmorTexture(ItemStack stack, TextureType textureType) {
    String texture = textures[textureType.ordinal()];
    if (!texture.isEmpty() && (alwaysRender || ModifierUtil.getModifierLevel(stack, modifier) > 0)) {
      int color = ModifierUtil.getPersistentInt(stack, modifier, defaultColor);
      return new ArmorTexture(textures[textureType.ordinal()], 0xFF000000 | color);
    }
    return ArmorTexture.EMPTY;
  }

  @Override
  public RecordLoadable<DyedArmorTextureSupplier> getLoader() {
    return LOADER;
  }
}

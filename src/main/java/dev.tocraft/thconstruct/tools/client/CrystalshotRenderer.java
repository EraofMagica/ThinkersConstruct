package dev.tocraft.thconstruct.tools.client;

import net.minecraft.client.renderer.entity.ArrowRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.resources.ResourceLocation;
import dev.tocraft.thconstruct.tools.item.CrystalshotItem.CrystalshotEntity;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class CrystalshotRenderer extends ArrowRenderer<CrystalshotEntity> {
  private static final Map<String,ResourceLocation> TEXTURES = new HashMap<>();
  private static final Function<String,ResourceLocation> TEXTURE_GETTER = variant -> dev.tocraft.thconstruct.ThConstruct.getResource("textures/entity/arrow/" + variant + ".png");
  public CrystalshotRenderer(Context context) {
    super(context);
  }

  @Override
  public ResourceLocation getTextureLocation(CrystalshotEntity arrow) {
    return TEXTURES.computeIfAbsent(arrow.getVariant(), TEXTURE_GETTER);
  }
}

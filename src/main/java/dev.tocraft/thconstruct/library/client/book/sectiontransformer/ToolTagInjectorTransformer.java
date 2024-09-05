package dev.tocraft.thconstruct.library.client.book.sectiontransformer;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import dev.tocraft.eomantle.client.book.data.content.PageContent;
import dev.tocraft.thconstruct.library.client.book.content.ContentTool;

/** Injects tools into a section based on a tag */
public class ToolTagInjectorTransformer extends AbstractTagInjectingTransformer<Item> {
  public static final ToolTagInjectorTransformer INSTANCE = new ToolTagInjectorTransformer();

  private ToolTagInjectorTransformer() {
    super(Registry.ITEM_REGISTRY, dev.tocraft.thconstruct.ThConstruct.getResource("load_tools"), ContentTool.ID);
  }

  @Override
  protected ResourceLocation getId(Item item) {
    return Registry.ITEM.getKey(item);
  }

  @Override
  protected PageContent createFallback(Item item) {
    return new ContentTool(item);
  }
}

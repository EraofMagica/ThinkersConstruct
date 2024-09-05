package dev.tocraft.thconstruct.library.client.book.sectiontransformer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import dev.tocraft.eomantle.client.book.data.content.PageContent;
import dev.tocraft.thconstruct.library.client.book.content.ContentModifier;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierManager;

import java.util.Iterator;

/** Injects modifiers into a section based on a tag */
public class ModifierTagInjectorTransformer extends AbstractTagInjectingTransformer<Modifier> {
  public static final ModifierTagInjectorTransformer INSTANCE = new ModifierTagInjectorTransformer();

  private ModifierTagInjectorTransformer() {
    super(ModifierManager.REGISTRY_KEY, dev.tocraft.thconstruct.ThConstruct.getResource("load_modifiers"), ContentModifier.ID);
  }

  @Override
  protected Iterator<Modifier> getTagEntries(TagKey<Modifier> tag) {
    return ModifierManager.getTagValues(tag).iterator();
  }

  @Override
  protected ResourceLocation getId(Modifier modifier) {
    return modifier.getId();
  }

  @Override
  protected PageContent createFallback(Modifier modifier) {
    return new ContentModifier(modifier);
  }
}

package dev.tocraft.thconstruct.library.json.predicate.modifier;

import net.minecraft.tags.TagKey;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.json.TinkerLoadables;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.modifiers.ModifierManager;

/**
 * Predicate matching an entity tag
 */
public record TagModifierPredicate(TagKey<Modifier> tag) implements ModifierPredicate {
  public static final RecordLoadable<TagModifierPredicate> LOADER = RecordLoadable.create(TinkerLoadables.MODIFIER_TAGS.requiredField("tag", TagModifierPredicate::tag), TagModifierPredicate::new);

  @Override
  public boolean matches(ModifierId modifier) {
    return ModifierManager.isInTag(modifier, tag);
  }

  @Override
  public IGenericLoader<? extends ModifierPredicate> getLoader() {
    return LOADER;
  }
}

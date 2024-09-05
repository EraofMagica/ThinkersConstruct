package dev.tocraft.thconstruct.library.json.predicate.modifier;

import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;

/** Predicate matching a single modifier */
public record SingleModifierPredicate(ModifierId modifier) implements ModifierPredicate {
  public static final RecordLoadable<SingleModifierPredicate> LOADER = RecordLoadable.create(ModifierId.PARSER.requiredField("modifier", SingleModifierPredicate::modifier), SingleModifierPredicate::new);

  @Override
  public boolean matches(ModifierId input) {
    return input.equals(modifier);
  }

  @Override
  public IGenericLoader<? extends ModifierPredicate> getLoader() {
    return LOADER;
  }
}

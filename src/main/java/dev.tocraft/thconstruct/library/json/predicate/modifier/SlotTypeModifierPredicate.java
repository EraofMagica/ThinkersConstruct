package dev.tocraft.thconstruct.library.json.predicate.modifier;

import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.recipe.modifiers.ModifierRecipeLookup;
import dev.tocraft.thconstruct.library.tools.SlotType;

import javax.annotation.Nullable;

/** Predicate that matches any modifiers with recipes requiring a slot */
public record SlotTypeModifierPredicate(@Nullable SlotType slotType) implements ModifierPredicate {
  public static final RecordLoadable<SlotTypeModifierPredicate> LOADER = RecordLoadable.create(SlotType.LOADABLE.nullableField("slot", SlotTypeModifierPredicate::slotType), SlotTypeModifierPredicate::new);

  @Override
  public boolean matches(ModifierId input) {
    return ModifierRecipeLookup.isRecipeModifier(slotType, input);
  }

  @Override
  public IGenericLoader<SlotTypeModifierPredicate> getLoader() {
    return LOADER;
  }
}

package dev.tocraft.thconstruct.library.tools.definition.module.interaction;

import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.thconstruct.library.json.TinkerLoadables;
import dev.tocraft.thconstruct.library.json.predicate.modifier.ModifierPredicate;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.modifiers.hook.interaction.InteractionSource;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import java.util.List;

/**
 * Interaction that makes only a limited set work in the preferred hand, the rest working in the other hand
 */
public record PreferenceSetInteraction(InteractionSource preferredSource, IJsonPredicate<ModifierId> preferenceModifiers) implements InteractionToolModule, ToolModule {
  public static final RecordLoadable<PreferenceSetInteraction> LOADER = RecordLoadable.create(
    TinkerLoadables.INTERACTION_SOURCE.requiredField("preferred_source", PreferenceSetInteraction::preferredSource),
    ModifierPredicate.LOADER.requiredField("preferred_modifiers", PreferenceSetInteraction::preferenceModifiers),
    PreferenceSetInteraction::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<PreferenceSetInteraction>defaultHooks(ToolHooks.INTERACTION);

  @Override
  public boolean canInteract(IToolStackView tool, ModifierId modifier, InteractionSource source) {
    return (source == preferredSource) == preferenceModifiers.matches(modifier);
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<PreferenceSetInteraction> getLoader() {
    return LOADER;
  }
}

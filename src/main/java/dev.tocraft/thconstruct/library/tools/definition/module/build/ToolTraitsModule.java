package dev.tocraft.thconstruct.library.tools.definition.module.build;

import com.google.common.collect.ImmutableList;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierId;
import dev.tocraft.thconstruct.library.modifiers.util.LazyModifier;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.helper.ModifierBuilder;
import dev.tocraft.thconstruct.library.tools.nbt.MaterialNBT;

import java.util.List;

/** Module for adding traits to a tool */
public record ToolTraitsModule(List<ModifierEntry> traits) implements ToolTraitHook, ToolModule {
  public static final RecordLoadable<ToolTraitsModule> LOADER = RecordLoadable.create(ModifierEntry.LOADABLE.list(1).requiredField("traits", ToolTraitsModule::traits), ToolTraitsModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolTraitsModule>defaultHooks(ToolHooks.TOOL_TRAITS);

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public RecordLoadable<ToolTraitsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addTraits(ToolDefinition definition, MaterialNBT materials, ModifierBuilder builder) {
    builder.add(traits);
  }

  public static class Builder {
    private final ImmutableList.Builder<ModifierEntry> traits = ImmutableList.builder();

    private Builder() {}

    /** Adds a base trait to the tool */
    public Builder trait(ModifierId modifier, int level) {
      traits.add(new ModifierEntry(modifier, level));
      return this;
    }

    /** Adds a base trait to the tool */
    public Builder trait(LazyModifier modifier, int level) {
      return trait(modifier.getId(), level);
    }

    /** Adds a base trait to the tool */
    public Builder trait(ModifierId modifier) {
      return trait(modifier, 1);
    }

    /** Adds a base trait to the tool */
    public Builder trait(LazyModifier modifier) {
      return trait(modifier, 1);
    }

    /** Makes a copy of this builder */
    public Builder copy() {
      Builder copy = new Builder();
      copy.traits.addAll(this.traits.build());
      return copy;
    }

    /** Builds the final module */
    public ToolTraitsModule build() {
      List<ModifierEntry> traits = this.traits.build();
      if (traits.isEmpty()) {
        throw new IllegalStateException("Must have at least 1 trait");
      }
      return new ToolTraitsModule(traits);
    }
  }
}

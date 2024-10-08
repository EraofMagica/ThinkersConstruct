package dev.tocraft.thconstruct.library.tools.definition.module.build;

import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.helper.ModifierBuilder;
import dev.tocraft.thconstruct.library.tools.nbt.MaterialNBT;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;

import java.util.Collection;

/** Hook for tools exposing tool traits */
public interface ToolTraitHook {
  /** Adds all traits to the given builder */
  void addTraits(ToolDefinition definition, MaterialNBT materials, ModifierBuilder builder);

  /** Gets the traits from the given definition */
  static ModifierNBT getTraits(ToolDefinition definition, MaterialNBT materials) {
    ModifierNBT.Builder builder = ModifierNBT.builder();
    definition.getHook(ToolHooks.TOOL_TRAITS).addTraits(definition, materials, builder);
    return builder.build();
  }

  /** Gets the traits for the given tool */
  record AllMerger(Collection<ToolTraitHook> hooks) implements ToolTraitHook {
    @Override
    public void addTraits(ToolDefinition definition, MaterialNBT materials, ModifierBuilder builder) {
      for (ToolTraitHook hook : hooks) {
        hook.addTraits(definition, materials, builder);
      }
    }
  }
}

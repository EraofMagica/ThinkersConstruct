package dev.tocraft.thconstruct.library.tools.definition.module.material;

import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.json.TinkerLoadables;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.part.IToolPart;

import java.util.List;

/** Module to directly add parts to a tool without using part stats, mainly used to allow a module to have some parts and some fixed stat types. */
public record PartsModule(List<IToolPart> parts) implements ToolPartsHook, ToolModule {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<PartsModule>defaultHooks(ToolHooks.TOOL_PARTS);
  public static final RecordLoadable<PartsModule> LOADER = RecordLoadable.create(TinkerLoadables.TOOL_PART_ITEM.list(1).requiredField("parts", m -> m.parts), PartsModule::new);

  @Override
  public List<IToolPart> getParts(ToolDefinition definition) {
    return parts;
  }

  @Override
  public RecordLoadable<PartsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }
}

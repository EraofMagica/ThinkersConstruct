package dev.tocraft.thconstruct.library.tools.definition.module.material;

import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.part.IToolPart;

import java.util.List;

/** Hook to get parts from a tool */
public interface ToolPartsHook {
  /** Gets the list of parts on this tool */
  List<IToolPart> getParts(ToolDefinition definition);

  /** Gets the tool parts from the given definition */
  static List<IToolPart> parts(ToolDefinition definition) {
    return definition.getHook(ToolHooks.TOOL_PARTS).getParts(definition);
  }
}

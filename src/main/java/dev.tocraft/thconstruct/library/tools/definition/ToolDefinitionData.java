package dev.tocraft.thconstruct.library.tools.definition;

import lombok.Getter;
import net.minecraft.network.FriendlyByteBuf;
import dev.tocraft.eomantle.data.loadable.ErrorFactory;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap;
import dev.tocraft.thconstruct.library.module.WithHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.nbt.MultiplierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.StatsNBT;

import java.util.List;

/**
 * This class contains all data pack configurable data for a tool, before materials are factored in.
 * Contains info about how to craft a tool and how it behaves.
 */
public class ToolDefinitionData {
  /** Empty tool data definition instance */
  public static final ToolDefinitionData EMPTY = new ToolDefinitionData(List.of(), ErrorFactory.RUNTIME);
  /** Loadable to parse definition data from JSON */
  public static final RecordLoadable<ToolDefinitionData> LOADABLE = RecordLoadable.create(ToolModule.WITH_HOOKS.list(0).defaultField("modules", List.of(), d -> d.modules), ErrorFactory.FIELD, ToolDefinitionData::new);

  private final List<WithHooks<ToolModule>> modules;
  @Getter
  private final transient ModuleHookMap hooks;

  private transient StatsNBT baseStats;
  private transient MultiplierNBT multipliers;

  protected ToolDefinitionData(List<WithHooks<ToolModule>> modules, ErrorFactory error) {
    this.modules = modules;
    this.hooks = ModuleHookMap.createMap(modules, error);
  }


  /* Getters */

  /** Gets the given module from the tool */
  public <T> T getHook(ModuleHook<T> hook) {
    return hooks.getOrDefault(hook);
  }


  /* Packet buffers */

  /** Writes a tool definition stat object to a packet buffer */
  @Deprecated
  public void write(FriendlyByteBuf buffer) {
    LOADABLE.encode(buffer, this);
  }

  /** Reads a tool definition stat object from a packet buffer */
  @Deprecated
  public static ToolDefinitionData read(FriendlyByteBuf buffer) {
    return LOADABLE.decode(buffer);
  }
}

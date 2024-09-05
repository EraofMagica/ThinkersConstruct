package dev.tocraft.thconstruct.library.tools.definition.module.build;

import com.google.common.collect.ImmutableMap;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.SlotType;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;
import dev.tocraft.thconstruct.library.tools.nbt.ModDataNBT;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/** Adds starting modifier slots to a tool */
public record ToolSlotsModule(Map<SlotType,Integer> slots) implements VolatileDataToolHook, ToolModule {
  public static final RecordLoadable<ToolSlotsModule> LOADER = RecordLoadable.create(SlotType.LOADABLE.mapWithValues(IntLoadable.FROM_ONE).requiredField("slots", ToolSlotsModule::slots), ToolSlotsModule::new);
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<ToolSlotsModule>defaultHooks(ToolHooks.VOLATILE_DATA);

  public static Builder builder() {
    return new Builder();
  }

  @Override
  public RecordLoadable<ToolSlotsModule> getLoader() {
    return LOADER;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public void addVolatileData(IToolContext context, ModDataNBT volatileData) {
    for (Entry<SlotType,Integer> entry : slots.entrySet()) {
      volatileData.addSlots(entry.getKey(), entry.getValue());
    }
  }

  /** Logic to build a tool definition stats JSON */
  public static class Builder {
    private final ImmutableMap.Builder<SlotType, Integer> builder = ImmutableMap.builder();

    private Builder() {}

    /**
     * Adds a stat to the builder
     * @param slotType  Slot type
     * @param count     Number of slots
     * @return Builder instance
     */
    public Builder slots(SlotType slotType, int count) {
      builder.put(slotType, count);
      return this;
    }

    /**
     * Creates the instance
     * @return  Instance
     */
    public ToolSlotsModule build() {
      return new ToolSlotsModule(builder.build());
    }
  }
}

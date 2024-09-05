package dev.tocraft.thconstruct.library.modifiers.modules.build;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ModifierRemovalHook;
import dev.tocraft.thconstruct.library.modifiers.hook.build.VolatileDataModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.display.DisplayNameModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.util.ModuleWithKey;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.tools.SlotType;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;
import java.util.List;

/**
 * Module for a extra slot modifier with multiple variants based on the slot type
 * @param key             Persistent data key containing the slot name. If null, uses the modifier ID.
 *                        Presently, changing this makes it incompatible with the swappable modifier recipe, this is added for future proofing.
 * @param slotCount       Number of slots to grant
 */
public record SwappableSlotModule(@Nullable ResourceLocation key, int slotCount) implements VolatileDataModifierHook, DisplayNameModifierHook, ModifierRemovalHook, ModifierModule, ModuleWithKey {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<SwappableSlotModule>defaultHooks(ModifierHooks.VOLATILE_DATA, ModifierHooks.DISPLAY_NAME, ModifierHooks.REMOVE);
  /** Format key for swappable variant */
  public static final String FORMAT = dev.tocraft.thconstruct.ThConstruct.makeTranslationKey("modifier", "extra_modifier.type_format");
  public static final RecordLoadable<SwappableSlotModule> LOADER = RecordLoadable.create(
    ModuleWithKey.FIELD,
    IntLoadable.ANY_SHORT.requiredField("slots", SwappableSlotModule::slotCount),
    SwappableSlotModule::new);

  public SwappableSlotModule(int slotCount) {
    this(null, slotCount);
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry, Component name) {
    String slotName = tool.getPersistentData().getString(getKey(entry.getModifier()));
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        return Component.translatable(FORMAT, name.plainCopy(), type.getDisplayName()).withStyle(style -> style.withColor(type.getColor()));
      }
    }
    return name;
  }

  @Override
  public Integer getPriority() {
    // show lower priority so they group together
    return 50;
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    String slotName = context.getPersistentData().getString(getKey(modifier.getModifier()));
    if (!slotName.isEmpty()) {
      SlotType type = SlotType.getIfPresent(slotName);
      if (type != null) {
        volatileData.addSlots(type, slotCount);
      }
    }
  }

  @Nullable
  @Override
  public Component onRemoved(IToolStackView tool, Modifier modifier) {
    tool.getPersistentData().remove(getKey(modifier));
    return null;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public IGenericLoader<? extends ModifierModule> getLoader() {
    return LOADER;
  }

  /** Module to add (or remove) additional slots based on the given swappable slot type */
  public record BonusSlot(@Nullable ResourceLocation key, SlotType match, SlotType bonus, int slotCount) implements VolatileDataModifierHook, ModifierModule, ModuleWithKey {
    private static final List<ModuleHook<?>> DEFAULT_HOOKS = List.of(ModifierHooks.VOLATILE_DATA);
    public static final RecordLoadable<BonusSlot> LOADER = RecordLoadable.create(
      ModuleWithKey.FIELD,
      SlotType.LOADABLE.requiredField("match", BonusSlot::match),
      SlotType.LOADABLE.requiredField("bonus", BonusSlot::bonus),
      IntLoadable.ANY_SHORT.requiredField("slots", BonusSlot::slotCount),
      BonusSlot::new);

    public BonusSlot(SlotType match, SlotType penalty, int slotCount) {
      this(null, match, penalty, slotCount);
    }

    @Override
    public void addVolatileData(IToolContext context, ModifierEntry modifier, ModDataNBT volatileData) {
      String slotName = context.getPersistentData().getString(getKey(modifier.getModifier()));
      if (!slotName.isEmpty() && match.getName().equals(slotName)) {
        volatileData.addSlots(bonus, slotCount);
      }
    }

    @Override
    public List<ModuleHook<?>> getDefaultHooks() {
      return DEFAULT_HOOKS;
    }

    @Override
    public IGenericLoader<? extends ModifierModule> getLoader() {
      return LOADER;
    }
  }
}

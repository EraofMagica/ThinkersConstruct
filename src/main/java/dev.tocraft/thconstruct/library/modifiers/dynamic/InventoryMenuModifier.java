package dev.tocraft.thconstruct.library.modifiers.dynamic;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import dev.tocraft.eomantle.client.TooltipKey;
import dev.tocraft.eomantle.data.loadable.primitive.IntLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.interaction.InteractionSource;
import dev.tocraft.thconstruct.library.modifiers.hook.interaction.KeybindInteractModifierHook;
import dev.tocraft.thconstruct.library.modifiers.impl.InventoryModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

// TODO: migrate to a modifier module
public class InventoryMenuModifier extends InventoryModifier implements KeybindInteractModifierHook, GeneralInteractionModifierHook {
  /** Loader instance */
  public static final RecordLoadable<InventoryMenuModifier> LOADER = RecordLoadable.create(IntLoadable.FROM_ONE.requiredField("size", m -> m.slotsPerLevel), InventoryMenuModifier::new);

  public InventoryMenuModifier(int size) {
    super(size);
  }

  public InventoryMenuModifier(ResourceLocation key, int size) {
    super(key, size);
  }

  @Override
  public int getPriority() {
    return 75; // run latest so the keybind does not prevent shield strap or tool belt
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry) {
    return DualOptionInteraction.formatModifierName(tool, this, super.getDisplayName(tool, entry));
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot slot, TooltipKey keyModifier) {
    return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot).consumesAction();
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (player.isCrouching() && tool.getHook(ToolHooks.INTERACTION).canInteract(tool, modifier.getId(), source)) {
      EquipmentSlot slot = source.getSlot(hand);
      return ToolInventoryCapability.tryOpenContainer(player.getItemBySlot(slot), tool, player, slot);
    }
    return InteractionResult.PASS;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.ARMOR_INTERACT, ModifierHooks.GENERAL_INTERACT);
  }

  @Override
  public IGenericLoader<? extends Modifier> getLoader() {
    return LOADER;
  }
}

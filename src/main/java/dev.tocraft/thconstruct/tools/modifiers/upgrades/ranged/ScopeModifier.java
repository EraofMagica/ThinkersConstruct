package dev.tocraft.thconstruct.tools.modifiers.upgrades.ranged;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataKeys;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

public class ScopeModifier extends Modifier implements EquipmentChangeModifierHook {
  public static final ResourceLocation SCOPE = dev.tocraft.thconstruct.ThConstruct.getResource("longbow_scope");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getEntity().level.isClientSide) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0) {
        context.getTinkerData().ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(SCOPE));
      }
    }
  }

  /**
   * Implementation of using tick that supports scopes
   * @param tool       Tool performing interaction
   * @param entity     Interacting entity
   * @param chargeTime  Amount of ticks the tool has charged for, typically just use duration - tiee left
   */
  public static void scopingUsingTick(IToolStackView tool, LivingEntity entity, int chargeTime) {
    if (entity.level.isClientSide && tool.getModifierLevel(TinkerModifiers.scope.getId()) > 0) {
      float drawTime = tool.getPersistentData().getInt(GeneralInteractionModifierHook.KEY_DRAWTIME);
      if (chargeTime > 0 && drawTime > 0) {
        entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).set(SCOPE, 1 - (0.6f * Math.min(chargeTime / drawTime, 1))));
      }
    }
  }

  /**
   * Cancels the scoping effect for the given entity
   * @param entity  Entity
   */
  public static void stopScoping(LivingEntity entity) {
    if (entity.level.isClientSide) {
      entity.getCapability(TinkerDataCapability.CAPABILITY).ifPresent(data -> data.computeIfAbsent(TinkerDataKeys.FOV_MODIFIER).remove(SCOPE));
    }
  }
}

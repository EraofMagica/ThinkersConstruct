package dev.tocraft.thconstruct.tools.modifiers.traits.general;

import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.behavior.ToolDamageModifierHook;
import dev.tocraft.thconstruct.library.modifiers.impl.NoLevelsModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

public class TannedModifier extends NoLevelsModifier implements ToolDamageModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.TOOL_DAMAGE);
  }

  @Override
  public int getPriority() {
    // higher than stoneshield, overslime, and reinforced
    return 200;
  }

  @Override
  public int onDamageTool(IToolStackView tool, ModifierEntry modifier, int amount, @Nullable LivingEntity holder) {
    return amount >= 1 ? 1 : 0;
  }
}

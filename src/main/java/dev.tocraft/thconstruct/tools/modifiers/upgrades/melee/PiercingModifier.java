package dev.tocraft.thconstruct.tools.modifiers.upgrades.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TooltipFlag;
import dev.tocraft.eomantle.client.TooltipKey;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ToolStatsModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.display.TooltipModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.helper.ToolAttackUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.stat.ModifierStatsBuilder;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;

public class PiercingModifier extends Modifier implements ToolStatsModifierHook, MeleeHitModifierHook, TooltipModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.TOOL_STATS, ModifierHooks.MELEE_HIT, ModifierHooks.TOOLTIP);
  }

  @Override
  public void addToolStats(IToolContext context, ModifierEntry modifier, ModifierStatsBuilder builder) {
    ToolStats.ATTACK_DAMAGE.add(builder, -0.5f * modifier.getEffectiveLevel());
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    // deals 0.5 pierce damage per level, scaled, half of sharpness
    DamageSource source;
    Player player = context.getPlayerAttacker();
    if (player != null) {
      source = DamageSource.playerAttack(player);
    } else {
      source = DamageSource.mobAttack(context.getAttacker());
    }
    source.bypassArmor();
    float secondaryDamage = (modifier.getEffectiveLevel() * tool.getMultiplier(ToolStats.ATTACK_DAMAGE)) * context.getCooldown();
    if (context.isCritical()) {
      secondaryDamage *= 1.5f;
    }
    ToolAttackUtil.attackEntitySecondary(source, secondaryDamage, context.getTarget(), context.getLivingTarget(), true);
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    TooltipModifierHook.addDamageBoost(tool, this, modifier.getEffectiveLevel(), tooltip);
  }
}

package dev.tocraft.thconstruct.tools.modifiers.traits.melee;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.phys.EntityHitResult;
import dev.tocraft.eomantle.client.TooltipKey;
import dev.tocraft.thconstruct.common.TinkerEffect;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeDamageModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.display.TooltipModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;
import dev.tocraft.thconstruct.library.tools.stat.FloatToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;
import dev.tocraft.thconstruct.tools.TinkerModifiers;
import dev.tocraft.thconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class InsatiableModifier extends Modifier implements ProjectileHitModifierHook, ConditionalStatModifierHook, MeleeDamageModifierHook, MeleeHitModifierHook, TooltipModifierHook {
  public static final ToolType[] TYPES = {ToolType.MELEE, ToolType.RANGED};

  /** Gets the current bonus for the entity */
  private static float getBonus(LivingEntity attacker, int level, ToolType type) {
    int effectLevel = TinkerModifiers.insatiableEffect.get(type).getLevel(attacker) + 1;
    return level * effectLevel / 4f;
  }

  /** Applies the effect to the target */
  public static void applyEffect(LivingEntity living, ToolType type, int duration, int add, int maxLevel) {
    TinkerEffect effect = TinkerModifiers.insatiableEffect.get(type);
    effect.apply(living, duration, Math.min(maxLevel, effect.getLevel(living) + add), true);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_HIT, ModifierHooks.CONDITIONAL_STAT, ModifierHooks.MELEE_DAMAGE, ModifierHooks.MELEE_HIT, ModifierHooks.TOOLTIP);
  }

  @Override
  public float getMeleeDamage(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float baseDamage, float damage) {
    // gives +2 damage per level at max
    return damage + (getBonus(context.getAttacker(), modifier.getLevel(), ToolType.MELEE) * tool.getMultiplier(ToolStats.ATTACK_DAMAGE));
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    // 8 hits gets you to max, levels faster at higher levels
    if (!context.isExtraAttack() && context.isFullyCharged()) {
      applyEffect(context.getAttacker(), ToolType.MELEE, 5*20, 1, 7);
    }
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (stat == ToolStats.PROJECTILE_DAMAGE) {
      // get bonus is +2 damage per level, but we want to half for the actual damage due to velocity stuff
      baseValue += (getBonus(living, modifier.getLevel(), ToolType.RANGED) / 2f * multiplier);
    }
    return baseValue;
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    if (attacker != null) {
      applyEffect(attacker, ToolType.RANGED, 10*20, 1, 7);
    }
    return false;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    ToolType type = ToolType.from(tool.getItem(), TYPES);
    if (type != null) {
      int level = modifier.getLevel();
      float bonus = level * 2;
      if (player != null && key == TooltipKey.SHIFT) {
        bonus = getBonus(player, level, type);
      }
      if (bonus > 0) {
        TooltipModifierHook.addFlatBoost(this, TooltipModifierHook.statName(this, ToolStats.ATTACK_DAMAGE), bonus, tooltip);
      }
    }
  }
}

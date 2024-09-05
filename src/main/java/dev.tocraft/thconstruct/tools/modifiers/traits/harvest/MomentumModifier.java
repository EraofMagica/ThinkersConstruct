package dev.tocraft.thconstruct.tools.modifiers.traits.harvest;

import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.TooltipFlag;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import dev.tocraft.eomantle.client.TooltipKey;
import dev.tocraft.thconstruct.common.TinkerEffect;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.display.TooltipModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ProtectionModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.ToolHarvestContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;
import dev.tocraft.thconstruct.library.tools.stat.FloatToolStat;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;
import dev.tocraft.thconstruct.tools.TinkerModifiers;
import dev.tocraft.thconstruct.tools.stats.ToolType;

import javax.annotation.Nullable;
import java.util.List;

public class MomentumModifier extends Modifier implements ProjectileLaunchModifierHook, ConditionalStatModifierHook, BlockBreakModifierHook, BreakSpeedModifierHook, TooltipModifierHook {
  private static final Component SPEED = dev.tocraft.thconstruct.ThConstruct.makeTranslation("modifier", "momentum.speed");

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.CONDITIONAL_STAT, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.BLOCK_BREAK, ModifierHooks.BREAK_SPEED, ModifierHooks.TOOLTIP);
  }

  @Override
  public int getPriority() {
    // run this last as we boost original speed, adds to existing boosts
    return 75;
  }

  /** Gets the bonus for the modifier */
  private static float getBonus(LivingEntity living, ToolType type, ModifierEntry modifier) {
    return modifier.getEffectiveLevel() * (TinkerModifiers.momentumEffect.get(type).getLevel(living) + 1);
  }

  /** Applies the effect to the target */
  private static void applyEffect(LivingEntity living, ToolType type, int duration, int maxLevel) {
    TinkerEffect effect = TinkerModifiers.momentumEffect.get(type);
    effect.apply(living, duration, Math.min(maxLevel, effect.getLevel(living) + 1), true);
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      // 25% boost per level at max
      event.setNewSpeed(event.getNewSpeed() * (1 + getBonus(event.getEntity(), ToolType.HARVEST, modifier) / 128f));
    }
  }

  @Override
  public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    if (context.canHarvest() && context.isEffective() && !context.isAOE()) {
      // funny duration formula from 1.12, guess it makes faster tools have a slightly shorter effect
      int duration = (int) ((10f / tool.getStats().get(ToolStats.MINING_SPEED)) * 1.5f * 20f);
      // 32 blocks gets you to max, effect is stronger at higher levels
      applyEffect(context.getLiving(), ToolType.HARVEST, duration, 31);
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    if (primary && (arrow == null || arrow.isCritArrow())) {
      // 16 arrows gets you to max
      applyEffect(shooter, ToolType.RANGED, 5*20, 15);
    }
  }

  @Override
  public float modifyStat(IToolStackView tool, ModifierEntry modifier, LivingEntity living, FloatToolStat stat, float baseValue, float multiplier) {
    if (stat == ToolStats.DRAW_SPEED) {
      return baseValue * (1 + getBonus(living, ToolType.RANGED, modifier) / 64f);
    }
    return baseValue;
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey key, TooltipFlag tooltipFlag) {
    ToolType type = ToolType.from(tool.getItem(), ToolType.NO_MELEE);
    if (type != null) {
      float bonus;
      if (player != null && key == TooltipKey.SHIFT) {
        bonus = getBonus(player, type, modifier) / (switch (type) {
          default -> 128;
          case RANGED -> 64;
          case ARMOR -> 4;
        });
      } else {
        bonus = modifier.getEffectiveLevel();
        if (type != ToolType.ARMOR) {
          bonus *= 0.25f;
        }
      }
      if (bonus > 0) {
        if (type == ToolType.ARMOR) {
          ProtectionModule.addResistanceTooltip(tool, this, bonus * 2.5f, player, tooltip);
        } else {
          TooltipModifierHook.addPercentBoost(this, SPEED, bonus, tooltip);
        }
      }
    }
  }
}

package dev.tocraft.thconstruct.tools.modifiers.traits.harvest;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerEvent.BreakSpeed;
import dev.tocraft.eomantle.client.TooltipKey;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.common.config.Config;
import dev.tocraft.thconstruct.common.recipe.RecipeCacheInvalidator;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.display.TooltipModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.mining.BreakSpeedModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.recipe.TinkerRecipeTypes;
import dev.tocraft.thconstruct.library.recipe.melting.IMeltingContainer;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SearingModifier extends Modifier implements BreakSpeedModifierHook, TooltipModifierHook {
  /** Container for melting recipe lookup */
  private static final SearingContainer CONTAINER = new SearingContainer();
  /** Cache of item forms of blocks which have a boost */
  private static final Map<Item, Boolean> BOOSTED_BLOCKS = new ConcurrentHashMap<>();
  static {
    RecipeCacheInvalidator.addReloadListener(client -> BOOSTED_BLOCKS.clear());
  }

  /** Checks if the modifier is effective on the given block state */
  private static boolean isEffective(Level world, Item item) {
    CONTAINER.setStack(new ItemStack(item));
    boolean effective = world.getRecipeManager().getRecipeFor(TinkerRecipeTypes.MELTING.get(), CONTAINER, world).isPresent();
    CONTAINER.setStack(ItemStack.EMPTY);
    return effective;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.BREAK_SPEED, ModifierHooks.TOOLTIP);
  }

  @Override
  public void onBreakSpeed(IToolStackView tool, ModifierEntry modifier, BreakSpeed event, Direction sideHit, boolean isEffective, float miningSpeedModifier) {
    if (isEffective) {
      BlockState state = event.getState();
      Item item = state.getBlock().asItem();
      if (item != Items.AIR) {
        Level world = event.getEntity().level;
        // +7 per level if it has a melting recipe, cache to save lookup time
        // TODO: consider whether we should use getCloneItemStack, problem is I don't want a position based logic and its possible the result is BE based
        if (BOOSTED_BLOCKS.computeIfAbsent(item, i -> isEffective(world, i)) == Boolean.TRUE) {
          event.setNewSpeed(event.getNewSpeed() + modifier.getLevel() * 6 * tool.getMultiplier(ToolStats.MINING_SPEED) * miningSpeedModifier);
        }
      }
    }
  }

  @Override
  public void addTooltip(IToolStackView tool, ModifierEntry modifier, @Nullable Player player, List<Component> tooltip, TooltipKey tooltipKey, TooltipFlag tooltipFlag) {
    TooltipModifierHook.addStatBoost(tool, this, ToolStats.MINING_SPEED, TinkerTags.Items.HARVEST, 7 * modifier.getLevel(), tooltip);
  }

  /** Container implementation for recipe lookup */
  private static class SearingContainer implements IMeltingContainer {
    @Getter @Setter
    private ItemStack stack = ItemStack.EMPTY;

    @Override
    public IOreRate getOreRate() {
      return Config.COMMON.melterOreRate;
    }
  }
}

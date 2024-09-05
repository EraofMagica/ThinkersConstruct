package dev.tocraft.thconstruct.shared;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkHooks;
import dev.tocraft.eomantle.inventory.BaseContainerMenu;
import dev.tocraft.thconstruct.common.Sounds;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.world.TinkerWorld;

@SuppressWarnings("unused")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Mod.EventBusSubscriber(modid = dev.tocraft.thconstruct.ThConstruct.MOD_ID)
public class CommonsEvents {

  // Slimy block jump stuff
  @SubscribeEvent
  static void onLivingJump(LivingEvent.LivingJumpEvent event) {
    if (event.getEntity() == null) {
      return;
    }

    // check if we jumped from a slime block
    BlockPos pos = new BlockPos(event.getEntity().getX(), event.getEntity().getY(), event.getEntity().getZ());
    if (event.getEntity().getCommandSenderWorld().isEmptyBlock(pos)) {
      pos = pos.below();
    }
    BlockState state = event.getEntity().getCommandSenderWorld().getBlockState(pos);
    Block block = state.getBlock();

    if (TinkerWorld.congealedSlime.contains(block)) {
      bounce(event.getEntity(), 0.25f);
    } else if (state.is(TinkerTags.Blocks.SLIMY_SOIL)) {
      bounce(event.getEntity(), 0.06f);
    }
  }

  /** Handles opening our containers as the vanilla logic does not grant TE access */
  @SubscribeEvent
  static void openSpectatorMenu(RightClickBlock event) {
    Player player = event.getEntity();
    if (player.isSpectator()) {
      BlockPos pos = event.getPos();
      Level world = event.getLevel();
      BlockState state = world.getBlockState(pos);
      // only handle our blocks, no guarantee this will work with other mods
      if (dev.tocraft.thconstruct.ThConstruct.MOD_ID.equals(Registry.BLOCK.getKey(state.getBlock()).getNamespace())) {
        MenuProvider provider = state.getMenuProvider(world, pos);
        event.setCanceled(true);
        if (provider != null) {
          if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openScreen(serverPlayer, provider, pos);
            if (player.containerMenu instanceof BaseContainerMenu<?> menu) {
              menu.syncOnOpen(serverPlayer);
            }
          }
          event.setCancellationResult(InteractionResult.SUCCESS);
        }
        event.setCancellationResult(InteractionResult.PASS);
      }
    }
  }

  private static void bounce(Entity entity, float amount) {
    entity.setDeltaMovement(entity.getDeltaMovement().add(0.0D, amount, 0.0D));
    entity.playSound(Sounds.SLIMY_BOUNCE.getSound(), 0.5f + amount, 1f);
  }
}

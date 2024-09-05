package dev.tocraft.thconstruct.tools.modifiers.upgrades.general;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.mining.BlockBreakModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.special.PlantHarvestModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.special.ShearsModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.context.ToolHarvestContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

import javax.annotation.Nullable;
import java.util.List;

public class MagneticModifier extends Modifier implements PlantHarvestModifierHook, ShearsModifierHook, BlockBreakModifierHook, MeleeHitModifierHook, ProjectileLaunchModifierHook {
  /** Player modifier data key for haste */
  private static final TinkerDataKey<Integer> MAGNET = dev.tocraft.thconstruct.ThConstruct.createKey("magnet");

  public MagneticModifier() {
    // TODO: move this out of constructor to generalized logic
    MinecraftForge.EVENT_BUS.addListener(MagneticModifier::onLivingTick);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.PLANT_HARVEST, ModifierHooks.SHEAR_ENTITY, ModifierHooks.BLOCK_BREAK, ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_LAUNCH);
    hookBuilder.addModule(new ArmorLevelModule(MAGNET, false, null));
  }

  @Override
  public void afterBlockBreak(IToolStackView tool, ModifierEntry modifier, ToolHarvestContext context) {
    if (!context.isAOE()) {
      TinkerModifiers.magneticEffect.get().apply(context.getLiving(), 30, modifier.getLevel() - 1);
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (!context.isExtraAttack()) {
      TinkerModifiers.magneticEffect.get().apply(context.getAttacker(), 30, modifier.getLevel() - 1);
    }
  }

  @Override
  public void afterHarvest(IToolStackView tool, ModifierEntry modifier, UseOnContext context, ServerLevel world, BlockState state, BlockPos pos) {
    Player player = context.getPlayer();
    if (player != null) {
      TinkerModifiers.magneticEffect.get().apply(player, 30, modifier.getLevel() - 1);
    }
  }

  @Override
  public void afterShearEntity(IToolStackView tool, ModifierEntry modifier, Player player, Entity entity, boolean isTarget) {
    if (isTarget) {
      TinkerModifiers.magneticEffect.get().apply(player, 30, modifier.getLevel() - 1);
    }
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    if (primary) {
      TinkerModifiers.magneticEffect.get().apply(shooter, 30, modifier.getLevel() - 1);
    }
  }


  // armor

  /** Called to perform the magnet for armor */
  private static void onLivingTick(LivingTickEvent event) {
    // TOOD: this will run on any held armor that is also melee/harvest, is that a problem?
    LivingEntity entity = event.getEntity();
    if (!entity.isSpectator() && (entity.tickCount & 1) == 0) {
      int level = ArmorLevelModule.getLevel(entity, MAGNET);
      if (level > 0) {
        applyMagnet(entity, level);
      }
    }
  }

  /** Performs the magnetic effect */
  public static <T extends Entity> void applyVelocity(LivingEntity entity, int amplifier, Class<T> targetClass, int minRange, float speed, int maxPush) {
    // super magnetic - inspired by botanias code
    double x = entity.getX();
    double y = entity.getY();
    double z = entity.getZ();
    float range = minRange + amplifier;
    List<T> targets = entity.level.getEntitiesOfClass(targetClass, new AABB(x - range, y - range, z - range, x + range, y + range, z + range));

    // only pull up to a max targets
    int pulled = 0;
    for (T target : targets) {
      if (target.isRemoved()) {
        continue;
      }
      // calculate direction: item -> player
      Vec3 vec = entity.position()
                       .subtract(target.getX(), target.getY(), target.getZ())
                       .normalize()
                       .scale(speed * (amplifier + 1));
      if (!target.isNoGravity()) {
        vec = vec.add(0, 0.04f, 0);
      }

      // we calculated the movement vector and set it to the correct strength.. now we apply it \o/
      target.setDeltaMovement(target.getDeltaMovement().add(vec));

      pulled++;
      if (pulled > maxPush) {
        break;
      }
    }
  }

  /** Performs the magnetic effect */
  public static void applyMagnet(LivingEntity entity, int amplifier) {
    applyVelocity(entity, amplifier, ItemEntity.class, 3, 0.05f, 100);
  }
}

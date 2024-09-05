package dev.tocraft.thconstruct.tools.modifiers.ability.fluid;

import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.UseAnim;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffectManager;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffects;
import dev.tocraft.thconstruct.library.modifiers.hook.build.ConditionalStatModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.interaction.GeneralInteractionModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.interaction.InteractionSource;
import dev.tocraft.thconstruct.library.modifiers.modules.build.StatBoostModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.EntityModifierCapability;
import dev.tocraft.thconstruct.library.tools.capability.PersistentDataCapability;
import dev.tocraft.thconstruct.library.tools.capability.fluid.ToolTankHelper;
import dev.tocraft.thconstruct.library.tools.helper.ModifierUtil;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.item.ranged.ModifiableLauncherItem;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;
import dev.tocraft.thconstruct.tools.entity.FluidEffectProjectile;
import dev.tocraft.thconstruct.tools.modifiers.ability.interaction.BlockingModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.ranged.ScopeModifier;

import static dev.tocraft.thconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;

/** Modifier that fires fluid as a projectile */
public class SpittingModifier extends Modifier implements GeneralInteractionModifierHook {
  @Override
  protected void registerHooks(Builder builder) {
    builder.addHook(this, ModifierHooks.GENERAL_INTERACT);
    builder.addModule(ToolTankHelper.TANK_HANDLER);
    builder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
  }

  @Override
  public int getUseDuration(IToolStackView tool, ModifierEntry modifier) {
    return 72000;
  }

  @Override
  public UseAnim getUseAction(IToolStackView tool, ModifierEntry modifier) {
    return BlockingModifier.blockWhileCharging(tool, UseAnim.BOW);
  }

  @Override
  public InteractionResult onToolUse(IToolStackView tool, ModifierEntry modifier, Player player, InteractionHand hand, InteractionSource source) {
    if (!tool.isBroken() && source == InteractionSource.RIGHT_CLICK) {
      // launch if the fluid has effects, cannot simulate as we don't know the target yet
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (fluid.getAmount() >= (1 + 2 * (modifier.getLevel() - 1)) && FluidEffectManager.INSTANCE.find(fluid.getFluid()).hasEffects()) {
        GeneralInteractionModifierHook.startUsingWithDrawtime(tool, modifier.getId(), player, hand, 1.5f);
        return InteractionResult.SUCCESS;
      }
    }
    return InteractionResult.PASS;
  }

  @Override
  public void onUsingTick(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    ScopeModifier.scopingUsingTick(tool, entity, getUseDuration(tool, modifier) - timeLeft);
  }

  @Override
  public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    ScopeModifier.stopScoping(entity);
    if (!entity.level.isClientSide) {
      int chargeTime = getUseDuration(tool, modifier) - timeLeft;
      if (chargeTime > 0) {
        // find the fluid to spit
        FluidStack fluid = TANK_HELPER.getFluid(tool);
        if (!fluid.isEmpty()) {
          FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
          if (recipe.hasEffects()) {
            // projectile stats
            float charge = GeneralInteractionModifierHook.getToolCharge(tool, chargeTime);
            // power - size of each individual projectile
            float power = charge * ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.PROJECTILE_DAMAGE);
            // level acts like multishot level, meaning higher produces more projectiles
            int level = modifier.intEffectiveLevel();
            // amount is the amount per projectile, total cost is amount times level (every other shot is free)
            // if its 0, that means we have only a couple mb left
            int amount = Math.min(fluid.getAmount(), (int)(recipe.getAmount(fluid.getFluid()) * power) * level) / level;
            if (amount > 0) {
              // other stats now that we know we are shooting
              // velocity determines how far it goes, does not impact damage unlike bows
              float velocity = ConditionalStatModifierHook.getModifiedStat(tool, entity, ToolStats.VELOCITY) * charge * 3.0f;
              float inaccuracy = ModifierUtil.getInaccuracy(tool, entity);

              // multishot stuff
              int shots = 1 + 2 * (level - 1);
              float startAngle = ModifiableLauncherItem.getAngleStart(shots);
              int primaryIndex = shots / 2;
              for (int shotIndex = 0; shotIndex < shots; shotIndex++) {
                FluidEffectProjectile spit = new FluidEffectProjectile(entity.level, entity, new FluidStack(fluid, amount), power);

                // setup projectile target
                Vector3f targetVector = new Vector3f(entity.getViewVector(1.0f));
                float angle = startAngle + (10 * shotIndex);
                targetVector.transform(new Quaternion(new Vector3f(entity.getUpVector(1.0f)), angle, true));
                spit.shoot(targetVector.x(), targetVector.y(), targetVector.z(), velocity, inaccuracy);

                // store all modifiers on the spit
                spit.getCapability(EntityModifierCapability.CAPABILITY).ifPresent(cap -> cap.setModifiers(tool.getModifiers()));

                // fetch the persistent data for the arrow as modifiers may want to store data
                NamespacedNBT arrowData = PersistentDataCapability.getOrWarn(spit);
                // let modifiers set properties
                for (ModifierEntry entry : tool.getModifierList()) {
                  entry.getHook(ModifierHooks.PROJECTILE_LAUNCH).onProjectileLaunch(tool, entry, entity, spit, null, arrowData, shotIndex == primaryIndex);
                }

                // finally, fire the projectile
                entity.level.addFreshEntity(spit);
                entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.LLAMA_SPIT, SoundSource.PLAYERS, 1.0F, 1.0F / (entity.level.getRandom().nextFloat() * 0.4F + 1.2F) + charge * 0.5F + (angle / 10f));

              }

              // consume the fluid and durability
              fluid.shrink(amount * level);
              TANK_HELPER.setFluid(tool, fluid);
              ToolDamageUtil.damageAnimated(tool, shots, entity, entity.getUsedItemHand());
            }
          }
        }
      }
    }
  }

}

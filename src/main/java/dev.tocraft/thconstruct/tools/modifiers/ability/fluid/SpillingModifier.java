package dev.tocraft.thconstruct.tools.modifiers.ability.fluid;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidType;
import net.minecraftforge.fluids.capability.IFluidHandler.FluidAction;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffectContext;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffectManager;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffects;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.modules.build.StatBoostModule;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.capability.fluid.ToolTankHelper;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import static dev.tocraft.thconstruct.library.tools.capability.fluid.ToolTankHelper.TANK_HELPER;
import static dev.tocraft.thconstruct.tools.modifiers.ability.fluid.UseFluidOnHitModifier.spawnParticles;

/** Modifier applying fluid effects on melee hit */
public class SpillingModifier extends Modifier implements MeleeHitModifierHook {
  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addModule(ToolTankHelper.TANK_HANDLER);
    hookBuilder.addModule(StatBoostModule.add(ToolTankHelper.CAPACITY_STAT).eachLevel(FluidType.BUCKET_VOLUME));
    hookBuilder.addHook(this, ModifierHooks.MELEE_HIT);
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    if (damageDealt > 0 && context.isFullyCharged()) {
      FluidStack fluid = TANK_HELPER.getFluid(tool);
      if (!fluid.isEmpty()) {
        FluidEffects recipe = FluidEffectManager.INSTANCE.find(fluid.getFluid());
        if (recipe.hasEntityEffects()) {
          LivingEntity living = context.getAttacker();
          Player player = context.getPlayerAttacker();
          int consumed = recipe.applyToEntity(fluid, modifier.getEffectiveLevel(), new FluidEffectContext.Entity(living.level, living, player, null, context.getTarget(), context.getLivingTarget()), FluidAction.EXECUTE);
          if (consumed > 0 && (player == null || !player.isCreative())) {
            spawnParticles(context.getTarget(), fluid);
            fluid.shrink(consumed);
            TANK_HELPER.setFluid(tool, fluid);
          }
        }
      }
    }
  }
}

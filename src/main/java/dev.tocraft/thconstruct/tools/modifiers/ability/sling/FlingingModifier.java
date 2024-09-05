package dev.tocraft.thconstruct.tools.modifiers.ability.sling;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import dev.tocraft.thconstruct.common.Sounds;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.tools.helper.ModifierUtil;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.item.ModifiableItem;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.utils.SlimeBounceHandler;

/** Add velocity opposite of the targeted block */
public class FlingingModifier extends SlingModifier {
  @Override
  public void onStoppedUsing(IToolStackView tool, ModifierEntry modifier, LivingEntity entity, int timeLeft) {
    super.onStoppedUsing(tool, modifier, entity, timeLeft);
    if (entity.isOnGround() && entity instanceof Player player) {
      // check if player was targeting a block
      BlockHitResult mop = ModifiableItem.blockRayTrace(entity.level, player, ClipContext.Fluid.NONE);
      if (mop.getType() == HitResult.Type.BLOCK) {
        // we fling the inverted player look vector
        float f = getForce(tool, modifier, entity, timeLeft, true) * 4;
        if (f > 0) {
          Vec3 vec = player.getLookAngle().normalize();
          float inaccuracy = ModifierUtil.getInaccuracy(tool, player) * 0.0075f;
          RandomSource random = player.getRandom();
          player.push((vec.x + random.nextGaussian() * inaccuracy) * -f,
                      (vec.y + random.nextGaussian() * inaccuracy) * -f / 3f,
                      (vec.z + random.nextGaussian() * inaccuracy) * -f);
          SlimeBounceHandler.addBounceHandler(player);
          if (!entity.level.isClientSide) {
            player.level.playSound(null, player.getX(), player.getY(), player.getZ(), Sounds.SLIME_SLING.getSound(), player.getSoundSource(), 1, 1);
            player.causeFoodExhaustion(0.2F);
            player.getCooldowns().addCooldown(tool.getItem(), 3);
            ToolDamageUtil.damageAnimated(tool, 1, entity);
          }
          return;
        }
      }
    }
    entity.level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), Sounds.SLIME_SLING.getSound(), entity.getSoundSource(), 1, 0.5f);
  }
}

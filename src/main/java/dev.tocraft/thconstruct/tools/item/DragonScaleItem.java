package dev.tocraft.thconstruct.tools.item;

import net.minecraft.world.damagesource.DamageSource;
import dev.tocraft.eomantle.item.TooltipItem;

import net.minecraft.world.item.Item.Properties;

/** Explosion immune tooltip item */
public class DragonScaleItem extends TooltipItem {
  public DragonScaleItem(Properties properties) {
    super(properties);
  }

  @Override
  public boolean canBeHurtBy(DamageSource damageSource) {
    return !damageSource.isExplosion();
  }
}

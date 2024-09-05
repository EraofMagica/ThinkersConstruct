package dev.tocraft.thconstruct.tools.modifiers.ability.ranged;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import dev.tocraft.eomantle.client.ResourceColorManager;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.BowAmmoModifierHook;
import dev.tocraft.thconstruct.library.modifiers.impl.NoLevelsModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.tools.item.CrystalshotItem;

import java.util.function.Predicate;

public class CrystalshotModifier extends NoLevelsModifier implements BowAmmoModifierHook {

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.BOW_AMMO);
  }

  @Override
  public int getPriority() {
    return 60; // before bulk quiver, after
  }

  @Override
  public Component getDisplayName(IToolStackView tool, ModifierEntry entry) {
    // color the display name for the variant
    String variant = tool.getPersistentData().getString(getId());
    if (!variant.isEmpty()) {
      String key = getTranslationKey();
      return Component.translatable(getTranslationKey())
        .withStyle(style -> style.withColor(ResourceColorManager.getTextColor(key + "." + variant)));
    }
    return super.getDisplayName();
  }

  @Override
  public ItemStack findAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack standardAmmo, Predicate<ItemStack> ammoPredicate) {
    return CrystalshotItem.withVariant(tool.getPersistentData().getString(getId()), 64);
  }

  @Override
  public void shrinkAmmo(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, ItemStack ammo, int needed) {
    ToolDamageUtil.damageAnimated(tool, 4 * needed, shooter, shooter.getUsedItemHand());
  }
}

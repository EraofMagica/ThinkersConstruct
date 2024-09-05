package dev.tocraft.thconstruct.tools.modifiers.traits.skull;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.MobEffectEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.EquipmentChangeModifierHook;
import dev.tocraft.thconstruct.library.modifiers.impl.NoLevelsModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentChangeContext;
import dev.tocraft.thconstruct.library.tools.helper.ModifierUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

public class BoonOfSssssModifier extends NoLevelsModifier implements EquipmentChangeModifierHook {
  public BoonOfSssssModifier() {
    MinecraftForge.EVENT_BUS.addListener(EventPriority.NORMAL, false, MobEffectEvent.Added.class, this::onPotionStart);
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    super.registerHooks(hookBuilder);
    hookBuilder.addHook(this, ModifierHooks.EQUIPMENT_CHANGE);
  }

  @Override
  public void onUnequip(IToolStackView tool, ModifierEntry modifier, EquipmentChangeContext context) {
    if (context.getChangedSlot() == EquipmentSlot.HEAD) {
      IToolStackView replacement = context.getReplacementTool();
      if (replacement == null || replacement.getModifierLevel(this) == 0 || replacement.getItem() != tool.getItem()) {
        // cure effects using the helmet
        context.getEntity().curePotionEffects(new ItemStack(tool.getItem()));
      }
    }
  }

  /** Called when the potion effects start to apply this effect */
  private void onPotionStart(MobEffectEvent.Added event) {
    MobEffectInstance newEffect = event.getEffectInstance();
    if (newEffect.getEffect().isBeneficial() && !newEffect.getCurativeItems().isEmpty()) {
      LivingEntity living = event.getEntity();
      // strong bones has to be the helmet as we use it for curing
      // TODO 1.20: can use the new cure effects to make this work in any slot
      ItemStack helmet = living.getItemBySlot(EquipmentSlot.HEAD);
      if (ModifierUtil.getModifierLevel(helmet, this.getId()) > 0) {
        newEffect.duration *= 1.25f;
        newEffect.getCurativeItems().add(new ItemStack(helmet.getItem()));
      }
    }
  }
}

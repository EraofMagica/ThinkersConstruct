package dev.tocraft.thconstruct.tools.modifiers.ability.armor;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import dev.tocraft.eomantle.client.TooltipKey;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import dev.tocraft.thconstruct.library.recipe.partbuilder.Pattern;
import dev.tocraft.thconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import dev.tocraft.thconstruct.library.tools.nbt.IToolContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModDataNBT;

import javax.annotation.Nullable;

public class ShieldStrapModifier extends InventoryMenuModifier {
  private static final ResourceLocation KEY = dev.tocraft.thconstruct.ThConstruct.getResource("shield_strap");
  private static final Pattern PATTERN = new Pattern(dev.tocraft.thconstruct.ThConstruct.MOD_ID, "shield_plus");
  public ShieldStrapModifier() {
    super(KEY, 1);
  }

  @Override
  public int getPriority() {
    return 95; // before pockets and tool belt
  }

  @Override
  public void addVolatileData(IToolContext context, ModifierEntry modifier, ModDataNBT volatileData) {
    super.addVolatileData(context, modifier, volatileData);
    volatileData.putBoolean(ToolInventoryCapability.INCLUDE_OFFHAND, true);
  }

  @Override
  public boolean startInteract(IToolStackView tool, ModifierEntry modifier, Player player, EquipmentSlot equipmentSlot, TooltipKey keyModifier) {
    if (keyModifier == TooltipKey.SHIFT) {
      return super.startInteract(tool, modifier, player, equipmentSlot, keyModifier);
    }
    if (keyModifier == TooltipKey.NORMAL) {
      if (player.level.isClientSide) {
        return true;
      }
      // offhand must be able to go in the pants
      ItemStack offhand = player.getOffhandItem();
      int slots = getSlots(tool, modifier);
      if (offhand.isEmpty() || !ToolInventoryCapability.isBlacklisted(offhand)) {
        ItemStack newOffhand = ItemStack.EMPTY;
        ModDataNBT persistentData = tool.getPersistentData();
        ListTag list = new ListTag();
        // if we have existing items, shift all back by 1
        if (persistentData.contains(KEY, Tag.TAG_LIST)) {
          ListTag original = persistentData.get(KEY, GET_COMPOUND_LIST);
          for (int i = 0; i < original.size(); i++) {
            CompoundTag compoundNBT = original.getCompound(i);
            int slot = compoundNBT.getInt(TAG_SLOT);
            if (slot == 0) {
              newOffhand = ItemStack.of(compoundNBT);
            } else if (slot < slots) {
              CompoundTag copy = compoundNBT.copy();
              copy.putInt(TAG_SLOT, slot - 1);
              list.add(copy);
            }
          }
        }
        // add old offhand to the list
        if (!offhand.isEmpty()) {
          list.add(write(offhand, slots - 1));
        }
        // update offhand
        persistentData.put(KEY, list);
        player.setItemInHand(InteractionHand.OFF_HAND, newOffhand);

        // sound effect
        if (!newOffhand.isEmpty() || !list.isEmpty()) {
          player.level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.ARMOR_EQUIP_GENERIC, SoundSource.PLAYERS, 1.0f, 1.0f);
        }
        return true;
      }
    }
    return false;
  }

  @Nullable
  @Override
  public Pattern getPattern(IToolStackView tool, ModifierEntry modifier, int slot, boolean hasStack) {
    return hasStack ? null : PATTERN;
  }
}

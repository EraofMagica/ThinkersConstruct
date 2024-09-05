package dev.tocraft.thconstruct.library.tools.item.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import dev.tocraft.thconstruct.library.client.armor.ArmorModelManager.ArmorModelDispatcher;
import dev.tocraft.thconstruct.library.tools.definition.ModifiableArmorMaterial;
import dev.tocraft.thconstruct.library.tools.helper.ArmorUtil;
import dev.tocraft.thconstruct.tools.item.ArmorSlotType;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Armor model that applies multiple texture layers in order */
public class MultilayerArmorItem extends ModifiableArmorItem {
  private final ResourceLocation name;
  public MultilayerArmorItem(ModifiableArmorMaterial material, ArmorSlotType slot, Properties properties) {
    super(material, slot, properties);
    this.name = material.getId();
  }

  @Nullable
  @Override
  public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
    return ArmorUtil.getDummyArmorTexture(slot);
  }

  @Override
  public void initializeClient(Consumer<IClientItemExtensions> consumer) {
    consumer.accept(new ArmorModelDispatcher() {
      @Override
      protected ResourceLocation getName() {
        return name;
      }
    });
  }
}

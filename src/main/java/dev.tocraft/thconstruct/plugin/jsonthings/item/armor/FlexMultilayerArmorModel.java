package dev.tocraft.thconstruct.plugin.jsonthings.item.armor;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import dev.tocraft.thconstruct.library.client.armor.ArmorModelManager.ArmorModelDispatcher;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.helper.ArmorUtil;

import javax.annotation.Nullable;
import java.util.function.Consumer;

/** Armor model with two texture layers, the base and an overlay */
public class FlexMultilayerArmorModel extends FlexModifiableArmorItem {
  private final ResourceLocation name;
  public FlexMultilayerArmorModel(ArmorMaterial material, EquipmentSlot slot, Properties properties, ToolDefinition toolDefinition) {
    super(material, slot, properties, toolDefinition);
    this.name = new ResourceLocation(material.getName());
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

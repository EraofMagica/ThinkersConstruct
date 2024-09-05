package dev.tocraft.thconstruct.library.tools.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.level.ItemLike;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.nbt.ModDataNBT;

/** Base interface for all tools that can receive modifiers */
public interface IModifiable extends ItemLike {
  /** Modifier key to make a tool spawn an indestructable entity */
  ResourceLocation INDESTRUCTIBLE_ENTITY = dev.tocraft.thconstruct.ThConstruct.getResource("indestructible");
  /** Modifier key to make a tool spawn an indestructable entity */
  ResourceLocation SHINY = dev.tocraft.thconstruct.ThConstruct.getResource("shiny");
  /** Modifier key to make a tool spawn an indestructable entity */
  ResourceLocation RARITY = dev.tocraft.thconstruct.ThConstruct.getResource("rarity");
  /** Modifier key to defer tool interaction to the offhand if present */
  ResourceLocation DEFER_OFFHAND = dev.tocraft.thconstruct.ThConstruct.getResource("defer_offhand");
  /** Modifier key to entirely disable tool interaction */
  ResourceLocation NO_INTERACTION = dev.tocraft.thconstruct.ThConstruct.getResource("no_interaction");

  /** Gets the definition of this tool for building and applying modifiers */
  ToolDefinition getToolDefinition();

  /**
   * Sets the rarity of the stack
   * @param volatileData     NBT
   * @param rarity  Rarity, only supports vanilla values
   */
  static void setRarity(ModDataNBT volatileData, Rarity rarity) {
    int current = volatileData.getInt(RARITY);
    if (rarity.ordinal() > current) {
      volatileData.putInt(RARITY, rarity.ordinal());
    }
  }
}

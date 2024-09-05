package dev.tocraft.thconstruct.tools;

import dev.tocraft.thconstruct.common.Sounds;
import dev.tocraft.thconstruct.library.tools.definition.ModifiableArmorMaterial;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;

import static dev.tocraft.thconstruct.ThConstruct.getResource;

public class ArmorDefinitions {
   /** Balanced armor set */
  public static final ModifiableArmorMaterial TRAVELERS = ModifiableArmorMaterial.create(getResource("travelers"), Sounds.EQUIP_TRAVELERS.getSound());
  public static final ToolDefinition TRAVELERS_SHIELD = ToolDefinition.create(TinkerTools.travelersShield);

  /** High defense armor set */
  public static final ModifiableArmorMaterial PLATE = ModifiableArmorMaterial.create(getResource("plate"), Sounds.EQUIP_PLATE.getSound());
  public static final ToolDefinition PLATE_SHIELD = ToolDefinition.create(TinkerTools.plateShield);

  /** High modifiers armor set */
  public static final ModifiableArmorMaterial SLIMESUIT = ModifiableArmorMaterial.create(getResource("slime"), Sounds.EQUIP_SLIME.getSound());
}

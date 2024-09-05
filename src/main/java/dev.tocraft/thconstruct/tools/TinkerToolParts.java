package dev.tocraft.thconstruct.tools;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import dev.tocraft.eomantle.registration.object.EnumObject;
import dev.tocraft.eomantle.registration.object.ItemObject;
import dev.tocraft.eomantle.util.SupplierCreativeTab;
import dev.tocraft.thconstruct.common.TinkerModule;
import dev.tocraft.thconstruct.library.materials.MaterialRegistry;
import dev.tocraft.thconstruct.library.materials.definition.IMaterial;
import dev.tocraft.thconstruct.library.tools.part.ToolPartItem;
import dev.tocraft.thconstruct.tools.item.ArmorSlotType;
import dev.tocraft.thconstruct.tools.item.RepairKitItem;
import dev.tocraft.thconstruct.tools.stats.GripMaterialStats;
import dev.tocraft.thconstruct.tools.stats.HandleMaterialStats;
import dev.tocraft.thconstruct.tools.stats.HeadMaterialStats;
import dev.tocraft.thconstruct.tools.stats.LimbMaterialStats;
import dev.tocraft.thconstruct.tools.stats.PlatingMaterialStats;
import dev.tocraft.thconstruct.tools.stats.StatlessMaterialStats;

import java.util.ArrayList;
import java.util.List;

public final class TinkerToolParts extends TinkerModule {
  /** Tab for all tool parts */
  public static final CreativeModeTab TAB_TOOL_PARTS = new SupplierCreativeTab(dev.tocraft.thconstruct.ThConstruct.MOD_ID, "tool_parts", () -> {
    List<IMaterial> materials = new ArrayList<>(MaterialRegistry.getInstance().getVisibleMaterials());
    if (materials.isEmpty()) {
      return new ItemStack(TinkerToolParts.pickHead);
    }
    return TinkerToolParts.pickHead.get().withMaterial(materials.get(dev.tocraft.thconstruct.ThConstruct.RANDOM.nextInt(materials.size())).getIdentifier());
  });
  private static final Item.Properties PARTS_PROPS = new Item.Properties().tab(TAB_TOOL_PARTS);

  // repair kit, technically a head so it filters to things useful for repair
  public static final ItemObject<RepairKitItem> repairKit = ITEMS.register("repair_kit", () -> new RepairKitItem(PARTS_PROPS));

  // rock
  public static final ItemObject<ToolPartItem> pickHead = ITEMS.register("pick_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> hammerHead = ITEMS.register("hammer_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  // axe
  public static final ItemObject<ToolPartItem> smallAxeHead = ITEMS.register("small_axe_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> broadAxeHead = ITEMS.register("broad_axe_head", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  // blades
  public static final ItemObject<ToolPartItem> smallBlade = ITEMS.register("small_blade", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> broadBlade = ITEMS.register("broad_blade", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  // plates
  public static final ItemObject<ToolPartItem> roundPlate = ITEMS.register("round_plate", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  public static final ItemObject<ToolPartItem> largePlate = ITEMS.register("large_plate", () -> new ToolPartItem(PARTS_PROPS, HeadMaterialStats.ID));
  // bows
  public static final ItemObject<ToolPartItem> bowLimb = ITEMS.register("bow_limb", () -> new ToolPartItem(PARTS_PROPS, LimbMaterialStats.ID));
  public static final ItemObject<ToolPartItem> bowGrip = ITEMS.register("bow_grip", () -> new ToolPartItem(PARTS_PROPS, GripMaterialStats.ID));
  public static final ItemObject<ToolPartItem> bowstring = ITEMS.register("bowstring", () -> new ToolPartItem(PARTS_PROPS, StatlessMaterialStats.BOWSTRING.getIdentifier()));
  // other parts
  public static final ItemObject<ToolPartItem> toolBinding = ITEMS.register("tool_binding", () -> new ToolPartItem(PARTS_PROPS, StatlessMaterialStats.BINDING.getIdentifier()));
  public static final ItemObject<ToolPartItem> toolHandle = ITEMS.register("tool_handle", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
  public static final ItemObject<ToolPartItem> toughHandle = ITEMS.register("tough_handle", () -> new ToolPartItem(PARTS_PROPS, HandleMaterialStats.ID));
  // armor
  public static final EnumObject<ArmorSlotType,ToolPartItem> plating = ITEMS.registerEnum(ArmorSlotType.values(), "plating", type -> new ToolPartItem(PARTS_PROPS, PlatingMaterialStats.TYPES.get(type.getIndex()).getId()));
  public static final ItemObject<ToolPartItem> maille = ITEMS.register("maille", () -> new ToolPartItem(PARTS_PROPS, StatlessMaterialStats.MAILLE.getIdentifier()));
  public static final ItemObject<ToolPartItem> shieldCore = ITEMS.register("shield_core", () -> new ToolPartItem(PARTS_PROPS, StatlessMaterialStats.SHIELD_CORE.getIdentifier()));

}

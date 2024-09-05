package dev.tocraft.thconstruct.plugin.jsonthings;

import dev.gigaherz.jsonthings.things.IFlexItem;
import dev.gigaherz.jsonthings.things.serializers.FlexItemType;
import dev.gigaherz.jsonthings.things.serializers.IItemSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.util.Lazy;
import dev.tocraft.eomantle.data.loadable.Loadable;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.primitive.ResourceLocationLoadable;
import dev.tocraft.eomantle.util.JsonHelper;
import dev.tocraft.thconstruct.library.client.armor.texture.ArmorTextureSupplier;
import dev.tocraft.thconstruct.library.materials.stats.MaterialStatsId;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinition;
import dev.tocraft.thconstruct.library.tools.item.armor.DummyArmorMaterial;
import dev.tocraft.thconstruct.plugin.jsonthings.item.FlexModifiableBowItem;
import dev.tocraft.thconstruct.plugin.jsonthings.item.FlexModifiableCrossbowItem;
import dev.tocraft.thconstruct.plugin.jsonthings.item.FlexModifiableItem;
import dev.tocraft.thconstruct.plugin.jsonthings.item.FlexPartCastItem;
import dev.tocraft.thconstruct.plugin.jsonthings.item.FlexRepairKitItem;
import dev.tocraft.thconstruct.plugin.jsonthings.item.FlexToolPartItem;
import dev.tocraft.thconstruct.plugin.jsonthings.item.armor.FlexModifiableArmorItem;
import dev.tocraft.thconstruct.plugin.jsonthings.item.armor.FlexMultilayerArmorModel;
import dev.tocraft.thconstruct.tools.item.ArmorSlotType;

import java.util.ArrayList;
import java.util.List;

/** Collection of custom item types added by Tinkers */
@SuppressWarnings("unused")
public class FlexItemTypes {
  /** Standard tools that need standard properties */
  static final List<Item> TOOL_ITEMS = new ArrayList<>();
  /** All crossbow items that need their predicate registered */
  static final List<Item> CROSSBOW_ITEMS = new ArrayList<>();
  /** All armor items that need the broken predicate */
  static final List<Item> ARMOR_ITEMS = new ArrayList<>();

  /** Adds a thing to a list so we can fetch the instances later */
  private static <T> T add(List<? super T> list, T item) {
    list.add(item);
    return item;
  }

  /** Initializes the item types */
  public static void init() {
    /* Register a tool part to create new tools */
    register("tool_part", data -> {
      MaterialStatsId statType = new MaterialStatsId(JsonHelper.getResourceLocation(data, "stat_type"));
      return (props, builder) -> new FlexToolPartItem(props, statType);
    });

    /* Register an item that can be used to repair tools */
    register("repair_kit", data -> {
      float repairAmount = GsonHelper.getAsFloat(data, "repair_amount");
      return (props, builder) -> new FlexRepairKitItem(props, repairAmount);
    });

    /* Register a modifiable tool instance for melee/harvest tools */
    register("tool", data -> {
      boolean breakBlocksInCreative = GsonHelper.getAsBoolean(data, "break_blocks_in_creative", true);
      return (props, builder) -> add(TOOL_ITEMS, new FlexModifiableItem(props, ToolDefinition.create(builder.getRegistryName()), breakBlocksInCreative));
    });

    /* Register a modifiable tool instance for bow like items (release on finish) */
    register("bow", data -> (props, builder) -> add(TOOL_ITEMS, new FlexModifiableBowItem(props, ToolDefinition.create(builder.getRegistryName()))));

    /* Register a modifiable tool instance for crossbow like items (load on finish) */
    register("crossbow", data -> {
      boolean allowFireworks = GsonHelper.getAsBoolean(data, "allow_fireworks");
      return (props, builder) -> add(CROSSBOW_ITEMS, new FlexModifiableCrossbowItem(props, ToolDefinition.create(builder.getRegistryName()), allowFireworks));
    });

    /* Registries a cast item that shows a part cost in the tooltip */
    register("part_cast", data -> {
      ResourceLocation partId = JsonHelper.getResourceLocation(data, "part");
      return (props, builder) -> new FlexPartCastItem(props, builder, Lazy.of(() -> ((ResourceLocationLoadable<Item>)Loadables.ITEM).fromKey(partId, "part")));
    });


    /* Armor */

    /* Simple armor type with a flat texture */
    register("basic_armor", data -> {
      ResourceLocation name = JsonHelper.getResourceLocation(data, "texture_name");
      SoundEvent sound = Loadables.SOUND_EVENT.getOrDefault(data, "equip_sound", SoundEvents.ARMOR_EQUIP_GENERIC);
      ArmorSlotType slot = JsonHelper.getAsEnum(data, "slot", ArmorSlotType.class);
      return (props, builder) -> add(ARMOR_ITEMS, new FlexModifiableArmorItem(new DummyArmorMaterial(name, sound), slot.getEquipmentSlot(), props, ToolDefinition.create(builder.getRegistryName())));
    });

    /* Layered armor type, used for golden, dyeable, etc */
    Loadable<List<ArmorTextureSupplier>> ARMOR_TEXTURES = ArmorTextureSupplier.LOADER.list(1);
    register("multilayer_armor", data -> {
      ResourceLocation name = JsonHelper.getResourceLocation(data, "model_name");
      SoundEvent sound = Loadables.SOUND_EVENT.getOrDefault(data, "equip_sound", SoundEvents.ARMOR_EQUIP_GENERIC);
      ArmorSlotType slot = JsonHelper.getAsEnum(data, "slot", ArmorSlotType.class);
      return (props, builder) -> add(ARMOR_ITEMS, new FlexMultilayerArmorModel(new DummyArmorMaterial(name, sound), slot.getEquipmentSlot(), props, ToolDefinition.create(builder.getRegistryName())));
    });
  }

  /** Local helper to register our stuff */
  private static <T extends Item & IFlexItem> void register(String name, IItemSerializer<T> factory) {
    FlexItemType.register(dev.tocraft.thconstruct.ThConstruct.resourceString(name), factory);
  }
}

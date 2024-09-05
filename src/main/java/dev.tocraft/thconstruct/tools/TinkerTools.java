package dev.tocraft.thconstruct.tools;

import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import dev.tocraft.eomantle.registration.object.EnumObject;
import dev.tocraft.eomantle.registration.object.ItemObject;
import dev.tocraft.eomantle.util.SupplierCreativeTab;
import dev.tocraft.thconstruct.common.TinkerModule;
import dev.tocraft.thconstruct.common.config.Config;
import dev.tocraft.thconstruct.common.config.ConfigurableAction;
import dev.tocraft.thconstruct.common.data.tags.MaterialTagProvider;
import dev.tocraft.thconstruct.library.client.data.material.GeneratorPartTextureJsonGenerator;
import dev.tocraft.thconstruct.library.client.data.material.MaterialPaletteDebugGenerator;
import dev.tocraft.thconstruct.library.client.data.material.MaterialPartTextureGenerator;
import dev.tocraft.thconstruct.library.json.loot.AddToolDataFunction;
import dev.tocraft.thconstruct.library.json.predicate.tool.HasMaterialPredicate;
import dev.tocraft.thconstruct.library.json.predicate.tool.HasModifierPredicate;
import dev.tocraft.thconstruct.library.json.predicate.tool.HasStatTypePredicate;
import dev.tocraft.thconstruct.library.json.predicate.tool.StatInRangePredicate;
import dev.tocraft.thconstruct.library.json.predicate.tool.StatInSetPredicate;
import dev.tocraft.thconstruct.library.json.predicate.tool.ToolContextPredicate;
import dev.tocraft.thconstruct.library.json.predicate.tool.ToolStackItemPredicate;
import dev.tocraft.thconstruct.library.json.predicate.tool.ToolStackPredicate;
import dev.tocraft.thconstruct.library.materials.RandomMaterial;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.recipe.ingredient.ToolHookIngredient;
import dev.tocraft.thconstruct.library.tools.IndestructibleItemEntity;
import dev.tocraft.thconstruct.library.tools.SlotType;
import dev.tocraft.thconstruct.library.tools.capability.ToolCapabilityProvider;
import dev.tocraft.thconstruct.library.tools.capability.fluid.ToolFluidCapability;
import dev.tocraft.thconstruct.library.tools.capability.inventory.ToolInventoryCapability;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import dev.tocraft.thconstruct.library.tools.definition.module.aoe.BoxAOEIterator;
import dev.tocraft.thconstruct.library.tools.definition.module.aoe.CircleAOEIterator;
import dev.tocraft.thconstruct.library.tools.definition.module.aoe.ConditionalAOEIterator;
import dev.tocraft.thconstruct.library.tools.definition.module.aoe.TreeAOEIterator;
import dev.tocraft.thconstruct.library.tools.definition.module.aoe.VeiningAOEIterator;
import dev.tocraft.thconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.SetStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.ToolActionsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.ToolSlotsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.ToolTraitsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.interaction.DualOptionInteraction;
import dev.tocraft.thconstruct.library.tools.definition.module.interaction.PreferenceSetInteraction;
import dev.tocraft.thconstruct.library.tools.definition.module.material.DefaultMaterialsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.material.MaterialRepairModule;
import dev.tocraft.thconstruct.library.tools.definition.module.material.MaterialStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.material.MaterialTraitsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.material.PartStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.material.PartsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import dev.tocraft.thconstruct.library.tools.definition.module.mining.MaxTierHarvestLogic;
import dev.tocraft.thconstruct.library.tools.definition.module.mining.MiningSpeedModifierModule;
import dev.tocraft.thconstruct.library.tools.definition.module.mining.OneClickBreakModule;
import dev.tocraft.thconstruct.library.tools.definition.module.weapon.CircleWeaponAttack;
import dev.tocraft.thconstruct.library.tools.definition.module.weapon.ParticleWeaponAttack;
import dev.tocraft.thconstruct.library.tools.definition.module.weapon.SweepWeaponAttack;
import dev.tocraft.thconstruct.library.tools.helper.ModifierLootingHandler;
import dev.tocraft.thconstruct.library.tools.item.ModifiableItem;
import dev.tocraft.thconstruct.library.tools.item.armor.ModifiableArmorItem;
import dev.tocraft.thconstruct.library.tools.item.armor.MultilayerArmorItem;
import dev.tocraft.thconstruct.library.tools.item.ranged.ModifiableBowItem;
import dev.tocraft.thconstruct.library.tools.item.ranged.ModifiableCrossbowItem;
import dev.tocraft.thconstruct.library.utils.BlockSideHitListener;
import dev.tocraft.thconstruct.tools.data.ArmorModelProvider;
import dev.tocraft.thconstruct.tools.data.StationSlotLayoutProvider;
import dev.tocraft.thconstruct.tools.data.ToolDefinitionDataProvider;
import dev.tocraft.thconstruct.tools.data.ToolItemModelProvider;
import dev.tocraft.thconstruct.tools.data.ToolsRecipeProvider;
import dev.tocraft.thconstruct.tools.data.material.MaterialDataProvider;
import dev.tocraft.thconstruct.tools.data.material.MaterialRecipeProvider;
import dev.tocraft.thconstruct.tools.data.material.MaterialRenderInfoProvider;
import dev.tocraft.thconstruct.tools.data.material.MaterialStatsDataProvider;
import dev.tocraft.thconstruct.tools.data.material.MaterialTraitsDataProvider;
import dev.tocraft.thconstruct.tools.data.sprite.TinkerMaterialSpriteProvider;
import dev.tocraft.thconstruct.tools.data.sprite.TinkerPartSpriteProvider;
import dev.tocraft.thconstruct.tools.item.ArmorSlotType;
import dev.tocraft.thconstruct.tools.item.CrystalshotItem;
import dev.tocraft.thconstruct.tools.item.CrystalshotItem.CrystalshotEntity;
import dev.tocraft.thconstruct.tools.item.ModifiableSwordItem;
import dev.tocraft.thconstruct.tools.item.SlimeskullItem;
import dev.tocraft.thconstruct.tools.logic.EquipmentChangeWatcher;
import dev.tocraft.thconstruct.tools.menu.ToolContainerMenu;

import static dev.tocraft.thconstruct.ThConstruct.getResource;

/**
 * Contains all complete tool items
 */
public final class TinkerTools extends TinkerModule {
  public TinkerTools() {
    SlotType.init();
    BlockSideHitListener.init();
    ModifierLootingHandler.init();
    RandomMaterial.init();
  }

  /** Creative tab for all tool items */
  public static final CreativeModeTab TAB_TOOLS = new SupplierCreativeTab(dev.tocraft.thconstruct.ThConstruct.MOD_ID, "tools", () -> TinkerTools.pickaxe.get().getRenderTool());

  /** Loot function type for tool add data */
  public static final RegistryObject<LootItemFunctionType> lootAddToolData = LOOT_FUNCTIONS.register("add_tool_data", () -> new LootItemFunctionType(AddToolDataFunction.SERIALIZER));

  /*
   * Items
   */
  private static final Item.Properties TOOL = new Item.Properties().stacksTo(1).tab(TAB_TOOLS);

  public static final ItemObject<ModifiableItem> pickaxe = ITEMS.register("pickaxe", () -> new ModifiableItem(TOOL, ToolDefinitions.PICKAXE));
  public static final ItemObject<ModifiableItem> sledgeHammer = ITEMS.register("sledge_hammer", () -> new ModifiableItem(TOOL, ToolDefinitions.SLEDGE_HAMMER));
  public static final ItemObject<ModifiableItem> veinHammer = ITEMS.register("vein_hammer", () -> new ModifiableItem(TOOL, ToolDefinitions.VEIN_HAMMER));

  public static final ItemObject<ModifiableItem> mattock = ITEMS.register("mattock", () -> new ModifiableItem(TOOL, ToolDefinitions.MATTOCK));
  public static final ItemObject<ModifiableItem> pickadze = ITEMS.register("pickadze", () -> new ModifiableItem(TOOL, ToolDefinitions.PICKADZE));
  public static final ItemObject<ModifiableItem> excavator = ITEMS.register("excavator", () -> new ModifiableItem(TOOL, ToolDefinitions.EXCAVATOR));

  public static final ItemObject<ModifiableItem> handAxe = ITEMS.register("hand_axe", () -> new ModifiableItem(TOOL, ToolDefinitions.HAND_AXE));
  public static final ItemObject<ModifiableItem> broadAxe = ITEMS.register("broad_axe", () -> new ModifiableItem(TOOL, ToolDefinitions.BROAD_AXE));

  public static final ItemObject<ModifiableItem> kama = ITEMS.register("kama", () -> new ModifiableItem(TOOL, ToolDefinitions.KAMA));
  public static final ItemObject<ModifiableItem> scythe = ITEMS.register("scythe", () -> new ModifiableItem(TOOL, ToolDefinitions.SCYTHE));

  public static final ItemObject<ModifiableItem> dagger = ITEMS.register("dagger", () -> new ModifiableSwordItem(new Item.Properties().stacksTo(2).tab(TAB_TOOLS), ToolDefinitions.DAGGER));
  public static final ItemObject<ModifiableItem> sword = ITEMS.register("sword", () -> new ModifiableSwordItem(TOOL, ToolDefinitions.SWORD));
  public static final ItemObject<ModifiableItem> cleaver = ITEMS.register("cleaver", () -> new ModifiableSwordItem(TOOL, ToolDefinitions.CLEAVER));

  public static final ItemObject<ModifiableCrossbowItem> crossbow = ITEMS.register("crossbow", () -> new ModifiableCrossbowItem(TOOL, ToolDefinitions.CROSSBOW));
  public static final ItemObject<ModifiableBowItem> longbow = ITEMS.register("longbow", () -> new ModifiableBowItem(TOOL, ToolDefinitions.LONGBOW));

  public static final ItemObject<ModifiableItem> flintAndBrick = ITEMS.register("flint_and_brick", () -> new ModifiableItem(TOOL, ToolDefinitions.FLINT_AND_BRICK));
  public static final ItemObject<ModifiableItem> skyStaff = ITEMS.register("sky_staff", () -> new ModifiableItem(TOOL, ToolDefinitions.SKY_STAFF));
  public static final ItemObject<ModifiableItem> earthStaff = ITEMS.register("earth_staff", () -> new ModifiableItem(TOOL, ToolDefinitions.EARTH_STAFF));
  public static final ItemObject<ModifiableItem> ichorStaff = ITEMS.register("ichor_staff", () -> new ModifiableItem(TOOL, ToolDefinitions.ICHOR_STAFF));
  public static final ItemObject<ModifiableItem> enderStaff = ITEMS.register("ender_staff", () -> new ModifiableItem(TOOL, ToolDefinitions.ENDER_STAFF));

  // armor
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> travelersGear = ITEMS.registerEnum("travelers", ArmorSlotType.values(), type -> new MultilayerArmorItem(ArmorDefinitions.TRAVELERS, type, TOOL));
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> plateArmor = ITEMS.registerEnum("plate", ArmorSlotType.values(), type -> new MultilayerArmorItem(ArmorDefinitions.PLATE, type, TOOL));
  public static final EnumObject<ArmorSlotType,ModifiableArmorItem> slimesuit = new EnumObject.Builder<ArmorSlotType,ModifiableArmorItem>(ArmorSlotType.class)
    .putAll(ITEMS.registerEnum("slime", new ArmorSlotType[] {ArmorSlotType.BOOTS, ArmorSlotType.LEGGINGS, ArmorSlotType.CHESTPLATE}, type -> new MultilayerArmorItem(ArmorDefinitions.SLIMESUIT, type, TOOL)))
    .put(ArmorSlotType.HELMET, ITEMS.register("slime_helmet", () -> new SlimeskullItem(ArmorDefinitions.SLIMESUIT, TOOL)))
    .build();


  // shields
  public static final ItemObject<ModifiableItem> travelersShield = ITEMS.register("travelers_shield", () -> new ModifiableItem(TOOL, ArmorDefinitions.TRAVELERS_SHIELD));
  public static final ItemObject<ModifiableItem> plateShield = ITEMS.register("plate_shield", () -> new ModifiableItem(TOOL, ArmorDefinitions.PLATE_SHIELD));

  // arrows
  public static final ItemObject<ArrowItem> crystalshotItem = ITEMS.register("crystalshot", () -> new CrystalshotItem(new Item.Properties().tab(TAB_TOOLS)));

  /* Particles */
  public static final RegistryObject<SimpleParticleType> hammerAttackParticle = PARTICLE_TYPES.register("hammer_attack", () -> new SimpleParticleType(true));
  public static final RegistryObject<SimpleParticleType> axeAttackParticle = PARTICLE_TYPES.register("axe_attack", () -> new SimpleParticleType(true));
  public static final RegistryObject<SimpleParticleType> bonkAttackParticle = PARTICLE_TYPES.register("bonk", () -> new SimpleParticleType(true));

  /* Entities */
  public static final RegistryObject<EntityType<IndestructibleItemEntity>> indestructibleItem = ENTITIES.register("indestructible_item", () ->
    EntityType.Builder.<IndestructibleItemEntity>of(IndestructibleItemEntity::new, MobCategory.MISC)
                      .sized(0.25F, 0.25F)
                      .fireImmune());
  public static final RegistryObject<EntityType<CrystalshotEntity>> crystalshotEntity = ENTITIES.register("crystalshot", () ->
    EntityType.Builder.<CrystalshotEntity>of(CrystalshotEntity::new, MobCategory.MISC)
                      .sized(0.5F, 0.5F)
                      .clientTrackingRange(4)
                      .updateInterval(20));

  /* Containers */
  public static final RegistryObject<MenuType<ToolContainerMenu>> toolContainer = MENUS.register("tool_container", ToolContainerMenu::forClient);


  /*
   * Events
   */

  @SubscribeEvent
  void commonSetup(FMLCommonSetupEvent event) {
    EquipmentChangeWatcher.register();
    ToolCapabilityProvider.register(ToolFluidCapability.Provider::new);
    ToolCapabilityProvider.register(ToolInventoryCapability.Provider::new);
    for (ConfigurableAction action : Config.COMMON.toolTweaks) {
      event.enqueueWork(action);
    }
    ModifierHooks.init();
    ToolHooks.init();
  }

  @SubscribeEvent
  void registerRecipeSerializers(RegisterEvent event) {
    if (event.getRegistryKey() == Registry.RECIPE_SERIALIZER_REGISTRY) {
      ItemPredicate.register(ToolStackItemPredicate.ID, ToolStackItemPredicate::deserialize);
      CraftingHelper.register(ToolHookIngredient.Serializer.ID, ToolHookIngredient.Serializer.INSTANCE);

      // tool definition components
      ToolModule.LOADER.register(getResource("base_stats"), SetStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("multiply_stats"), MultiplyStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("tool_actions"), ToolActionsModule.LOADER);
      ToolModule.LOADER.register(getResource("traits"), ToolTraitsModule.LOADER);
      ToolModule.LOADER.register(getResource("modifier_slots"), ToolSlotsModule.LOADER);
      // harvest
      ToolModule.LOADER.register(getResource("is_effective"), IsEffectiveModule.LOADER);
      ToolModule.LOADER.register(getResource("mining_speed_modifier"), MiningSpeedModifierModule.LOADER);
      ToolModule.LOADER.register(getResource("max_tier"), MaxTierHarvestLogic.LOADER);
      ToolModule.LOADER.register(getResource("one_click_break"), OneClickBreakModule.LOADER);
      // material
      ToolModule.LOADER.register(getResource("material_stats"), MaterialStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("part_stats"), PartStatsModule.LOADER);
      ToolModule.LOADER.register(getResource("material_traits"), MaterialTraitsModule.LOADER);
      ToolModule.LOADER.register(getResource("tool_parts"), PartsModule.LOADER);
      ToolModule.LOADER.register(getResource("material_repair"), MaterialRepairModule.LOADER);
      ToolModule.LOADER.register(getResource("default_materials"), DefaultMaterialsModule.LOADER);
      // aoe
      AreaOfEffectIterator.register(getResource("box_aoe"), BoxAOEIterator.LOADER);
      AreaOfEffectIterator.register(getResource("circle_aoe"), CircleAOEIterator.LOADER);
      AreaOfEffectIterator.register(getResource("tree_aoe"), TreeAOEIterator.LOADER);
      AreaOfEffectIterator.register(getResource("vein_aoe"), VeiningAOEIterator.LOADER);
      AreaOfEffectIterator.register(getResource("conditional_aoe"), ConditionalAOEIterator.LOADER);
      // attack
      ToolModule.LOADER.register(getResource("sweep_melee"), SweepWeaponAttack.LOADER);
      ToolModule.LOADER.register(getResource("circle_melee"), CircleWeaponAttack.LOADER);
      ToolModule.LOADER.register(getResource("melee_particle"), ParticleWeaponAttack.LOADER);
      // generic tool modules
      ToolModule.LOADER.register(getResource("dual_option_interaction"), DualOptionInteraction.LOADER);
      ToolModule.LOADER.register(getResource("preference_set_interaction"), PreferenceSetInteraction.LOADER);
      // tool predicates
      ToolContextPredicate.LOADER.register(getResource("has_upgrades"), ToolContextPredicate.HAS_UPGRADES.getLoader());
      ToolContextPredicate.LOADER.register(getResource("has_modifier"), HasModifierPredicate.LOADER);
      ToolContextPredicate.LOADER.register(getResource("has_material"), HasMaterialPredicate.LOADER);
      ToolContextPredicate.LOADER.register(getResource("has_stat_type"), HasStatTypePredicate.LOADER);
      ToolStackPredicate.LOADER.register(getResource("not_broken"), ToolStackPredicate.NOT_BROKEN.getLoader());
      ToolStackPredicate.LOADER.register(getResource("stat_in_range"), StatInRangePredicate.LOADER);
      ToolStackPredicate.LOADER.register(getResource("stat_in_set"), StatInSetPredicate.LOADER);
    }
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean server = event.includeServer();
    boolean client = event.includeClient();
    generator.addProvider(server, new ToolsRecipeProvider(generator));
    generator.addProvider(server, new MaterialRecipeProvider(generator));
    MaterialDataProvider materials = new MaterialDataProvider(generator);
    generator.addProvider(server, materials);
    generator.addProvider(server, new MaterialStatsDataProvider(generator, materials));
    generator.addProvider(server, new MaterialTraitsDataProvider(generator, materials));
    generator.addProvider(server, new ToolDefinitionDataProvider(generator));
    generator.addProvider(server, new StationSlotLayoutProvider(generator));
    generator.addProvider(server, new MaterialTagProvider(generator, existingFileHelper));
    generator.addProvider(client, new ToolItemModelProvider(generator, existingFileHelper));
    TinkerMaterialSpriteProvider materialSprites = new TinkerMaterialSpriteProvider();
    TinkerPartSpriteProvider partSprites = new TinkerPartSpriteProvider();
    generator.addProvider(client, new MaterialRenderInfoProvider(generator, materialSprites, existingFileHelper));
    generator.addProvider(client, new GeneratorPartTextureJsonGenerator(generator, dev.tocraft.thconstruct.ThConstruct.MOD_ID, partSprites));
    generator.addProvider(client, new MaterialPartTextureGenerator(generator, existingFileHelper, partSprites, materialSprites));
    generator.addProvider(client, new MaterialPaletteDebugGenerator(generator, dev.tocraft.thconstruct.ThConstruct.MOD_ID, materialSprites));
    generator.addProvider(client, new ArmorModelProvider(generator));
  }
}

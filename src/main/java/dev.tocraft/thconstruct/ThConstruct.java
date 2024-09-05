package dev.tocraft.thconstruct;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.MissingMappingsEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import dev.tocraft.eomantle.registration.RegistrationHelper;
import dev.tocraft.thconstruct.common.TinkerModule;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.common.config.Config;
import dev.tocraft.thconstruct.common.data.AdvancementsProvider;
import dev.tocraft.thconstruct.common.data.loot.GlobalLootModifiersProvider;
import dev.tocraft.thconstruct.common.data.loot.TConstructLootTableProvider;
import dev.tocraft.thconstruct.common.data.tags.BiomeTagProvider;
import dev.tocraft.thconstruct.common.data.tags.BlockEntityTypeTagProvider;
import dev.tocraft.thconstruct.common.data.tags.BlockTagProvider;
import dev.tocraft.thconstruct.common.data.tags.EnchantmentTagProvider;
import dev.tocraft.thconstruct.common.data.tags.EntityTypeTagProvider;
import dev.tocraft.thconstruct.common.data.tags.FluidTagProvider;
import dev.tocraft.thconstruct.common.data.tags.ItemTagProvider;
import dev.tocraft.thconstruct.common.network.TinkerNetwork;
import dev.tocraft.thconstruct.fluids.TinkerFluids;
import dev.tocraft.thconstruct.gadgets.TinkerGadgets;
import dev.tocraft.thconstruct.library.TinkerBookIDs;
import dev.tocraft.thconstruct.library.materials.MaterialRegistry;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.ComputableDataKey;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability.TinkerDataKey;
import dev.tocraft.thconstruct.library.tools.definition.ToolDefinitionLoader;
import dev.tocraft.thconstruct.library.tools.layout.StationSlotLayoutLoader;
import dev.tocraft.thconstruct.library.utils.Util;
import dev.tocraft.thconstruct.plugin.DietPlugin;
import dev.tocraft.thconstruct.plugin.ImmersiveEngineeringPlugin;
import dev.tocraft.thconstruct.plugin.jsonthings.JsonThingsPlugin;
import dev.tocraft.thconstruct.shared.TinkerClient;
import dev.tocraft.thconstruct.shared.TinkerCommons;
import dev.tocraft.thconstruct.shared.TinkerMaterials;
import dev.tocraft.thconstruct.shared.block.SlimeType;
import dev.tocraft.thconstruct.smeltery.TinkerSmeltery;
import dev.tocraft.thconstruct.tables.TinkerTables;
import dev.tocraft.thconstruct.tools.TinkerModifiers;
import dev.tocraft.thconstruct.tools.TinkerToolParts;
import dev.tocraft.thconstruct.tools.TinkerTools;
import dev.tocraft.thconstruct.tools.stats.ToolType;
import dev.tocraft.thconstruct.world.TinkerStructures;
import dev.tocraft.thconstruct.world.TinkerWorld;

import javax.annotation.Nullable;
import java.util.Locale;
import java.util.Random;
import java.util.function.Supplier;

/**
 * TConstruct, the tool mod. Craft your tools with style, then modify until the original is gone!
 *
 * @author mDiyo
 */

@SuppressWarnings("unused")
@Mod(dev.tocraft.thconstruct.ThConstruct.MOD_ID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ThConstruct {

  public static final String MOD_ID = "thconstruct";
  public static final Logger LOG = LogManager.getLogger(MOD_ID);
  public static final Random RANDOM = new Random();

  /* Instance of this mod, used for grabbing prototype fields */
  public static dev.tocraft.thconstruct.ThConstruct instance;

  public ThConstruct() {
    instance = this;

    Config.init();

    // initialize modules, done this way rather than with annotations to give us control over the order
    MinecraftForge.EVENT_BUS.addListener(dev.tocraft.thconstruct.ThConstruct::missingMappings);
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    // base
    bus.register(new TinkerCommons());
    bus.register(new TinkerMaterials());
    bus.register(new TinkerFluids());
    bus.register(new TinkerGadgets());
    // world
    bus.register(new TinkerWorld());
    bus.register(new TinkerStructures());
    // tools
    bus.register(new TinkerTables());
    bus.register(new TinkerModifiers());
    bus.register(new TinkerToolParts());
    bus.register(new TinkerTools());
    // smeltery
    bus.register(new TinkerSmeltery());

    // init deferred registers
    TinkerModule.initRegisters();
    TinkerNetwork.setup();
    TinkerTags.init();
    // init client logic
    TinkerBookIDs.registerCommandSuggestion();
    DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> TinkerClient::onConstruct);

    // compat
    ModList modList = ModList.get();
    if (modList.isLoaded("immersiveengineering")) {
      bus.register(new ImmersiveEngineeringPlugin());
    }
    if (modList.isLoaded("jsonthings")) {
      JsonThingsPlugin.onConstruct();
    }
    if (modList.isLoaded("diet")) {
      DietPlugin.onConstruct();
    }
  }

  @SubscribeEvent
  static void commonSetup(final FMLCommonSetupEvent event) {
    MaterialRegistry.init();
    ToolDefinitionLoader.init();
    StationSlotLayoutLoader.init();
  }

  @SubscribeEvent
  static void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();
    ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
    boolean server = event.includeServer();
    BlockTagProvider blockTags = new BlockTagProvider(datagenerator, existingFileHelper);
    datagenerator.addProvider(server, blockTags);
    datagenerator.addProvider(server, new ItemTagProvider(datagenerator, blockTags, existingFileHelper));
    datagenerator.addProvider(server, new FluidTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new EntityTypeTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new BlockEntityTypeTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new BiomeTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new EnchantmentTagProvider(datagenerator, existingFileHelper));
    datagenerator.addProvider(server, new TConstructLootTableProvider(datagenerator));
    datagenerator.addProvider(server, new AdvancementsProvider(datagenerator));
    datagenerator.addProvider(server, new GlobalLootModifiersProvider(datagenerator));
  }

  /** Shared behavior between item and block missing mappings */
  @Nullable
  private static Block missingBlock(String name) {
    return switch(name) {
      // blood removal
      case "blood_slime" -> Blocks.SLIME_BLOCK;
      case "blood_congealed_slime" -> TinkerWorld.congealedSlime.get(SlimeType.EARTH);
      case "blood_fluid" -> TinkerFluids.earthSlime.getBlock();
      // lavawood removal
      case "lavawood" -> TinkerMaterials.blazewood.get();
      case "lavawood_slab" -> TinkerMaterials.blazewood.getSlab();
      case "lavawood_stairs" -> TinkerMaterials.blazewood.getStairs();
      // migrate mud bricks to vanilla
      case "mud_bricks" -> Blocks.MUD_BRICKS;
      case "mud_bricks_slab" -> Blocks.MUD_BRICK_SLAB;
      case "mud_bricks_stairs" -> Blocks.MUD_BRICK_STAIRS;
      default -> null;
    };
  }

  /** Handles missing mappings of all types */
  private static void missingMappings(MissingMappingsEvent event) {
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.BLOCK, name -> {
      // no item form so we handle it directly
      if (name.equals("blood_fluid")) {
        return TinkerFluids.earthSlime.getBlock();
      }
      return missingBlock(name);
    });
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.ITEM, name -> switch (name) {
      // slings are modifiers now
      case "earth_slime_sling" -> TinkerTools.earthStaff.get();
      case "sky_slime_sling" -> TinkerTools.skyStaff.get();
      case "ichor_slime_sling" -> TinkerTools.ichorStaff.get();
      case "ender_slime_sling" -> TinkerTools.enderStaff.get();
      // earthslime no longer needed due to forge feature
      case "earth_slime_spawn_egg" -> Items.SLIME_SPAWN_EGG;
      // blood removal
      case "bloodbone" -> TinkerMaterials.venombone.get();
      case "blood_slime_ball" -> Items.SLIME_BALL;
      case "blood_bucket" -> TinkerFluids.earthSlime.asItem();
      case "blood_bottle" -> TinkerFluids.slimeBottle.get(SlimeType.EARTH);
      // ID switched from non-generated to generated
      case "ichor_bottle" -> TinkerFluids.slimeBottle.get(SlimeType.ICHOR);
      // reinforcement rework, bronze was the only type dropped so map to the new type
      case "bronze_reinforcement" -> TinkerModifiers.obsidianReinforcement.get();
      default -> {
        Block block = missingBlock(name);
        yield block == null ? null : block.asItem();
      }
    });
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.FLUID, name -> switch (name) {
      case "blood" -> TinkerFluids.earthSlime.getStill();
      case "flowing_blood" -> TinkerFluids.earthSlime.getFlowing();
      default -> null;
    });
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.ENTITY_TYPE, name -> name.equals("earth_slime") ? EntityType.SLIME : null);
    RegistrationHelper.handleMissingMappings(event, MOD_ID, Registries.MOB_EFFECT, name -> switch (name) {
      case "momentum" -> TinkerModifiers.momentumEffect.get(ToolType.HARVEST);
      case "insatiable" -> TinkerModifiers.insatiableEffect.get(ToolType.MELEE);
      default -> null;
    });
  }


  /* Utils */

  /**
   * Gets a resource location for Tinkers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  public static ResourceLocation getResource(String name) {
    return new ResourceLocation(MOD_ID, name);
  }

  /**
   * Gets a data key for the capability, mainly used for modifier markers
   * @param name  Resource path
   * @return  Location for tinkers
   */
  public static <T> TinkerDataKey<T> createKey(String name) {
    return TinkerDataKey.of(getResource(name));
  }

  /**
   * Gets a data key for the capability, mainly used for modifier markers
   * @param name         Resource path
   * @param constructor  Constructor for compute if absent
   * @return  Location for tinkers
   */
  public static <T> ComputableDataKey<T> createKey(String name, Supplier<T> constructor) {
    return ComputableDataKey.of(getResource(name), constructor);
  }

  /**
   * Returns the given Resource prefixed with tinkers resource location. Use this function instead of hardcoding
   * resource locations.
   */
  public static String resourceString(String res) {
    return String.format("%s:%s", MOD_ID, res);
  }

  /**
   * Prefixes the given unlocalized name with tinkers prefix. Use this when passing unlocalized names for a uniform
   * namespace.
   */
  public static String prefix(String name) {
    return MOD_ID + "." + name.toLowerCase(Locale.US);
  }

  /** Makes a Tinker's description ID */
  public static String makeDescriptionId(String type, String name) {
    return type + "." + MOD_ID + "." + name;
  }

  /**
   * Makes a translation key for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static String makeTranslationKey(String base, String name) {
    return Util.makeTranslationKey(base, getResource(name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base  Base name, such as "block" or "gui"
   * @param name  Object name
   * @return  Translation key
   */
  public static MutableComponent makeTranslation(String base, String name) {
    return Component.translatable(makeTranslationKey(base, name));
  }

  /**
   * Makes a translation text component for the given name
   * @param base       Base name, such as "block" or "gui"
   * @param name       Object name
   * @param arguments  Additional arguments to the translation
   * @return  Translation key
   */
  public static MutableComponent makeTranslation(String base, String name, Object... arguments) {
    return Component.translatable(makeTranslationKey(base, name), arguments);
  }

  /**
   * This function is called in the constructor in some internal classes that are a common target for addons to wrongly extend.
   * These classes will cause issues if blindly used by the addon, and are typically trivial for the addon to implement
   * the parts they need if they just put in some effort understanding the code they are copying.
   *
   * As a reminder for addon devs, anything that is not in the library package can and will change arbitrarily. If you need to use a feature outside library, request it on our github.
   * @param self  Class to validate
   */
  public static void sealTinkersClass(Object self, String base, String solution) {
    // note for future maintainers: this does not use Java 9's sealed classes as unless you use modules those are restricted to the same package.
    // Dumb restriction but not like we can change it.
    String name = self.getClass().getName();
    if (!name.startsWith("dev.tocraft.thconstruct.")) {
      throw new IllegalStateException(base + " being extended from invalid package " + name + ". " + solution);
    }
  }
}

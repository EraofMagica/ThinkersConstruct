package dev.tocraft.thconstruct.common;

import com.mojang.serialization.Codec;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistries.Keys;
import dev.tocraft.eomantle.item.BlockTooltipItem;
import dev.tocraft.eomantle.item.TooltipItem;
import dev.tocraft.eomantle.registration.deferred.BlockEntityTypeDeferredRegister;
import dev.tocraft.eomantle.registration.deferred.EntityTypeDeferredRegister;
import dev.tocraft.eomantle.registration.deferred.FluidDeferredRegister;
import dev.tocraft.eomantle.registration.deferred.MenuTypeDeferredRegister;
import dev.tocraft.eomantle.registration.deferred.SynchronizedDeferredRegister;
import dev.tocraft.thconstruct.common.registration.BlockDeferredRegisterExtension;
import dev.tocraft.thconstruct.common.registration.ConfiguredFeatureDeferredRegister;
import dev.tocraft.thconstruct.common.registration.EnumDeferredRegister;
import dev.tocraft.thconstruct.common.registration.ItemDeferredRegisterExtension;
import dev.tocraft.thconstruct.common.registration.PlacedFeatureDeferredRegister;
import dev.tocraft.thconstruct.library.recipe.TinkerRecipeTypes;
import dev.tocraft.thconstruct.shared.TinkerCommons;
import dev.tocraft.thconstruct.shared.block.SlimeType;

import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Contains base helpers for all Tinker modules. Should not be extended by other mods, this is only for internal usage.
 */
public abstract class TinkerModule {
  protected TinkerModule() {
    dev.tocraft.thconstruct.ThConstruct.sealTinkersClass(this, "TinkerModule", "This is a bug with the mod containing that class, they should create their own deferred registers.");
  }

  // deferred register instances
  // gameplay singleton
  protected static final BlockDeferredRegisterExtension BLOCKS = new BlockDeferredRegisterExtension(dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final ItemDeferredRegisterExtension ITEMS = new ItemDeferredRegisterExtension(dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final FluidDeferredRegister FLUIDS = new FluidDeferredRegister(dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final EnumDeferredRegister<MobEffect> MOB_EFFECTS = new EnumDeferredRegister<>(Registries.MOB_EFFECT, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<ParticleType<?>> PARTICLE_TYPES = SynchronizedDeferredRegister.create(ForgeRegistries.PARTICLE_TYPES, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS = SynchronizedDeferredRegister.create(Keys.ENTITY_DATA_SERIALIZERS, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  // gameplay instances
  protected static final BlockEntityTypeDeferredRegister BLOCK_ENTITIES = new BlockEntityTypeDeferredRegister(dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final EntityTypeDeferredRegister ENTITIES = new EntityTypeDeferredRegister(dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final MenuTypeDeferredRegister MENUS = new MenuTypeDeferredRegister(dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  // datapacks
  protected static final SynchronizedDeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = SynchronizedDeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<Codec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIERS = SynchronizedDeferredRegister.create(Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<LootItemConditionType> LOOT_CONDITIONS = SynchronizedDeferredRegister.create(Registries.LOOT_CONDITION_TYPE, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<LootItemFunctionType> LOOT_FUNCTIONS = SynchronizedDeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final SynchronizedDeferredRegister<LootPoolEntryType> LOOT_ENTRIES = SynchronizedDeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  // worldgen
  protected static final PlacedFeatureDeferredRegister PLACED_FEATURES = new PlacedFeatureDeferredRegister(dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  protected static final ConfiguredFeatureDeferredRegister CONFIGURED_FEATURES = new ConfiguredFeatureDeferredRegister(dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  //

  /** Creative tab for items that do not fit in another tab */
  @SuppressWarnings("WeakerAccess")
  public static final CreativeModeTab TAB_GENERAL = CreativeModeTab.builder().title(Component.literal("itemGroup.tconstruct.general")).icon(() -> new ItemStack(TinkerCommons.slimeball.get(SlimeType.SKY))).build();

  // base item properties
  protected static final Item.Properties HIDDEN_PROPS = new Item.Properties();
  protected static final Item.Properties GENERAL_PROPS = new Item.Properties().tab(TAB_GENERAL);
  protected static final Function<Block,? extends BlockItem> HIDDEN_BLOCK_ITEM = (b) -> new BlockItem(b, HIDDEN_PROPS);
  protected static final Function<Block,? extends BlockItem> GENERAL_BLOCK_ITEM = (b) -> new BlockItem(b, GENERAL_PROPS);
  protected static final Function<Block,? extends BlockItem> GENERAL_TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GENERAL_PROPS);
  protected static final Supplier<Item> TOOLTIP_ITEM = () -> new TooltipItem(GENERAL_PROPS);

  /** Called during construction to initialize the registers for this mod */
  public static void initRegisters() {
    IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
    // gameplay singleton
    BLOCKS.register(bus);
    ITEMS.register(bus);
    FLUIDS.register(bus);
    MOB_EFFECTS.register(bus);
    PARTICLE_TYPES.register(bus);
    DATA_SERIALIZERS.register(bus);
    // gameplay instance
    BLOCK_ENTITIES.register(bus);
    ENTITIES.register(bus);
    MENUS.register(bus);
    // datapacks
    RECIPE_SERIALIZERS.register(bus);
    GLOBAL_LOOT_MODIFIERS.register(bus);
    LOOT_CONDITIONS.register(bus);
    LOOT_FUNCTIONS.register(bus);
    LOOT_ENTRIES.register(bus);
    TinkerRecipeTypes.init(bus);
    // worldgen
    CONFIGURED_FEATURES.register(bus);
    PLACED_FEATURES.register(bus);
  }

  /**
   * We use this builder to ensure that our blocks all have the most important properties set.
   * This way it'll stick out if a block doesn't have a tooltype or sound set.
   * It may be a bit less clear at first, since the actual builder methods tell you what each value means,
   * but as long as we don't statically import the enums it should be just as readable.
   */
  protected static BlockBehaviour.Properties builder(NoteBlockInstrument instrument, SoundType soundType) {
    return Block.Properties.of().instrument(instrument).sound(soundType);
  }

  /** Same as above, but with a color */
  protected static BlockBehaviour.Properties builder(NoteBlockInstrument instrument, MapColor color, SoundType soundType) {
    return Block.Properties.of().instrument(instrument).mapColor(color).sound(soundType);
  }

  /** Builder that pre-supplies metal properties */
  protected static BlockBehaviour.Properties metalBuilder(MapColor color) {
    return builder(NoteBlockInstrument.BASEDRUM, color, SoundType.METAL).requiresCorrectToolForDrops().strength(5.0f);
  }

  /** Builder that pre-supplies glass properties */
  protected static BlockBehaviour.Properties glassBuilder(MapColor color) {
    return builder(NoteBlockInstrument.HAT, color, SoundType.GLASS)
      .strength(0.3F).noOcclusion().isValidSpawn(Blocks::never)
      .isRedstoneConductor(Blocks::never).isSuffocating(Blocks::never).isViewBlocking(Blocks::never);
  }

  /** Builder that pre-supplies glass properties */
  protected static BlockBehaviour.Properties woodBuilder(MapColor color) {
    return builder(NoteBlockInstrument.BASS, color, SoundType.WOOD).requiresCorrectToolForDrops().strength(2.0F, 7.0F);
  }

  /**
   * Creates a Tinkers Construct resource location
   * @param path  Resource path
   * @return  Tinkers Construct resource location
   */
  protected static ResourceLocation resource(String path) {
    return dev.tocraft.thconstruct.ThConstruct.getResource(path);
  }
}

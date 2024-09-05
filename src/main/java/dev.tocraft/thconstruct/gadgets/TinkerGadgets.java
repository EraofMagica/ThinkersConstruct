package dev.tocraft.thconstruct.gadgets;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.Material;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.RegistryObject;
import org.apache.logging.log4j.Logger;
import dev.tocraft.eomantle.item.BlockTooltipItem;
import dev.tocraft.eomantle.registration.object.EnumObject;
import dev.tocraft.eomantle.registration.object.ItemObject;
import dev.tocraft.eomantle.util.SupplierCreativeTab;
import dev.tocraft.thconstruct.common.TinkerModule;
import dev.tocraft.thconstruct.gadgets.block.FoodCakeBlock;
import dev.tocraft.thconstruct.gadgets.block.PunjiBlock;
import dev.tocraft.thconstruct.gadgets.capability.PiggybackCapability;
import dev.tocraft.thconstruct.gadgets.data.GadgetRecipeProvider;
import dev.tocraft.thconstruct.gadgets.entity.EFLNEntity;
import dev.tocraft.thconstruct.gadgets.entity.FancyItemFrameEntity;
import dev.tocraft.thconstruct.gadgets.entity.FrameType;
import dev.tocraft.thconstruct.gadgets.entity.GlowballEntity;
import dev.tocraft.thconstruct.gadgets.entity.shuriken.FlintShurikenEntity;
import dev.tocraft.thconstruct.gadgets.entity.shuriken.QuartzShurikenEntity;
import dev.tocraft.thconstruct.gadgets.item.EFLNItem;
import dev.tocraft.thconstruct.gadgets.item.FancyItemFrameItem;
import dev.tocraft.thconstruct.gadgets.item.GlowBallItem;
import dev.tocraft.thconstruct.gadgets.item.PiggyBackPackItem;
import dev.tocraft.thconstruct.gadgets.item.PiggyBackPackItem.CarryPotionEffect;
import dev.tocraft.thconstruct.gadgets.item.ShurikenItem;
import dev.tocraft.thconstruct.library.utils.Util;
import dev.tocraft.thconstruct.shared.TinkerFood;
import dev.tocraft.thconstruct.world.block.FoliageType;

import java.util.function.Function;

/**
 * Contains any special tools unrelated to the base tools
 */
@SuppressWarnings("unused")
public final class TinkerGadgets extends TinkerModule {
  /** Tab for all special tools added by the mod */
  public static final CreativeModeTab TAB_GADGETS = new SupplierCreativeTab(dev.tocraft.thconstruct.ThConstruct.MOD_ID, "gadgets", () -> new ItemStack(TinkerGadgets.itemFrame.get(FrameType.CLEAR)));
  static final Logger log = Util.getLogger("tinker_gadgets");

  /*
   * Block base properties
   */
  private static final Item.Properties GADGET_PROPS = new Item.Properties().tab(TAB_GADGETS);
  private static final Item.Properties UNSTACKABLE_PROPS = new Item.Properties().tab(TAB_GADGETS).stacksTo(1);
  private static final Function<Block,? extends BlockItem> DEFAULT_BLOCK_ITEM = (b) -> new BlockItem(b, GADGET_PROPS);
  private static final Function<Block,? extends BlockItem> TOOLTIP_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, GADGET_PROPS);
  private static final Function<Block,? extends BlockItem> UNSTACKABLE_BLOCK_ITEM = (b) -> new BlockTooltipItem(b, UNSTACKABLE_PROPS);

  /*
   * Blocks
   */
  public static final ItemObject<PunjiBlock> punji = BLOCKS.register("punji", () -> new PunjiBlock(builder(Material.PLANT, SoundType.GRASS).strength(3.0F).speedFactor(0.4F).noOcclusion()), TOOLTIP_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<PiggyBackPackItem> piggyBackpack = ITEMS.register("piggy_backpack", () -> new PiggyBackPackItem(new Properties().tab(TinkerGadgets.TAB_GADGETS).stacksTo(16)));
  public static final EnumObject<FrameType,FancyItemFrameItem> itemFrame = ITEMS.registerEnum(FrameType.values(), "item_frame", (type) -> new FancyItemFrameItem(GADGET_PROPS, (world, pos, dir) -> new FancyItemFrameEntity(world, pos, dir, type)));

  // throwballs
  public static final ItemObject<GlowBallItem> glowBall = ITEMS.register("glow_ball", GlowBallItem::new);
  public static final ItemObject<EFLNItem> efln = ITEMS.register("efln_ball", EFLNItem::new);

  // foods
  private static final BlockBehaviour.Properties CAKE = builder(Material.CAKE, SoundType.WOOL).strength(0.5F);
  public static final EnumObject<FoliageType,FoodCakeBlock> cake = BLOCKS.registerEnum(FoliageType.values(), "cake", type -> new FoodCakeBlock(CAKE, TinkerFood.getCake(type)), UNSTACKABLE_BLOCK_ITEM);
  public static final ItemObject<FoodCakeBlock> magmaCake = BLOCKS.register("magma_cake", () -> new FoodCakeBlock(CAKE, TinkerFood.MAGMA_CAKE), UNSTACKABLE_BLOCK_ITEM);

  // Shurikens
  private static final Item.Properties THROWABLE_PROPS = new Item.Properties().stacksTo(16).tab(TAB_GADGETS);
  public static final ItemObject<ShurikenItem> quartzShuriken = ITEMS.register("quartz_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, QuartzShurikenEntity::new));
  public static final ItemObject<ShurikenItem> flintShuriken = ITEMS.register("flint_shuriken", () -> new ShurikenItem(THROWABLE_PROPS, FlintShurikenEntity::new));

  /*
   * Entities
   */
  public static final RegistryObject<EntityType<FancyItemFrameEntity>> itemFrameEntity = ENTITIES.register("fancy_item_frame", () ->
    EntityType.Builder.<FancyItemFrameEntity>of(
      FancyItemFrameEntity::new, MobCategory.MISC)
      .sized(0.5F, 0.5F)
      .setTrackingRange(10)
      .setUpdateInterval(Integer.MAX_VALUE)
      .setCustomClientFactory((spawnEntity, world) -> new FancyItemFrameEntity(TinkerGadgets.itemFrameEntity.get(), world))
      .setShouldReceiveVelocityUpdates(false)
  );
  public static final RegistryObject<EntityType<GlowballEntity>> glowBallEntity = ENTITIES.register("glow_ball", () ->
    EntityType.Builder.<GlowballEntity>of(GlowballEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new GlowballEntity(TinkerGadgets.glowBallEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true)
  );
  public static final RegistryObject<EntityType<EFLNEntity>> eflnEntity = ENTITIES.register("efln_ball", () ->
    EntityType.Builder.<EFLNEntity>of(EFLNEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new EFLNEntity(TinkerGadgets.eflnEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true)
                                                                                           );
  public static final RegistryObject<EntityType<QuartzShurikenEntity>> quartzShurikenEntity = ENTITIES.register("quartz_shuriken", () ->
    EntityType.Builder.<QuartzShurikenEntity>of(QuartzShurikenEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new QuartzShurikenEntity(TinkerGadgets.quartzShurikenEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true)
  );
  public static final RegistryObject<EntityType<FlintShurikenEntity>> flintShurikenEntity = ENTITIES.register("flint_shuriken", () ->
    EntityType.Builder.<FlintShurikenEntity>of(FlintShurikenEntity::new, MobCategory.MISC)
      .sized(0.25F, 0.25F)
      .setTrackingRange(4)
      .setUpdateInterval(10)
      .setCustomClientFactory((spawnEntity, world) -> new FlintShurikenEntity(TinkerGadgets.flintShurikenEntity.get(), world))
      .setShouldReceiveVelocityUpdates(true)
  );

  /*
   * Potions
   */
  public static final RegistryObject<CarryPotionEffect> carryEffect = MOB_EFFECTS.register("carry", CarryPotionEffect::new);

  /*
   * Events
   */
  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    PiggybackCapability.register();
    event.enqueueWork(() -> {
      cake.forEach(block -> ComposterBlock.add(1.0f, block));
      ComposterBlock.add(1.0f, magmaCake.get());
    });
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator datagenerator = event.getGenerator();
    datagenerator.addProvider(event.includeServer(), new GadgetRecipeProvider(datagenerator));
  }
}

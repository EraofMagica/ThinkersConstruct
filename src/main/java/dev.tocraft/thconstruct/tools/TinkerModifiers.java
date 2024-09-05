package dev.tocraft.thconstruct.tools;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleRecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;
import dev.tocraft.eomantle.recipe.helper.LoadableRecipeSerializer;
import dev.tocraft.eomantle.registration.object.EnumObject;
import dev.tocraft.eomantle.registration.object.ItemObject;
import dev.tocraft.thconstruct.common.TinkerEffect;
import dev.tocraft.thconstruct.common.TinkerModule;
import dev.tocraft.thconstruct.common.data.tags.ModifierTagProvider;
import dev.tocraft.thconstruct.library.json.predicate.modifier.ModifierPredicate;
import dev.tocraft.thconstruct.library.json.predicate.modifier.SingleModifierPredicate;
import dev.tocraft.thconstruct.library.json.predicate.modifier.SlotTypeModifierPredicate;
import dev.tocraft.thconstruct.library.json.predicate.modifier.TagModifierPredicate;
import dev.tocraft.thconstruct.library.json.variable.block.BlockVariable;
import dev.tocraft.thconstruct.library.json.variable.block.ConditionalBlockVariable;
import dev.tocraft.thconstruct.library.json.variable.block.StatePropertyVariable;
import dev.tocraft.thconstruct.library.json.variable.entity.AttributeEntityVariable;
import dev.tocraft.thconstruct.library.json.variable.entity.ConditionalEntityVariable;
import dev.tocraft.thconstruct.library.json.variable.entity.EntityEffectLevelVariable;
import dev.tocraft.thconstruct.library.json.variable.entity.EntityLightVariable;
import dev.tocraft.thconstruct.library.json.variable.entity.EntityVariable;
import dev.tocraft.thconstruct.library.json.variable.melee.EntityMeleeVariable;
import dev.tocraft.thconstruct.library.json.variable.melee.MeleeVariable;
import dev.tocraft.thconstruct.library.json.variable.mining.BlockLightVariable;
import dev.tocraft.thconstruct.library.json.variable.mining.BlockMiningSpeedVariable;
import dev.tocraft.thconstruct.library.json.variable.mining.MiningSpeedVariable;
import dev.tocraft.thconstruct.library.json.variable.stat.ConditionalStatVariable;
import dev.tocraft.thconstruct.library.json.variable.stat.EntityConditionalStatVariable;
import dev.tocraft.thconstruct.library.json.variable.tool.ConditionalToolVariable;
import dev.tocraft.thconstruct.library.json.variable.tool.ToolStatVariable;
import dev.tocraft.thconstruct.library.json.variable.tool.ToolVariable;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierManager;
import dev.tocraft.thconstruct.library.modifiers.dynamic.ComposableModifier;
import dev.tocraft.thconstruct.library.modifiers.dynamic.InventoryMenuModifier;
import dev.tocraft.thconstruct.library.modifiers.fluid.ConditionalFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.FluidEffectManager;
import dev.tocraft.thconstruct.library.modifiers.fluid.block.MobEffectCloudFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.block.PlaceBlockFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.block.PotionCloudFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.AddBreathFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.AwardStatFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.CureEffectsFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.DamageFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.FireFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.FreezeFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.MobEffectFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.PotionFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.RemoveEffectFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.fluid.entity.RestoreHungerFluidEffect;
import dev.tocraft.thconstruct.library.modifiers.impl.BasicModifier.TooltipDisplay;
import dev.tocraft.thconstruct.library.modifiers.impl.SingleLevelModifier;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.BlockDamageSourceModule;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.CoverGroundWalkerModule;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.EffectImmunityModule;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.MobDisguiseModule;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ProtectionModule;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ReplaceBlockWalkerModule;
import dev.tocraft.thconstruct.library.modifiers.modules.armor.ToolActionWalkerTransformModule;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.AttributeModule;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.ConditionalStatModule;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.ExtinguishCampfireModule;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.ReduceToolDamageModule;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.RepairModule;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.ShowOffhandModule;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.ToolActionTransformModule;
import dev.tocraft.thconstruct.library.modifiers.modules.behavior.ToolActionsModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.EnchantmentModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.ModifierRequirementsModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.ModifierSlotModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.ModifierTraitModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.RarityModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.SetStatModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.StatBoostModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.SwappableSlotModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.SwappableToolTraitsModule;
import dev.tocraft.thconstruct.library.modifiers.modules.build.VolatileFlagModule;
import dev.tocraft.thconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import dev.tocraft.thconstruct.library.modifiers.modules.combat.KnockbackModule;
import dev.tocraft.thconstruct.library.modifiers.modules.combat.LootingModule;
import dev.tocraft.thconstruct.library.modifiers.modules.combat.MeleeAttributeModule;
import dev.tocraft.thconstruct.library.modifiers.modules.combat.MobEffectModule;
import dev.tocraft.thconstruct.library.modifiers.modules.display.DurabilityBarColorModule;
import dev.tocraft.thconstruct.library.modifiers.modules.mining.ConditionalMiningSpeedModule;
import dev.tocraft.thconstruct.library.modifiers.modules.technical.ArmorLevelModule;
import dev.tocraft.thconstruct.library.modifiers.modules.technical.ArmorStatModule;
import dev.tocraft.thconstruct.library.modifiers.util.DynamicModifier;
import dev.tocraft.thconstruct.library.modifiers.util.ModifierDeferredRegister;
import dev.tocraft.thconstruct.library.modifiers.util.ModifierLevelDisplay;
import dev.tocraft.thconstruct.library.modifiers.util.ModifierLevelDisplay.UniqueForLevels;
import dev.tocraft.thconstruct.library.modifiers.util.StaticModifier;
import dev.tocraft.thconstruct.library.module.ModuleHookMap;
import dev.tocraft.thconstruct.library.recipe.modifiers.ModifierSalvage;
import dev.tocraft.thconstruct.library.recipe.modifiers.adding.IncrementalModifierRecipe;
import dev.tocraft.thconstruct.library.recipe.modifiers.adding.ModifierRecipe;
import dev.tocraft.thconstruct.library.recipe.modifiers.adding.MultilevelModifierRecipe;
import dev.tocraft.thconstruct.library.recipe.modifiers.adding.OverslimeModifierRecipe;
import dev.tocraft.thconstruct.library.recipe.modifiers.adding.SwappableModifierRecipe;
import dev.tocraft.thconstruct.library.recipe.modifiers.severing.AgeableSeveringRecipe;
import dev.tocraft.thconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairKitRecipe;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing.ModifierMaterialRepairRecipe;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing.ModifierRepairCraftingRecipe;
import dev.tocraft.thconstruct.library.recipe.tinkerstation.repairing.ModifierRepairTinkerStationRecipe;
import dev.tocraft.thconstruct.library.recipe.worktable.ModifierSetWorktableRecipe;
import dev.tocraft.thconstruct.library.tools.capability.EntityModifierCapability;
import dev.tocraft.thconstruct.library.tools.capability.PersistentDataCapability;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataCapability;
import dev.tocraft.thconstruct.library.tools.capability.TinkerDataKeys;
import dev.tocraft.thconstruct.library.tools.capability.fluid.TankModule;
import dev.tocraft.thconstruct.library.tools.capability.fluid.ToolTankHelper;
import dev.tocraft.thconstruct.tools.data.EnchantmentToModifierProvider;
import dev.tocraft.thconstruct.tools.data.FluidEffectProvider;
import dev.tocraft.thconstruct.tools.data.ModifierProvider;
import dev.tocraft.thconstruct.tools.data.ModifierRecipeProvider;
import dev.tocraft.thconstruct.tools.entity.FluidEffectProjectile;
import dev.tocraft.thconstruct.tools.item.CreativeSlotItem;
import dev.tocraft.thconstruct.tools.item.DragonScaleItem;
import dev.tocraft.thconstruct.tools.item.ModifierCrystalItem;
import dev.tocraft.thconstruct.tools.modifiers.ModifierLootModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.armor.AmbidextrousModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.armor.BouncyModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.armor.DoubleJumpModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.armor.ReflectingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.armor.ShieldStrapModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.armor.ToolBeltModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.armor.ZoomModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.armor.walker.FlamewakeModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.fluid.BurstingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.fluid.SlurpingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.fluid.SpillingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.fluid.SpittingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.fluid.SplashingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.fluid.WettingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.interaction.BlockingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.interaction.FirestarterModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.interaction.HarvestAbilityModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.interaction.ShearsAbilityModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.interaction.SilkyShearsAbilityModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.ranged.BulkQuiverModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.ranged.CrystalshotModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.ranged.TrickQuiverModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.sling.BonkingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.sling.FlingingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.sling.SpringingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.sling.WarpingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.AutosmeltModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.BucketingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.DuelWieldingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.ExchangingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.GlowingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.MeltingModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.OffhandAttackModifier;
import dev.tocraft.thconstruct.tools.modifiers.ability.tool.ParryingModifier;
import dev.tocraft.thconstruct.tools.modifiers.defense.BlastProtectionModifier;
import dev.tocraft.thconstruct.tools.modifiers.defense.DragonbornModifier;
import dev.tocraft.thconstruct.tools.modifiers.defense.MagicProtectionModifier;
import dev.tocraft.thconstruct.tools.modifiers.defense.MeleeProtectionModifier;
import dev.tocraft.thconstruct.tools.modifiers.defense.ProjectileProtectionModifier;
import dev.tocraft.thconstruct.tools.modifiers.defense.ShulkingModifier;
import dev.tocraft.thconstruct.tools.modifiers.effect.BleedingEffect;
import dev.tocraft.thconstruct.tools.modifiers.effect.MagneticEffect;
import dev.tocraft.thconstruct.tools.modifiers.effect.NoMilkEffect;
import dev.tocraft.thconstruct.tools.modifiers.effect.RepulsiveEffect;
import dev.tocraft.thconstruct.tools.modifiers.loot.ChrysophiliteBonusFunction;
import dev.tocraft.thconstruct.tools.modifiers.loot.ChrysophiliteLootCondition;
import dev.tocraft.thconstruct.tools.modifiers.loot.HasModifierLootCondition;
import dev.tocraft.thconstruct.tools.modifiers.loot.ModifierBonusLootFunction;
import dev.tocraft.thconstruct.tools.modifiers.slotless.CreativeSlotModifier;
import dev.tocraft.thconstruct.tools.modifiers.slotless.DyedModifier;
import dev.tocraft.thconstruct.tools.modifiers.slotless.EmbellishmentModifier;
import dev.tocraft.thconstruct.tools.modifiers.slotless.FarsightedModifier;
import dev.tocraft.thconstruct.tools.modifiers.slotless.NearsightedModifier;
import dev.tocraft.thconstruct.tools.modifiers.slotless.OverslimeModifier;
import dev.tocraft.thconstruct.tools.modifiers.slotless.StatOverrideModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.DamageSpeedTradeModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.general.EnderportingModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.general.OvergrowthModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.general.OverlordModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.general.SolarPoweredModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.general.StoneshieldModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.general.TannedModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.general.TastyModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.harvest.DwarvenModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.harvest.MomentumModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.harvest.SearingModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.harvest.TemperateModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.melee.ConductingModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.melee.DecayModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.melee.EnderferenceModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.melee.InsatiableModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.melee.InvariantModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.melee.LaceratingModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.melee.NecroticModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.ranged.HolyModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.ranged.OlympicModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.BoonOfSssssModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.BreathtakingModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.ChrysophiliteModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.EnderdodgingModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.FirebreathModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.FrosttouchModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.GoldGuardModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.PlagueModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.RevengeModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.SelfDestructiveModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.SelfDestructiveModifier.SelfDestructiveEffect;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.StrongBonesModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.WildfireModifier;
import dev.tocraft.thconstruct.tools.modifiers.traits.skull.WitheredModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.armor.ItemFrameModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.armor.LightspeedArmorModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.armor.SoulSpeedModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.armor.SpringyModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.armor.ThornsModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.general.MagneticModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.melee.FieryModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.melee.PiercingModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.melee.SeveringModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.melee.SweepingEdgeModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.ranged.FreezingModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.ranged.ImpalingModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.ranged.PunchModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.ranged.ScopeModifier;
import dev.tocraft.thconstruct.tools.modifiers.upgrades.ranged.SinistralModifier;
import dev.tocraft.thconstruct.tools.modules.TheOneProbeModule;
import dev.tocraft.thconstruct.tools.modules.armor.DepthProtectionModule;
import dev.tocraft.thconstruct.tools.modules.armor.EnderclearanceModule;
import dev.tocraft.thconstruct.tools.modules.armor.FlameBarrierModule;
import dev.tocraft.thconstruct.tools.modules.armor.KineticModule;
import dev.tocraft.thconstruct.tools.modules.armor.RecurrentProtectionModule;
import dev.tocraft.thconstruct.tools.modules.ranged.RestrictAngleModule;
import dev.tocraft.thconstruct.tools.recipe.ArmorDyeingRecipe;
import dev.tocraft.thconstruct.tools.recipe.CreativeSlotRecipe;
import dev.tocraft.thconstruct.tools.recipe.EnchantmentConvertingRecipe;
import dev.tocraft.thconstruct.tools.recipe.ExtractModifierRecipe;
import dev.tocraft.thconstruct.tools.recipe.ModifierRemovalRecipe;
import dev.tocraft.thconstruct.tools.recipe.ModifierSortingRecipe;
import dev.tocraft.thconstruct.tools.recipe.severing.MooshroomDemushroomingRecipe;
import dev.tocraft.thconstruct.tools.recipe.severing.PlayerBeheadingRecipe;
import dev.tocraft.thconstruct.tools.recipe.severing.SheepShearingRecipe;
import dev.tocraft.thconstruct.tools.recipe.severing.SnowGolemBeheadingRecipe;
import dev.tocraft.thconstruct.tools.stats.ToolType;

import static dev.tocraft.thconstruct.ThConstruct.getResource;
import static dev.tocraft.thconstruct.tools.TinkerTools.TAB_TOOLS;

/**
 * Contains modifiers and the items or blocks used to craft modifiers
 */
@SuppressWarnings("unused")
public final class TinkerModifiers extends TinkerModule {
  private static final ModifierDeferredRegister MODIFIERS = ModifierDeferredRegister.create(dev.tocraft.thconstruct.ThConstruct.MOD_ID);

  public TinkerModifiers() {
    ModifierManager.INSTANCE.init();
    DynamicModifier.init();
    FluidEffectManager.INSTANCE.init();
    MODIFIERS.register(FMLJavaModLoadingContext.get().getModEventBus());
    TinkerDataKeys.init();
  }

  /*
   * Blocks
   */
  // material
  public static final ItemObject<Block> silkyJewelBlock = BLOCKS.register("silky_jewel_block", metalBuilder(MapColor.GOLD), HIDDEN_BLOCK_ITEM);

  /*
   * Items
   */
  public static final ItemObject<Item> silkyCloth = ITEMS.register("silky_cloth", GENERAL_PROPS);
  public static final ItemObject<Item> silkyJewel = ITEMS.register("silky_jewel", HIDDEN_PROPS);
  public static final ItemObject<Item> dragonScale = ITEMS.register("dragon_scale", () -> new DragonScaleItem(new Item.Properties().tab(TAB_GENERAL).rarity(Rarity.RARE)));
  // durability reinforcements
  public static final ItemObject<Item> emeraldReinforcement = ITEMS.register("emerald_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> slimesteelReinforcement = ITEMS.register("slimesteel_reinforcement", GENERAL_PROPS);
  // armor reinforcements
  public static final ItemObject<Item> ironReinforcement = ITEMS.register("iron_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> searedReinforcement = ITEMS.register("seared_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> goldReinforcement = ITEMS.register("gold_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> cobaltReinforcement = ITEMS.register("cobalt_reinforcement", GENERAL_PROPS);
  public static final ItemObject<Item> obsidianReinforcement = ITEMS.register("obsidian_reinforcement", GENERAL_PROPS);
  // special
  public static final ItemObject<Item> modifierCrystal = ITEMS.register("modifier_crystal", () -> new ModifierCrystalItem(new Item.Properties().tab(TAB_TOOLS).stacksTo(16)));
  public static final ItemObject<Item> creativeSlotItem = ITEMS.register("creative_slot", () -> new CreativeSlotItem(new Item.Properties().tab(TAB_TOOLS)));

  // entity
  public static final RegistryObject<EntityType<FluidEffectProjectile>> fluidSpitEntity = ENTITIES.register("fluid_spit", () ->
    EntityType.Builder.<FluidEffectProjectile>of(FluidEffectProjectile::new, MobCategory.MISC).sized(0.25F, 0.25F).clientTrackingRange(4).updateInterval(10).setShouldReceiveVelocityUpdates(false));

  /*
   * Modifiers
   */
  public static final StaticModifier<OverslimeModifier> overslime = MODIFIERS.register("overslime", OverslimeModifier::new);
  public static final StaticModifier<MagneticModifier> magnetic = MODIFIERS.register("magnetic", MagneticModifier::new);
  public static final StaticModifier<FarsightedModifier> farsighted = MODIFIERS.register("farsighted", FarsightedModifier::new);
  public static final StaticModifier<NearsightedModifier> nearsighted = MODIFIERS.register("nearsighted", NearsightedModifier::new);

  // weapon
  public static final DynamicModifier<Modifier> knockback = MODIFIERS.registerDynamic("knockback");
  public static final DynamicModifier<Modifier> padded = MODIFIERS.registerDynamic("padded");
  public static final StaticModifier<FieryModifier> fiery = MODIFIERS.register("fiery", FieryModifier::new);
  public static final StaticModifier<SeveringModifier> severing = MODIFIERS.register("severing", SeveringModifier::new);
  public static final StaticModifier<ReflectingModifier> reflecting = MODIFIERS.register("reflecting", ReflectingModifier::new);

  // damage boost
  public static final StaticModifier<PiercingModifier> piercing = MODIFIERS.register("piercing", PiercingModifier::new);
  public static final StaticModifier<SweepingEdgeModifier> sweeping = MODIFIERS.register("sweeping_edge", SweepingEdgeModifier::new);

  // ranged
  public static final StaticModifier<PunchModifier> punch = MODIFIERS.register("punch", PunchModifier::new);
  public static final StaticModifier<ImpalingModifier> impaling = MODIFIERS.register("impaling", ImpalingModifier::new);
  public static final StaticModifier<FreezingModifier> freezing = MODIFIERS.register("freezing", FreezingModifier::new);
  public static final StaticModifier<BulkQuiverModifier> bulkQuiver = MODIFIERS.register("bulk_quiver", BulkQuiverModifier::new);
  public static final StaticModifier<TrickQuiverModifier> trickQuiver = MODIFIERS.register("trick_quiver", TrickQuiverModifier::new);
  public static final StaticModifier<CrystalshotModifier> crystalshot = MODIFIERS.register("crystalshot", CrystalshotModifier::new);
  public static final StaticModifier<Modifier> multishot = MODIFIERS.register("multishot", Modifier::new);
  public static final StaticModifier<SinistralModifier> sinistral = MODIFIERS.register("sinistral", SinistralModifier::new);
  public static final StaticModifier<ScopeModifier> scope = MODIFIERS.register("scope", ScopeModifier::new);

  // armor
  // protection
  public static final StaticModifier<MeleeProtectionModifier> meleeProtection = MODIFIERS.register("melee_protection", MeleeProtectionModifier::new);
  public static final StaticModifier<BlastProtectionModifier> blastProtection = MODIFIERS.register("blast_protection", BlastProtectionModifier::new);
  public static final StaticModifier<MagicProtectionModifier> magicProtection = MODIFIERS.register("magic_protection", MagicProtectionModifier::new);
  public static final StaticModifier<ProjectileProtectionModifier> projectileProtection = MODIFIERS.register("projectile_protection", ProjectileProtectionModifier::new);
  public static final StaticModifier<ShulkingModifier> shulking = MODIFIERS.register("shulking", ShulkingModifier::new);
  public static final StaticModifier<DragonbornModifier> dragonborn = MODIFIERS.register("dragonborn", DragonbornModifier::new);
  // general
  public static final DynamicModifier<Modifier> golden = MODIFIERS.registerDynamic("golden", Modifier.class);
  public static final StaticModifier<EmbellishmentModifier> embellishment = MODIFIERS.register("embellishment", EmbellishmentModifier::new);
  public static final StaticModifier<DyedModifier> dyed = MODIFIERS.register("dyed", DyedModifier::new);
  // counterattack
  public static final StaticModifier<ThornsModifier> thorns = MODIFIERS.register("thorns", ThornsModifier::new);
  public static final StaticModifier<SpringyModifier> springy = MODIFIERS.register("springy", SpringyModifier::new);
  // helmet
  public static final StaticModifier<ItemFrameModifier> itemFrame = MODIFIERS.register("item_frame", ItemFrameModifier::new);
  public static final StaticModifier<ZoomModifier> zoom = MODIFIERS.register("zoom", ZoomModifier::new);
  public static final StaticModifier<SlurpingModifier> slurping = MODIFIERS.register("slurping", SlurpingModifier::new);
  // chestplate
  public static final StaticModifier<AmbidextrousModifier> ambidextrous = MODIFIERS.register("ambidextrous", AmbidextrousModifier::new);
  // leggings
  public static final StaticModifier<ShieldStrapModifier> shieldStrap = MODIFIERS.register("shield_strap", ShieldStrapModifier::new);
  public static final StaticModifier<WettingModifier> wetting = MODIFIERS.register("wetting", WettingModifier::new);

  // boots
  public static final StaticModifier<SoulSpeedModifier> soulspeed = MODIFIERS.register("soulspeed", SoulSpeedModifier::new);
  public static final StaticModifier<LightspeedArmorModifier> lightspeedArmor = MODIFIERS.register("lightspeed_armor", LightspeedArmorModifier::new);
  public static final StaticModifier<DoubleJumpModifier> doubleJump = MODIFIERS.register("double_jump", DoubleJumpModifier::new);
  public static final StaticModifier<Modifier> bouncy = MODIFIERS.register("bouncy", BouncyModifier::new);
  public static final StaticModifier<FlamewakeModifier> flamewake = MODIFIERS.register("flamewake", FlamewakeModifier::new);

  // abilities
  public static final DynamicModifier<Modifier> unbreakable = MODIFIERS.registerDynamic("unbreakable");
  // weapon
  public static final StaticModifier<DuelWieldingModifier> dualWielding = MODIFIERS.register("dual_wielding", DuelWieldingModifier::new);
  // harvest
  public static final DynamicModifier<Modifier> silky = MODIFIERS.registerDynamic("silky", Modifier.class);
  public static final StaticModifier<AutosmeltModifier> autosmelt = MODIFIERS.register("autosmelt", AutosmeltModifier::new);
  public static final StaticModifier<Modifier> expanded = MODIFIERS.register("expanded", Modifier::new);
  public static final StaticModifier<ExchangingModifier> exchanging = MODIFIERS.register("exchanging", ExchangingModifier::new);

  // fluid abilities
  public static final StaticModifier<Modifier> tankHandler = MODIFIERS.register("tank_handler", () -> ModuleHookMap.builder().addModule(new TankModule(ToolTankHelper.TANK_HELPER)).modifier().levelDisplay(ModifierLevelDisplay.NO_LEVELS).tooltipDisplay(TooltipDisplay.NEVER).build());
  public static final StaticModifier<MeltingModifier> melting = MODIFIERS.register("melting", MeltingModifier::new);
  public static final StaticModifier<BucketingModifier> bucketing = MODIFIERS.register("bucketing", BucketingModifier::new);
  public static final StaticModifier<SpillingModifier> spilling = MODIFIERS.register("spilling", SpillingModifier::new);
  public static final StaticModifier<SpittingModifier> spitting = MODIFIERS.register("spitting", SpittingModifier::new);
  public static final StaticModifier<BurstingModifier> bursting = MODIFIERS.register("bursting", BurstingModifier::new);
  public static final StaticModifier<SplashingModifier> splashing = MODIFIERS.register("splashing", SplashingModifier::new);
  
  // right click abilities
  public static final StaticModifier<GlowingModifier> glowing = MODIFIERS.register("glowing", GlowingModifier::new);
  public static final StaticModifier<FirestarterModifier> firestarter = MODIFIERS.register("firestarter", () -> new FirestarterModifier(Modifier.DEFAULT_PRIORITY));
  public static final StaticModifier<SingleLevelModifier> fireprimer = MODIFIERS.register("fireprimer", SingleLevelModifier::new);
  public static final StaticModifier<BlockingModifier> blocking = MODIFIERS.register("blocking", BlockingModifier::new);
  public static final StaticModifier<ParryingModifier> parrying = MODIFIERS.register("parrying", ParryingModifier::new);
  // slings
  public static final StaticModifier<FlingingModifier> flinging = MODIFIERS.register("flinging", FlingingModifier::new);
  public static final StaticModifier<SpringingModifier> springing = MODIFIERS.register("springing", SpringingModifier::new);
  public static final StaticModifier<BonkingModifier> bonking = MODIFIERS.register("bonking", BonkingModifier::new);
  public static final StaticModifier<WarpingModifier> warping = MODIFIERS.register("warping", WarpingModifier::new);


  // internal abilities
  public static final StaticModifier<ShearsAbilityModifier> shears = MODIFIERS.register("shears", () -> new ShearsAbilityModifier(0, 70));
  public static final StaticModifier<SilkyShearsAbilityModifier> silkyShears = MODIFIERS.register("silky_shears", () -> new SilkyShearsAbilityModifier(0, 70));
  public static final StaticModifier<SilkyShearsAbilityModifier> aoeSilkyShears = MODIFIERS.register("silky_aoe_shears", () -> new SilkyShearsAbilityModifier(1, 70));
  public static final StaticModifier<HarvestAbilityModifier> harvest = MODIFIERS.register("harvest", () -> new HarvestAbilityModifier(70));
  public static final StaticModifier<OffhandAttackModifier> offhandAttack = MODIFIERS.register("offhand_attack", OffhandAttackModifier::new);

  // creative
  public static final StaticModifier<CreativeSlotModifier> creativeSlot = MODIFIERS.register("creative_slot", CreativeSlotModifier::new);
  public static final StaticModifier<StatOverrideModifier> statOverride = MODIFIERS.register("stat_override", StatOverrideModifier::new);

  // traits - tier 1
  public static final StaticModifier<DamageSpeedTradeModifier> jagged = MODIFIERS.register("jagged", () -> new DamageSpeedTradeModifier(0.005f));
  public static final StaticModifier<DamageSpeedTradeModifier> stonebound = MODIFIERS.register("stonebound", () -> new DamageSpeedTradeModifier(-0.005f));
  // traits - tier 1 nether
  public static final StaticModifier<NecroticModifier> necrotic = MODIFIERS.register("necrotic", NecroticModifier::new);
  // traits - tier 1 nether
  public static final StaticModifier<EnderferenceModifier> enderference = MODIFIERS.register("enderference", EnderferenceModifier::new);
  // traits - tier 1 bindings
  public static final StaticModifier<TannedModifier> tanned = MODIFIERS.register("tanned", TannedModifier::new);
  public static final StaticModifier<SolarPoweredModifier> solarPowered = MODIFIERS.register("solar_powered", SolarPoweredModifier::new);
  // traits - tier 2
  public static final StaticModifier<SearingModifier> searing = MODIFIERS.register("searing", SearingModifier::new);
  public static final StaticModifier<DwarvenModifier> dwarven = MODIFIERS.register("dwarven", DwarvenModifier::new);
  public static final StaticModifier<OvergrowthModifier> overgrowth = MODIFIERS.register("overgrowth", OvergrowthModifier::new);
  // traits - tier 3
  public static final StaticModifier<LaceratingModifier> lacerating = MODIFIERS.register("lacerating", LaceratingModifier::new);
  public static final StaticModifier<TastyModifier> tasty = MODIFIERS.register("tasty", TastyModifier::new);
  // traits - tier 4
  public static final StaticModifier<OverlordModifier> overlord = MODIFIERS.register("overlord", OverlordModifier::new);
  public static final StaticModifier<MomentumModifier> momentum = MODIFIERS.register("momentum", MomentumModifier::new);
  public static final StaticModifier<InsatiableModifier> insatiable = MODIFIERS.register("insatiable", InsatiableModifier::new);
  public static final StaticModifier<ConductingModifier> conducting = MODIFIERS.register("conducting", ConductingModifier::new);
  // traits - tier 5
  public static final StaticModifier<EnderportingModifier> enderporting = MODIFIERS.register("enderporting", EnderportingModifier::new);

  // traits - mod compat tier 2
  public static final StaticModifier<StoneshieldModifier> stoneshield = MODIFIERS.register("stoneshield", StoneshieldModifier::new);
  public static final StaticModifier<HolyModifier> holy = MODIFIERS.register("holy", HolyModifier::new);
  public static final StaticModifier<OlympicModifier> olympic = MODIFIERS.register("olympic", OlympicModifier::new);
  // traits - mod compat tier 3
  public static final StaticModifier<TemperateModifier> temperate = MODIFIERS.register("temperate", TemperateModifier::new);
  public static final StaticModifier<InvariantModifier> invariant = MODIFIERS.register("invariant", InvariantModifier::new);
  public static final StaticModifier<DecayModifier> decay = MODIFIERS.register("decay", DecayModifier::new);
  public static final StaticModifier<Modifier> overworked = MODIFIERS.register("overworked", Modifier::new);
  // experienced is also an upgrade

  // traits - slimeskull
  public static final StaticModifier<SelfDestructiveModifier> selfDestructive = MODIFIERS.register("self_destructive", SelfDestructiveModifier::new);
  public static final StaticModifier<EnderdodgingModifier> enderdodging = MODIFIERS.register("enderdodging", EnderdodgingModifier::new);
  public static final StaticModifier<StrongBonesModifier> strongBones = MODIFIERS.register("strong_bones", StrongBonesModifier::new);
  public static final StaticModifier<FrosttouchModifier> frosttouch = MODIFIERS.register("frosttouch", FrosttouchModifier::new);
  public static final StaticModifier<WitheredModifier> withered = MODIFIERS.register("withered", WitheredModifier::new);
  public static final StaticModifier<BoonOfSssssModifier> boonOfSssss = MODIFIERS.register("boon_of_sssss", BoonOfSssssModifier::new);
  public static final StaticModifier<WildfireModifier> wildfire = MODIFIERS.register("wildfire", WildfireModifier::new);
  public static final StaticModifier<PlagueModifier> plague = MODIFIERS.register("plague", PlagueModifier::new);
  public static final StaticModifier<BreathtakingModifier> breathtaking = MODIFIERS.register("breathtaking", BreathtakingModifier::new);
  public static final StaticModifier<FirebreathModifier> firebreath = MODIFIERS.register("firebreath", FirebreathModifier::new);
  public static final StaticModifier<ChrysophiliteModifier> chrysophilite = MODIFIERS.register("chrysophilite", ChrysophiliteModifier::new);
  public static final StaticModifier<GoldGuardModifier> goldGuard = MODIFIERS.register("gold_guard", GoldGuardModifier::new);
  public static final StaticModifier<RevengeModifier> revenge = MODIFIERS.register("revenge", RevengeModifier::new);
  

  /*
   * Internal effects
   */
  public static final RegistryObject<BleedingEffect> bleeding = MOB_EFFECTS.register("bleeding", BleedingEffect::new);
  public static final RegistryObject<MagneticEffect> magneticEffect = MOB_EFFECTS.register("magnetic", MagneticEffect::new);
  public static final RegistryObject<RepulsiveEffect> repulsiveEffect = MOB_EFFECTS.register("repulsive", RepulsiveEffect::new);
  public static final RegistryObject<TinkerEffect> enderferenceEffect = MOB_EFFECTS.register("enderference", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0x8F648F, true));
  public static final RegistryObject<TinkerEffect> teleportCooldownEffect = MOB_EFFECTS.register("teleport_cooldown", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xCC00FA, true));
  public static final RegistryObject<TinkerEffect> fireballCooldownEffect = MOB_EFFECTS.register("fireball_cooldown", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xFC9600, true));
  public static final RegistryObject<TinkerEffect> calcifiedEffect = MOB_EFFECTS.register("calcified", () -> new NoMilkEffect(MobEffectCategory.BENEFICIAL, -1, true));
  public static final RegistryObject<TinkerEffect> selfDestructiveEffect = MOB_EFFECTS.register("self_destructing", SelfDestructiveEffect::new);
  public static final RegistryObject<TinkerEffect> pierceEffect = MOB_EFFECTS.register("pierce", () -> new NoMilkEffect(MobEffectCategory.HARMFUL, 0xD1D37A, true).addAttributeModifier(Attributes.ARMOR, "cd45be7c-c86f-4a7e-813b-42a44a054f44", -1, Operation.ADDITION));
  // markers
  public static final EnumObject<ToolType,TinkerEffect> momentumEffect = MOB_EFFECTS.registerEnum("momentum", ToolType.NO_MELEE, type -> new NoMilkEffect(MobEffectCategory.BENEFICIAL, 0x60496b, true));
  public static final EnumObject<ToolType,TinkerEffect> insatiableEffect = MOB_EFFECTS.registerEnum("insatiable", new ToolType[] {ToolType.MELEE, ToolType.RANGED, ToolType.ARMOR}, type -> {
    TinkerEffect effect = new NoMilkEffect(MobEffectCategory.BENEFICIAL, 0x9261cc, true);
    if (type == ToolType.ARMOR) {
      effect.addAttributeModifier(Attributes.ATTACK_DAMAGE, "cc6904f7-674a-4e6a-b992-4f3cb8edfef4", 1, AttributeModifier.Operation.ADDITION);
    }
    return effect;
  });

  /*
   * Recipes
   */
  public static final RegistryObject<RecipeSerializer<ModifierRecipe>> modifierSerializer = RECIPE_SERIALIZERS.register("modifier", () -> LoadableRecipeSerializer.of(ModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<IncrementalModifierRecipe>> incrementalModifierSerializer = RECIPE_SERIALIZERS.register("incremental_modifier", () -> LoadableRecipeSerializer.of(IncrementalModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<SwappableModifierRecipe>> swappableModifierSerializer = RECIPE_SERIALIZERS.register("swappable_modifier", () -> LoadableRecipeSerializer.of(SwappableModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<MultilevelModifierRecipe>> multilevelModifierSerializer = RECIPE_SERIALIZERS.register("multilevel_modifier", () -> LoadableRecipeSerializer.of(MultilevelModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<OverslimeModifierRecipe>> overslimeSerializer = RECIPE_SERIALIZERS.register("overslime_modifier", () -> LoadableRecipeSerializer.of(OverslimeModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierSalvage>> modifierSalvageSerializer = RECIPE_SERIALIZERS.register("modifier_salvage", () -> LoadableRecipeSerializer.of(ModifierSalvage.LOADER));
  public static final RegistryObject<RecipeSerializer<ArmorDyeingRecipe>> armorDyeingSerializer = RECIPE_SERIALIZERS.register("armor_dyeing_modifier", () -> LoadableRecipeSerializer.of(ArmorDyeingRecipe.LOADER));
  public static final RegistryObject<SimpleRecipeSerializer<CreativeSlotRecipe>> creativeSlotSerializer = RECIPE_SERIALIZERS.register("creative_slot_modifier", () -> new SimpleRecipeSerializer<>(CreativeSlotRecipe::new));
  // modifiers
  public static final RegistryObject<RecipeSerializer<ModifierRepairTinkerStationRecipe>> modifierRepair = RECIPE_SERIALIZERS.register("modifier_repair", () -> LoadableRecipeSerializer.of(ModifierRepairTinkerStationRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierRepairCraftingRecipe>> craftingModifierRepair = RECIPE_SERIALIZERS.register("crafting_modifier_repair", () -> LoadableRecipeSerializer.of(ModifierRepairCraftingRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierMaterialRepairRecipe>> modifierMaterialRepair = RECIPE_SERIALIZERS.register("modifier_material_repair", () -> LoadableRecipeSerializer.of(ModifierMaterialRepairRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierMaterialRepairKitRecipe>> craftingModifierMaterialRepair = RECIPE_SERIALIZERS.register("crafting_modifier_material_repair", () -> LoadableRecipeSerializer.of(ModifierMaterialRepairKitRecipe.LOADER));
  // worktable
  public static final RegistryObject<RecipeSerializer<ModifierRemovalRecipe>> removeModifierSerializer = RECIPE_SERIALIZERS.register("remove_modifier", () -> LoadableRecipeSerializer.of(ModifierRemovalRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ExtractModifierRecipe>> extractModifierSerializer = RECIPE_SERIALIZERS.register("extract_modifier", () -> LoadableRecipeSerializer.of(ExtractModifierRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierSortingRecipe>> modifierSortingSerializer = RECIPE_SERIALIZERS.register("modifier_sorting", () -> LoadableRecipeSerializer.of(ModifierSortingRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<ModifierSetWorktableRecipe>> modifierSetWorktableSerializer = RECIPE_SERIALIZERS.register("modifier_set_worktable", () -> LoadableRecipeSerializer.of(ModifierSetWorktableRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<EnchantmentConvertingRecipe>> enchantmentConvertingSerializer = RECIPE_SERIALIZERS.register("enchantment_converting", () -> LoadableRecipeSerializer.of(EnchantmentConvertingRecipe.LOADER));

  // severing
  public static final RegistryObject<RecipeSerializer<SeveringRecipe>> severingSerializer = RECIPE_SERIALIZERS.register("severing", () -> LoadableRecipeSerializer.of(SeveringRecipe.LOADER));
  public static final RegistryObject<RecipeSerializer<AgeableSeveringRecipe>> ageableSeveringSerializer = RECIPE_SERIALIZERS.register("ageable_severing", () -> LoadableRecipeSerializer.of(AgeableSeveringRecipe.LOADER));
  // special severing
  public static final RegistryObject<SimpleRecipeSerializer<PlayerBeheadingRecipe>> playerBeheadingSerializer = RECIPE_SERIALIZERS.register("player_beheading", () -> new SimpleRecipeSerializer<>(PlayerBeheadingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<SnowGolemBeheadingRecipe>> snowGolemBeheadingSerializer = RECIPE_SERIALIZERS.register("snow_golem_beheading", () -> new SimpleRecipeSerializer<>(SnowGolemBeheadingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<MooshroomDemushroomingRecipe>> mooshroomDemushroomingSerializer = RECIPE_SERIALIZERS.register("mooshroom_demushrooming", () -> new SimpleRecipeSerializer<>(MooshroomDemushroomingRecipe::new));
  public static final RegistryObject<SimpleRecipeSerializer<SheepShearingRecipe>> sheepShearing = RECIPE_SERIALIZERS.register("sheep_shearing", () -> new SimpleRecipeSerializer<>(SheepShearingRecipe::new));

  /**
   * Loot
   */
  public static final RegistryObject<Codec<ModifierLootModifier>> modifierLootModifier = GLOBAL_LOOT_MODIFIERS.register("modifier_hook", () -> ModifierLootModifier.CODEC);
  public static final RegistryObject<LootItemConditionType> hasModifierLootCondition = LOOT_CONDITIONS.register("has_modifier", () -> new LootItemConditionType(new HasModifierLootCondition.ConditionSerializer()));
  public static final RegistryObject<LootItemFunctionType> modifierBonusFunction = LOOT_FUNCTIONS.register("modifier_bonus", () -> new LootItemFunctionType(new ModifierBonusLootFunction.Serializer()));
  public static final RegistryObject<LootItemConditionType> chrysophiliteLootCondition = LOOT_CONDITIONS.register("has_chrysophilite", () -> new LootItemConditionType(ChrysophiliteLootCondition.SERIALIZER));
  public static final RegistryObject<LootItemFunctionType> chrysophiliteBonusFunction = LOOT_FUNCTIONS.register("chrysophilite_bonus", () -> new LootItemFunctionType(ChrysophiliteBonusFunction.SERIALIZER));

  /*
   * Events
   */

  @SubscribeEvent
  void registerSerializers(RegisterEvent event) {
    if (event.getRegistryKey() == Registries.RECIPE_SERIALIZER) {
      // conditional
      FluidEffect.BLOCK_EFFECTS.register(getResource("conditional"), ConditionalFluidEffect.Block.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("conditional"), ConditionalFluidEffect.Entity.LOADER);
      // simple
      FluidEffect.ENTITY_EFFECTS.register(getResource("calcified"), StrongBonesModifier.FLUID_EFFECT.getLoader());
      FluidEffect.ENTITY_EFFECTS.register(getResource("extinguish"), FluidEffect.EXTINGUISH_FIRE.getLoader());
      FluidEffect.ENTITY_EFFECTS.register(getResource("teleport"), FluidEffect.TELEPORT.getLoader());
      // potions
      FluidEffect.ENTITY_EFFECTS.register(getResource("cure_effects"), CureEffectsFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("remove_effect"), RemoveEffectFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("mob_effect"), MobEffectFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("potion"), PotionFluidEffect.LOADER);
      // misc
      FluidEffect.ENTITY_EFFECTS.register(getResource("damage"), DamageFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("restore_hunger"), RestoreHungerFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("fire"), FireFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("freeze"), FreezeFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("award_stat"), AwardStatFluidEffect.LOADER);
      FluidEffect.ENTITY_EFFECTS.register(getResource("add_breath"), AddBreathFluidEffect.LOADER);
      // block
      FluidEffect.BLOCK_EFFECTS.register(getResource("place_block"), PlaceBlockFluidEffect.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("mob_effect_cloud"), MobEffectCloudFluidEffect.LOADER);
      FluidEffect.BLOCK_EFFECTS.register(getResource("potion_cloud"), PotionCloudFluidEffect.LOADER);


      // modifier loaders
      ModifierManager.MODIFIER_LOADERS.register(getResource("inventory_with_menu"), InventoryMenuModifier.LOADER);
      ModifierManager.MODIFIER_LOADERS.register(getResource("composable"), ComposableModifier.LOADER);
      // specialized
      ModifierManager.MODIFIER_LOADERS.register(getResource("tool_belt"), ToolBeltModifier.LOADER);
      // modifier names, sometimes I wonder if I have too many registries for tiny JSON pieces
      ModifierLevelDisplay.LOADER.register(getResource("default"), ModifierLevelDisplay.DEFAULT.getLoader());
      ModifierLevelDisplay.LOADER.register(getResource("single_level"), ModifierLevelDisplay.SINGLE_LEVEL.getLoader());
      ModifierLevelDisplay.LOADER.register(getResource("no_levels"), ModifierLevelDisplay.NO_LEVELS.getLoader());
      ModifierLevelDisplay.LOADER.register(getResource("pluses"), ModifierLevelDisplay.PLUSES.getLoader());
      ModifierLevelDisplay.LOADER.register(getResource("unique"), UniqueForLevels.LOADER);

      // modifier modules //
      // armor
      ModifierModule.LOADER.register(getResource("effect_immunity"), EffectImmunityModule.LOADER);
      ModifierModule.LOADER.register(getResource("mob_disguise"), MobDisguiseModule.LOADER);
      ModifierModule.LOADER.register(getResource("block_damage"), BlockDamageSourceModule.LOADER);
      ModifierModule.LOADER.register(getResource("cover_ground"), CoverGroundWalkerModule.LOADER);
      ModifierModule.LOADER.register(getResource("protection"), ProtectionModule.LOADER);
      ModifierModule.LOADER.register(getResource("replace_fluid"), ReplaceBlockWalkerModule.LOADER);
      ModifierModule.LOADER.register(getResource("tool_action_walk_transform"), ToolActionWalkerTransformModule.LOADER);
      // behavior
      ModifierModule.LOADER.register(getResource("attribute"), AttributeModule.LOADER);
      ModifierModule.LOADER.register(getResource("campfire_extinguish"), ExtinguishCampfireModule.LOADER);
      ModifierModule.LOADER.register(getResource("reduce_tool_damage"), ReduceToolDamageModule.LOADER);
      ModifierModule.LOADER.register(getResource("repair"), RepairModule.LOADER);
      ModifierModule.LOADER.register(getResource("show_offhand"), ShowOffhandModule.LOADER);
      ModifierModule.LOADER.register(getResource("tool_actions"), ToolActionsModule.LOADER);
      ModifierModule.LOADER.register(getResource("tool_action_transform"), ToolActionTransformModule.LOADER);
      // build
      ModifierModule.LOADER.register(getResource("conditional_stat"), ConditionalStatModule.LOADER);
      ModifierModule.LOADER.register(getResource("modifier_slot"), ModifierSlotModule.LOADER);
      ModifierModule.LOADER.register(getResource("rarity"), RarityModule.LOADER);
      ModifierModule.LOADER.register(getResource("requirements"), ModifierRequirementsModule.LOADER);
      ModifierModule.LOADER.register(getResource("swappable_slot"), SwappableSlotModule.LOADER);
      ModifierModule.LOADER.register(getResource("swappable_bonus_slot"), SwappableSlotModule.BonusSlot.LOADER);
      ModifierModule.LOADER.register(getResource("swappable_tool_traits"), SwappableToolTraitsModule.LOADER);
      ModifierModule.LOADER.register(getResource("stat_boost"), StatBoostModule.LOADER);
      ModifierModule.LOADER.register(getResource("set_stat"), SetStatModule.LOADER);
      ModifierModule.LOADER.register(getResource("trait"), ModifierTraitModule.LOADER);
      ModifierModule.LOADER.register(getResource("volatile_flag"), VolatileFlagModule.LOADER);
      // combat
      ModifierModule.LOADER.register(getResource("conditional_melee_damage"), ConditionalMeleeDamageModule.LOADER);
      ModifierModule.LOADER.register(getResource("knockback"), KnockbackModule.LOADER);
      ModifierModule.LOADER.register(getResource("melee_attribute"), MeleeAttributeModule.LOADER);
      ModifierModule.LOADER.register(getResource("mob_effect"), MobEffectModule.LOADER);
      // display
      ModifierModule.LOADER.register(getResource("durability_color"), DurabilityBarColorModule.LOADER);
      // enchantment
      ModifierModule.LOADER.register(getResource("constant_enchantment"), EnchantmentModule.Constant.LOADER);
      ModifierModule.LOADER.register(getResource("main_hand_harvest_enchantment"), EnchantmentModule.MainHandHarvest.LOADER);
      ModifierModule.LOADER.register(getResource("armor_harvest_enchantment"), EnchantmentModule.ArmorHarvest.LOADER);
      ModifierModule.LOADER.register(getResource("enchantment_ignoring_protection"), EnchantmentModule.Protection.LOADER);
      ModifierModule.LOADER.register(getResource("weapon_looting"), LootingModule.Weapon.LOADER);
      ModifierModule.LOADER.register(getResource("armor_looting"), LootingModule.Armor.LOADER);
      // mining
      ModifierModule.LOADER.register(getResource("conditional_mining_speed"), ConditionalMiningSpeedModule.LOADER);
      // technical
      ModifierModule.LOADER.register(getResource("armor_level"), ArmorLevelModule.LOADER);
      ModifierModule.LOADER.register(getResource("armor_stat"), ArmorStatModule.LOADER);

      // special
      ModifierModule.LOADER.register(getResource("the_one_probe"), TheOneProbeModule.INSTANCE.getLoader());
      ModifierModule.LOADER.register(getResource("enderclearance"), EnderclearanceModule.INSTANCE.getLoader());
      // armor
      ModifierModule.LOADER.register(getResource("depth_protection"), DepthProtectionModule.LOADER);
      ModifierModule.LOADER.register(getResource("flame_barrier"), FlameBarrierModule.LOADER);
      ModifierModule.LOADER.register(getResource("kinetic"), KineticModule.LOADER);
      ModifierModule.LOADER.register(getResource("recurrent_protection"), RecurrentProtectionModule.LOADER);
      // ranged
      ModifierModule.LOADER.register(getResource("restrict_projectile_angle"), RestrictAngleModule.LOADER);

      // modifier predicates
      ModifierPredicate.LOADER.register(getResource("single"), SingleModifierPredicate.LOADER);
      ModifierPredicate.LOADER.register(getResource("tag"), TagModifierPredicate.LOADER);
      ModifierPredicate.LOADER.register(getResource("slot_type"), SlotTypeModifierPredicate.LOADER);


      // variables
      // block
      BlockVariable.LOADER.register(getResource("constant"), BlockVariable.Constant.LOADER);
      BlockVariable.LOADER.register(getResource("conditional"), ConditionalBlockVariable.LOADER);
      BlockVariable.LOADER.register(getResource("blast_resistance"), BlockVariable.BLAST_RESISTANCE.getLoader());
      BlockVariable.LOADER.register(getResource("hardness"), BlockVariable.HARDNESS.getLoader());
      BlockVariable.LOADER.register(getResource("state_property"), StatePropertyVariable.LOADER);
      // entity
      EntityVariable.LOADER.register(getResource("constant"), EntityVariable.Constant.LOADER);
      EntityVariable.LOADER.register(getResource("conditional"), ConditionalEntityVariable.LOADER);
      EntityVariable.LOADER.register(getResource("health"), EntityVariable.HEALTH.getLoader());
      EntityVariable.LOADER.register(getResource("height"), EntityVariable.HEIGHT.getLoader());
      EntityVariable.LOADER.register(getResource("attribute"), AttributeEntityVariable.LOADER);
      EntityVariable.LOADER.register(getResource("effect_level"), EntityEffectLevelVariable.LOADER);
      EntityVariable.LOADER.register(getResource("light"), EntityLightVariable.LOADER);
      // tool
      ToolVariable.LOADER.register(getResource("constant"), ToolVariable.Constant.LOADER);
      ToolVariable.register(getResource("tool_conditional"), ConditionalToolVariable.LOADER);
      ToolVariable.register(getResource("tool_durability"), ToolVariable.CURRENT_DURABILITY.getLoader());
      ToolVariable.register(getResource("tool_stat"), ToolStatVariable.LOADER);
      // stat
      ConditionalStatVariable.LOADER.register(getResource("constant"), ConditionalStatVariable.Constant.LOADER);
      ConditionalStatVariable.register(getResource("entity"), EntityConditionalStatVariable.LOADER);
      // melee
      MeleeVariable.LOADER.register(getResource("constant"), MeleeVariable.Constant.LOADER);
      MeleeVariable.LOADER.register(getResource("entity"), EntityMeleeVariable.LOADER);
      // mining speed
      MiningSpeedVariable.LOADER.register(getResource("constant"), MiningSpeedVariable.Constant.LOADER);
      MiningSpeedVariable.LOADER.register(getResource("block"), BlockMiningSpeedVariable.LOADER);
      MiningSpeedVariable.LOADER.register(getResource("block_light"), BlockLightVariable.LOADER);
    }
  }

  @SubscribeEvent
  void commonSetup(final FMLCommonSetupEvent event) {
    TinkerDataCapability.register();
    PersistentDataCapability.register();
    EntityModifierCapability.register();
    // by default, we support modifying projectiles (arrows or fireworks mainly, but maybe other stuff). other entities may come in the future
    EntityModifierCapability.registerEntityPredicate(entity -> entity instanceof Projectile);
  }

  @SubscribeEvent
  void gatherData(final GatherDataEvent event) {
    DataGenerator generator = event.getGenerator();
    boolean server = event.includeServer();
    generator.addProvider(server, new ModifierProvider(generator));
    generator.addProvider(server, new ModifierRecipeProvider(generator));
    generator.addProvider(server, new FluidEffectProvider(generator));
    generator.addProvider(server, new ModifierTagProvider(generator, event.getExistingFileHelper()));
    generator.addProvider(server, new EnchantmentToModifierProvider(generator));
  }
}

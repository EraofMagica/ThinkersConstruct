package dev.tocraft.thconstruct.common.data.loot;

import net.minecraft.advancements.critereon.EntityEquipmentPredicate;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.core.Registry;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.storage.loot.LootContext.EntityTarget;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.ApplyExplosionDecay;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import net.minecraftforge.common.data.GlobalLootModifierProvider;
import net.minecraftforge.common.loot.LootTableIdCondition;
import dev.tocraft.eomantle.loot.AddEntryLootModifier;
import dev.tocraft.eomantle.loot.ReplaceItemLootModifier;
import dev.tocraft.eomantle.loot.condition.BlockTagLootCondition;
import dev.tocraft.eomantle.loot.condition.ContainsItemModifierLootCondition;
import dev.tocraft.eomantle.recipe.helper.ItemOutput;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.common.json.BlockOrEntityCondition;
import dev.tocraft.thconstruct.common.json.ConfigEnabledCondition;
import dev.tocraft.thconstruct.library.json.condition.TagNotEmptyCondition;
import dev.tocraft.thconstruct.library.json.loot.TagPreferenceLootEntry;
import dev.tocraft.thconstruct.shared.TinkerMaterials;
import dev.tocraft.thconstruct.smeltery.data.SmelteryCompat;
import dev.tocraft.thconstruct.tools.data.ModifierIds;
import dev.tocraft.thconstruct.tools.modifiers.ModifierLootModifier;
import dev.tocraft.thconstruct.tools.modifiers.loot.ChrysophiliteBonusFunction;
import dev.tocraft.thconstruct.tools.modifiers.loot.ChrysophiliteLootCondition;
import dev.tocraft.thconstruct.tools.modifiers.loot.HasModifierLootCondition;
import dev.tocraft.thconstruct.tools.modifiers.loot.ModifierBonusLootFunction;

public class GlobalLootModifiersProvider extends GlobalLootModifierProvider {
  public GlobalLootModifiersProvider(DataGenerator gen) {
    super(gen, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  }

  @Override
  protected void start() {
    add("wither_bone", ReplaceItemLootModifier.builder(Ingredient.of(Items.BONE), ItemOutput.fromItem(TinkerMaterials.necroticBone))
      .addCondition(LootTableIdCondition.builder(new ResourceLocation("entities/wither_skeleton")).build())
      .addCondition(ConfigEnabledCondition.WITHER_BONE_DROP)
      .build());

    // generic modifier hook
    // TODO: look into migrating this fully to loot tables
    ItemPredicate.Builder lootCapableTool = ItemPredicate.Builder.item().of(TinkerTags.Items.LOOT_CAPABLE_TOOL);
    add("modifier_hook", ModifierLootModifier.builder()
      .addCondition(BlockOrEntityCondition.INSTANCE)
      .addCondition(MatchTool.toolMatches(lootCapableTool)
                             .or(LootItemEntityPropertyCondition.hasProperties(EntityTarget.KILLER, EntityPredicate.Builder.entity().equipment(mainHand(lootCapableTool.build()))))
                             .build())
      .build());

    // chrysophilite modifier hook
    add("chrysophilite_modifier", AddEntryLootModifier.builder(LootItem.lootTableItem(Items.GOLD_NUGGET))
      .addCondition(new BlockTagLootCondition(TinkerTags.Blocks.CHRYSOPHILITE_ORES))
      .addCondition(new ContainsItemModifierLootCondition(Ingredient.of(TinkerTags.Items.CHRYSOPHILITE_ORES)).inverted())
      .addCondition(ChrysophiliteLootCondition.INSTANCE)
      .addFunction(SetItemCountFunction.setCount(UniformGenerator.between(2, 6)).build())
      .addFunction(ChrysophiliteBonusFunction.oreDrops(false).build())
      .addFunction(ApplyExplosionDecay.explosionDecay().build())
      .build());

    // lustrous implementation
    addLustrous("iron", false);
    addLustrous("gold", false);
    addLustrous("copper", false);
    addLustrous("cobalt", false);
    addLustrous("netherite_scrap", false);
    for (SmelteryCompat compat : SmelteryCompat.values()) {
      if (compat.isOre()) {
        addLustrous(compat.getName(), true);
      }
    }
  }

  /** Adds lustrous for an ore */
  private void addLustrous(String name, boolean optional) {
    TagKey<Item> nuggets = TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation("forge", "nuggets/" + name));
    ResourceLocation ores = new ResourceLocation("forge", "ores/" + name);
    AddEntryLootModifier.Builder builder = AddEntryLootModifier.builder(TagPreferenceLootEntry.tagPreference(nuggets));
    builder.addCondition(new BlockTagLootCondition(TagKey.create(Registry.BLOCK_REGISTRY, ores)))
           .addCondition(new ContainsItemModifierLootCondition(Ingredient.of(TagKey.create(Registry.ITEM_REGISTRY, ores))).inverted());
    if (optional) {
      builder.addCondition(new TagNotEmptyCondition<>(nuggets));
    }
    add("lustrous/" + name, builder.addCondition(new HasModifierLootCondition(ModifierIds.lustrous))
      .addFunction(SetItemCountFunction.setCount(UniformGenerator.between(2, 4)).build())
      .addFunction(ModifierBonusLootFunction.oreDrops(ModifierIds.lustrous, false).build())
      .addFunction(ApplyExplosionDecay.explosionDecay().build())
      .build());
  }

  /** Creates an equipment predicate for mainhand */
  private static EntityEquipmentPredicate mainHand(ItemPredicate mainHand) {
    EntityEquipmentPredicate.Builder builder = EntityEquipmentPredicate.Builder.equipment();
    builder.mainhand = mainHand;
    return builder.build();
  }
}

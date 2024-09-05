package dev.tocraft.thconstruct.library.tools.definition;

import com.google.common.collect.ImmutableMap;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.ToolActions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import dev.tocraft.eomantle.data.predicate.block.BlockPredicate;
import dev.tocraft.thconstruct.fixture.MaterialItemFixture;
import dev.tocraft.thconstruct.fixture.RegistrationFixture;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierFixture;
import dev.tocraft.thconstruct.library.tools.SlotType;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolHooks;
import dev.tocraft.thconstruct.library.tools.definition.module.ToolModule;
import dev.tocraft.thconstruct.library.tools.definition.module.aoe.AreaOfEffectIterator;
import dev.tocraft.thconstruct.library.tools.definition.module.aoe.CircleAOEIterator;
import dev.tocraft.thconstruct.library.tools.definition.module.build.MultiplyStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.SetStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.ToolActionToolHook;
import dev.tocraft.thconstruct.library.tools.definition.module.build.ToolActionsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.ToolSlotsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.ToolTraitsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.build.VolatileDataToolHook;
import dev.tocraft.thconstruct.library.tools.definition.module.material.PartStatsModule;
import dev.tocraft.thconstruct.library.tools.definition.module.material.ToolPartsHook;
import dev.tocraft.thconstruct.library.tools.definition.module.mining.IsEffectiveModule;
import dev.tocraft.thconstruct.library.tools.definition.module.mining.IsEffectiveToolHook;
import dev.tocraft.thconstruct.library.tools.definition.module.weapon.MeleeHitToolHook;
import dev.tocraft.thconstruct.library.tools.definition.module.weapon.SweepWeaponAttack;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.MultiplierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.StatsNBT;
import dev.tocraft.thconstruct.library.tools.part.IToolPart;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;
import dev.tocraft.thconstruct.test.BaseMcTest;
import dev.tocraft.thconstruct.test.TestHelper;
import dev.tocraft.thconstruct.test.TestHelper.ToolDefinitionStats;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class UpdateToolDefinitionDataPacketTest extends BaseMcTest {
  private static final ResourceLocation EMPTY_ID = new ResourceLocation("test", "empty");
  private static final ResourceLocation FILLED_ID = new ResourceLocation("test", "filled");

  @BeforeAll
  static void initialize() {
    MaterialItemFixture.init();
    ModifierFixture.init();
    RegistrationFixture.register(ToolModule.LOADER, "slots", ToolSlotsModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "is_effective", IsEffectiveModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "circle", CircleAOEIterator.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "sweep", SweepWeaponAttack.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "traits", ToolTraitsModule.LOADER);
    RegistrationFixture.register(ToolModule.LOADER, "actions", ToolActionsModule.LOADER);
  }

  @Test
  void testGenericEncodeDecode() {
    ToolDefinitionData empty = ToolDefinitionData.EMPTY;
    ToolDefinitionData filled = ToolDefinitionDataBuilder
      .builder()
      // parts
      .module(PartStatsModule.parts()
                             .part(MaterialItemFixture.MATERIAL_ITEM_HEAD, 10)
                             .part(MaterialItemFixture.MATERIAL_ITEM_HANDLE).build())
      // stats
      .module(new SetStatsModule(StatsNBT.builder()
        .set(ToolStats.DURABILITY, 1000)
        .set(ToolStats.ATTACK_DAMAGE, 152.5f).build()))
      .module(new MultiplyStatsModule(MultiplierNBT.builder()
        .set(ToolStats.MINING_SPEED, 10)
        .set(ToolStats.ATTACK_SPEED, 0.5f)
        .set(ToolStats.ATTACK_DAMAGE, 1).build()))
      .module(ToolSlotsModule.builder().slots(SlotType.UPGRADE, 5).slots(SlotType.ABILITY, 8).build())
      // traits
      .module(ToolTraitsModule.builder().trait(ModifierFixture.TEST_1, 10).build())
      .module(ToolActionsModule.of(ToolActions.AXE_DIG, ToolActions.SHOVEL_FLATTEN))
      // behavior
      .module(new IsEffectiveModule(BlockPredicate.set(Blocks.GRANITE), true))
      .module(new CircleAOEIterator(7, true))
      .module(new SweepWeaponAttack(4))
      .build();

    // send a packet over the buffer
    FriendlyByteBuf buffer = new FriendlyByteBuf(Unpooled.buffer());
    UpdateToolDefinitionDataPacket packetToEncode = new UpdateToolDefinitionDataPacket(ImmutableMap.of(EMPTY_ID, empty, FILLED_ID, filled));
    packetToEncode.encode(buffer);
    UpdateToolDefinitionDataPacket decoded = new UpdateToolDefinitionDataPacket(buffer);

    // parse results
    Map<ResourceLocation,ToolDefinitionData> parsedMap = decoded.getDataMap();
    assertThat(parsedMap).hasSize(2);

    // first validate empty
    ToolDefinitionData parsed = parsedMap.get(EMPTY_ID);
    assertThat(parsed).isNotNull();
    // no parts
    assertThat(parsed.getHook(ToolHooks.TOOL_MATERIALS).getStatTypes(ToolDefinition.EMPTY)).isEmpty();
    // no stats
    ToolDefinitionStats stats = TestHelper.buildStats(parsed);
    assertThat(stats.base().getContainedStats()).isEmpty();
    assertThat(stats.multipliers().getContainedStats()).isEmpty();
    // no slots
    assertThat(parsed.getHook(ToolHooks.VOLATILE_DATA)).isNotInstanceOf(ToolSlotsModule.class);
    // no traits
    assertThat(TestHelper.getTraits(parsed)).isEmpty();

    // next, validate the filled one
    parsed = parsedMap.get(FILLED_ID);
    assertThat(parsed).isNotNull();

    // parts
    ToolPartsHook toolPartsHook = parsed.getHook(ToolHooks.TOOL_PARTS);
    assertThat(toolPartsHook).isInstanceOf(PartStatsModule.class);
    PartStatsModule module = (PartStatsModule)toolPartsHook;
    List<IToolPart> parts = module.getParts(ToolDefinition.EMPTY);
    float[] scales = module.getScales();
    assertThat(parts).hasSize(2);
    assertThat(parts.get(0)).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HEAD);
    assertThat(scales[0]).isEqualTo(10);
    assertThat(parts.get(1)).isEqualTo(MaterialItemFixture.MATERIAL_ITEM_HANDLE);
    assertThat(scales[1]).isEqualTo(1);

    // stats
    stats = TestHelper.buildStats(parsed);
    assertThat(stats.base().getContainedStats()).hasSize(2);
    assertThat(stats.base().getContainedStats()).contains(ToolStats.DURABILITY);
    assertThat(stats.base().getContainedStats()).contains(ToolStats.ATTACK_DAMAGE);
    assertThat(stats.base().get(ToolStats.DURABILITY)).isEqualTo(1000);
    assertThat(stats.base().get(ToolStats.ATTACK_DAMAGE)).isEqualTo(152.5f);
    assertThat(stats.base().get(ToolStats.ATTACK_SPEED)).isEqualTo(ToolStats.ATTACK_SPEED.getDefaultValue());

    assertThat(stats.multipliers().getContainedStats()).hasSize(2); // attack damage is 1, so its skipped
    assertThat(stats.multipliers().getContainedStats()).contains(ToolStats.ATTACK_SPEED);
    assertThat(stats.multipliers().getContainedStats()).contains(ToolStats.MINING_SPEED);
    assertThat(stats.multipliers().get(ToolStats.MINING_SPEED)).isEqualTo(10);
    assertThat(stats.multipliers().get(ToolStats.ATTACK_SPEED)).isEqualTo(0.5f);
    assertThat(stats.multipliers().get(ToolStats.ATTACK_DAMAGE)).isEqualTo(1);
    assertThat(stats.multipliers().get(ToolStats.DURABILITY)).isEqualTo(1);

    // slots
    VolatileDataToolHook volatileHook = parsed.getHook(ToolHooks.VOLATILE_DATA);
    assertThat(volatileHook).isInstanceOf(ToolSlotsModule.class);
    Map<SlotType,Integer> slots = ((ToolSlotsModule) volatileHook).slots();
    assertThat(slots).hasSize(2);
    assertThat(slots).containsEntry(SlotType.UPGRADE, 5);
    assertThat(slots).containsEntry(SlotType.ABILITY, 8);

    // traits
    List<ModifierEntry> traits = TestHelper.getTraits(parsed);
    assertThat(traits).hasSize(1);
    assertThat(traits.get(0).getModifier()).isEqualTo(ModifierFixture.TEST_MODIFIER_1);
    assertThat(traits.get(0).getLevel()).isEqualTo(10);

    // actions
    ToolActionToolHook actionModule = parsed.getHook(ToolHooks.TOOL_ACTION);
    assertThat(actionModule).isInstanceOf(ToolActionsModule.class);
    assertThat(((ToolActionsModule) actionModule).actions()).hasSize(2);
    IToolStackView tool = mock(IToolStackView.class);
    assertThat(parsed.getHook(ToolHooks.TOOL_ACTION).canPerformAction(tool, ToolActions.AXE_DIG)).isTrue();
    assertThat(parsed.getHook(ToolHooks.TOOL_ACTION).canPerformAction(tool, ToolActions.SHOVEL_FLATTEN)).isTrue();

    // harvest
    IsEffectiveToolHook harvestLogic = parsed.getHook(ToolHooks.IS_EFFECTIVE);
    assertThat(harvestLogic).isInstanceOf(IsEffectiveModule.class);
    assertThat(harvestLogic.isToolEffective(mock(IToolStackView.class), Blocks.GRANITE.defaultBlockState())).isTrue();

    // aoe
    AreaOfEffectIterator aoe = parsed.getHook(ToolHooks.AOE_ITERATOR);
    assertThat(aoe).isInstanceOf(CircleAOEIterator.class);
    assertThat(((CircleAOEIterator)aoe).diameter()).isEqualTo(7);
    assertThat(((CircleAOEIterator)aoe).is3D()).isTrue();

    // weapon
    MeleeHitToolHook attack = parsed.getHook(ToolHooks.MELEE_HIT);
    assertThat(attack).isInstanceOf(SweepWeaponAttack.class);
    assertThat(((SweepWeaponAttack)attack).range()).isEqualTo(4);
  }
}

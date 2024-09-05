package dev.tocraft.thconstruct.tables.recipe;

import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import dev.tocraft.eomantle.data.loadable.common.IngredientLoadable;
import dev.tocraft.eomantle.data.loadable.field.ContextKey;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.recipe.ingredient.SizedIngredient;
import dev.tocraft.thconstruct.common.TinkerTags;
import dev.tocraft.thconstruct.library.recipe.partbuilder.IPartBuilderContainer;
import dev.tocraft.thconstruct.library.recipe.partbuilder.IPartBuilderRecipe;
import dev.tocraft.thconstruct.library.recipe.partbuilder.Pattern;
import dev.tocraft.thconstruct.library.tools.definition.module.material.ToolPartsHook;
import dev.tocraft.thconstruct.library.tools.helper.ModifierUtil;
import dev.tocraft.thconstruct.library.tools.item.IModifiable;
import dev.tocraft.thconstruct.library.tools.nbt.ToolStack;
import dev.tocraft.thconstruct.library.tools.part.IToolPart;
import dev.tocraft.thconstruct.library.tools.stat.ToolStats;
import dev.tocraft.thconstruct.tables.TinkerTables;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

/** Recipe to break a tool into tool parts */
@SuppressWarnings("deprecation")  // Forge is dumb
@RequiredArgsConstructor
public class PartBuilderToolRecycle implements IPartBuilderRecipe {
  /** Title for the screen */
  private static final Component TOOL_RECYCLING = dev.tocraft.thconstruct.ThConstruct.makeTranslation("recipe", "tool_recycling");
  /** General instructions for recycling */
  private static final List<Component> INSTRUCTIONS = Collections.singletonList(dev.tocraft.thconstruct.ThConstruct.makeTranslation("recipe", "tool_recycling.info"));
  /** Error for trying to recycle a tool that cannot be */
  private static final List<Component> NO_MODIFIERS = Collections.singletonList(dev.tocraft.thconstruct.ThConstruct.makeTranslation("recipe", "tool_recycling.no_modifiers").withStyle(ChatFormatting.RED));
  /** Default tool field */
  public static final SizedIngredient DEFAULT_TOOLS = SizedIngredient.fromTag(TinkerTags.Items.MULTIPART_TOOL);
  /** Loader instance */
  public static final RecordLoadable<PartBuilderToolRecycle> LOADER = RecordLoadable.create(
    ContextKey.ID.requiredField(),
    SizedIngredient.LOADABLE.defaultField("tools", DEFAULT_TOOLS, true, r -> r.toolRequirement),
    IngredientLoadable.DISALLOW_EMPTY.requiredField("pattern", r -> r.pattern),
    PartBuilderToolRecycle::new);

  /** Should never be needed, but just in case better than null */
  private static final Pattern ERROR = new Pattern(dev.tocraft.thconstruct.ThConstruct.MOD_ID, "missingno");
  @Getter
  private final ResourceLocation id;
  private final SizedIngredient toolRequirement;
  private final Ingredient pattern;

  @Override
  public Pattern getPattern() {
    return ERROR;
  }

  @Override
  public Stream<Pattern> getPatterns(IPartBuilderContainer inv) {
    if (inv.getStack().getItem() instanceof IModifiable modifiable) {
      return ToolPartsHook.parts(modifiable.getToolDefinition()).stream()
                          .map(part -> Registry.ITEM.getKey(part.asItem()))
                          .distinct()
                          .map(Pattern::new);
    }
    return Stream.empty();
  }

  @Override
  public int getCost() {
    return 0;
  }

  @Override
  public int getItemsUsed(IPartBuilderContainer inv) {
    return toolRequirement.getAmountNeeded();
  }

  @Override
  public boolean partialMatch(IPartBuilderContainer inv) {
    return pattern.test(inv.getPatternStack()) && toolRequirement.test(inv.getStack());
  }

  @Override
  public boolean matches(IPartBuilderContainer inv, Level pLevel) {
    return partialMatch(inv) && ToolStack.from(inv.getStack()).getUpgrades().isEmpty();
  }

  @Override
  public ItemStack assemble(IPartBuilderContainer inv, Pattern pattern) {
    ToolStack tool = ToolStack.from(inv.getStack());
    // first, try to find a matching part
    IToolPart match = null;
    int matchIndex = -1;
    List<IToolPart> requirements = ToolPartsHook.parts(tool.getDefinition());
    for (int i = 0; i < requirements.size(); i++) {
      IToolPart part = requirements.get(i);
      if (pattern.equals(Registry.ITEM.getKey(part.asItem()))) {
        matchIndex = i;
        match = part;
        break;
      }
    }
    // failed to find part? should never happen but safety return
    if (match == null) {
      return ItemStack.EMPTY;
    }
    return match.withMaterial(tool.getMaterial(matchIndex).getVariant());
  }

  @Override
  public ItemStack getLeftover(IPartBuilderContainer inv, Pattern pattern) {
    ToolStack tool = ToolStack.from(inv.getStack());

    // if the tool is damaged, it we only have a chance of a second tool part
    int damage = tool.getDamage();
    if (damage > 0) {
      int max = tool.getStats().getInt(ToolStats.DURABILITY);
      if (dev.tocraft.thconstruct.ThConstruct.RANDOM.nextInt(max) < damage) {
        return ItemStack.EMPTY;
      }
    }

    // find all parts that did not match the pattern
    List<IToolPart> parts = new ArrayList<>();
    IntList indices = new IntArrayList();
    boolean found = false;
    List<IToolPart> requirements = ToolPartsHook.parts(tool.getDefinition());
    for (int i = 0; i < requirements.size(); i++) {
      IToolPart part = requirements.get(i);
      if (found || !pattern.equals(Registry.ITEM.getKey(part.asItem()))) {
        parts.add(part);
        indices.add(i);
      } else {
        found = true;
      }
    }
    if (parts.isEmpty()) {
      return ItemStack.EMPTY;
    }
    int index = dev.tocraft.thconstruct.ThConstruct.RANDOM.nextInt(parts.size());
    return parts.get(index).withMaterial(tool.getMaterial(indices.getInt(index)).getVariant());
  }

  /** @deprecated use {@link #assemble(IPartBuilderContainer, Pattern)} */
  @Deprecated
  @Override
  public ItemStack getResultItem() {
    return ItemStack.EMPTY;
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerTables.partBuilderToolRecycling.get();
  }

  @Nullable
  @Override
  public Component getTitle() {
    return TOOL_RECYCLING;
  }

  @Override
  public List<Component> getText(IPartBuilderContainer inv) {
    return ModifierUtil.hasUpgrades(inv.getStack()) ? NO_MODIFIERS : INSTRUCTIONS;
  }

  @RequiredArgsConstructor
  public static class Finished implements FinishedRecipe {
    @Getter
    private final ResourceLocation id;
    private final SizedIngredient tools;
    private final Ingredient pattern;

    @Override
    public void serializeRecipeData(JsonObject json) {
      json.add("tools", tools.serialize());
      json.add("pattern", pattern.toJson());
    }

    @Override
    public RecipeSerializer<?> getType() {
      return TinkerTables.partBuilderToolRecycling.get();
    }

    @Nullable
    @Override
    public JsonObject serializeAdvancement() {
      return null;
    }

    @Nullable
    @Override
    public ResourceLocation getAdvancementId() {
      return null;
    }
  }
}

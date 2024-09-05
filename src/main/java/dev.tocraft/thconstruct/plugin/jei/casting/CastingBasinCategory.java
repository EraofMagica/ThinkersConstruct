package dev.tocraft.thconstruct.plugin.jei.casting;

import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import dev.tocraft.thconstruct.library.recipe.casting.IDisplayableCastingRecipe;
import dev.tocraft.thconstruct.plugin.jei.TConstructJEIConstants;
import dev.tocraft.thconstruct.smeltery.TinkerSmeltery;

public class CastingBasinCategory extends AbstractCastingCategory {
  private static final Component TITLE = dev.tocraft.thconstruct.ThConstruct.makeTranslation("jei", "casting.basin");
  public CastingBasinCategory(IGuiHelper guiHelper) {
    super(guiHelper, TinkerSmeltery.searedBasin.get(), guiHelper.createDrawable(BACKGROUND_LOC, 117, 16, 16, 16));
  }

  @Override
  public RecipeType<IDisplayableCastingRecipe> getRecipeType() {
    return TConstructJEIConstants.CASTING_BASIN;
  }

  @Override
  public Component getTitle() {
    return TITLE;
  }
}

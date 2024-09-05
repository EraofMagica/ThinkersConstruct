package dev.tocraft.thconstruct.plugin.jei.modifiers;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.ingredients.IIngredientRenderer;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.TooltipFlag;
import dev.tocraft.thconstruct.library.client.modifiers.ModifierIconManager;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

/** Special modifier ingredient renderer used for ingredients in the bookmark menu */
public enum ModifierBookmarkIngredientRenderer implements IIngredientRenderer<ModifierEntry> {
  INSTANCE;

  private static final String WRAPPER_KEY = "jei.thconstruct.modifier_ingredient";

  @Override
  public void render(GuiGraphics matrixStack, @Nullable ModifierEntry entry) {
    if (entry != null) {
      ModifierIconManager.renderIcon(matrixStack, entry.getModifier(), 0, 0, 100, 16);
    }
  }

  @Override
  public List<Component> getTooltip(ModifierEntry entry, TooltipFlag flag) {
    List<Component> list = new ArrayList<>();
    // not using the main method as that applies color
    list.add(Component.translatable(WRAPPER_KEY, Component.translatable(entry.getModifier().getTranslationKey())));
    list.addAll(entry.getModifier().getDescriptionList());
    if (flag.isAdvanced()) {
      list.add((Component.literal(entry.getId().toString())).withStyle(ChatFormatting.DARK_GRAY));
    }
    return list;
  }
}

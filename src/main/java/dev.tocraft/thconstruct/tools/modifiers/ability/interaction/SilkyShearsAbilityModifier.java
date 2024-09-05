package dev.tocraft.thconstruct.tools.modifiers.ability.interaction;

import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

public class SilkyShearsAbilityModifier extends ShearsAbilityModifier {
  public SilkyShearsAbilityModifier(int range, int priority) {
    super(range, priority);
  }
  
  @Override
  protected boolean isShears(IToolStackView tool) {
    return tool.getModifierLevel(TinkerModifiers.silky.getId()) > 0;
  }
}

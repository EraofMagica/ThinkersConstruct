package dev.tocraft.thconstruct.library.data.tinkering;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import dev.tocraft.thconstruct.library.data.AbstractTagProvider;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierManager;

/** Tag provider to generate modifier tags */
public abstract class AbstractModifierTagProvider extends AbstractTagProvider<Modifier> {
  protected AbstractModifierTagProvider(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper) {
    // TODO:we don't fire modifier event during datagen, should we?
    super(generator, modId, ModifierManager.TAG_FOLDER, Modifier::getId, id -> true/*ModifierManager.INSTANCE.containsStatic(new ModifierId(id))*/, existingFileHelper);
  }
}

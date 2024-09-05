package dev.tocraft.thconstruct.library.data.tinkering;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import dev.tocraft.thconstruct.library.data.AbstractTagProvider;
import dev.tocraft.thconstruct.library.materials.definition.IMaterial;
import dev.tocraft.thconstruct.library.materials.definition.MaterialManager;

/** Tag provider for materials */
public abstract class AbstractMaterialTagProvider extends AbstractTagProvider<IMaterial> {
  protected AbstractMaterialTagProvider(DataGenerator generator, String modId, ExistingFileHelper existingFileHelper) {
    super(generator, modId, MaterialManager.TAG_FOLDER, IMaterial::getIdentifier, id -> true, existingFileHelper);
  }
}

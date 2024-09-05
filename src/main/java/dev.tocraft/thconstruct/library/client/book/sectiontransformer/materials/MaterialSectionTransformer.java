package dev.tocraft.thconstruct.library.client.book.sectiontransformer.materials;

import dev.tocraft.thconstruct.library.materials.definition.IMaterial;

// TODO: still needed? Not used in the mod currently
public class MaterialSectionTransformer extends AbstractMaterialSectionTransformer {
  public static final MaterialSectionTransformer INSTANCE = new MaterialSectionTransformer("materials", false);

  public MaterialSectionTransformer(String name, boolean detailed) {
    super(name, detailed);
  }

  @Override
  protected boolean isValidMaterial(IMaterial material) {
    return true;
  }
}

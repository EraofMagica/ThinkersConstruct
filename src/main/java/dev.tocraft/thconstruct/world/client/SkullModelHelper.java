package dev.tocraft.thconstruct.world.client;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.model.SkullModel;
import net.minecraft.client.model.SkullModelBase;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.world.level.block.SkullBlock;
import dev.tocraft.thconstruct.world.TinkerHeadType;

import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/** Helps with creation and registration of skull block models */
public class SkullModelHelper {
  /** Map of head type to model layer location for each head type */
  public static final Map<TinkerHeadType,ModelLayerLocation> HEAD_LAYERS = Arrays.stream(TinkerHeadType.values()).collect(
    Collectors.toMap(Function.identity(), type -> new ModelLayerLocation(dev.tocraft.thconstruct.ThConstruct.getResource(type.getSerializedName() + "_head"), "main"), (a, b) -> a, () -> new EnumMap<>(TinkerHeadType.class)));

  private SkullModelHelper() {}

  /** Injects the extra skulls into the given map */
  private static ImmutableMap<SkullBlock.Type,SkullModelBase> inject(EntityModelSet modelSet, Map<SkullBlock.Type,SkullModelBase> original) {
    ImmutableMap.Builder<SkullBlock.Type,SkullModelBase> builder = ImmutableMap.builder();
    builder.putAll(original);
    HEAD_LAYERS.forEach((type, layer) -> builder.put(type, new SkullModel(modelSet.bakeLayer(layer))));
    return builder.build();
  }

  /** Creates a head with the given start and texture size */
  public static LayerDefinition createHeadLayer(int headX, int headY, int width, int height) {
    MeshDefinition mesh = new MeshDefinition();
    mesh.getRoot().addOrReplaceChild("head", CubeListBuilder.create().texOffs(headX, headY).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F), PartPose.ZERO);
    return LayerDefinition.create(mesh, width, height);
  }

  /** Creates a head with a hat, starting the head at 0,0, hat at the values, and using the given size */
  @SuppressWarnings("SameParameterValue")
  public static LayerDefinition createHeadHatLayer(int hatX, int hatY, int width, int height) {
    MeshDefinition mesh = SkullModel.createHeadModel();
    mesh.getRoot().getChild("head").addOrReplaceChild("hat", CubeListBuilder.create().texOffs(hatX, hatY).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.25F)), PartPose.ZERO);
    return LayerDefinition.create(mesh, width, height);
  }

  /** Creates a layer de */
  public static LayerDefinition createPiglinHead() {
    MeshDefinition mesh = new MeshDefinition();
    PartDefinition head = mesh.getRoot().addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -8.0F, -4.0F, 10.0F, 8.0F, 8.0F).texOffs(31, 1).addBox(-2.0F, -4.0F, -5.0F, 4.0F, 4.0F, 1.0F).texOffs(2, 4).addBox(2.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F).texOffs(2, 0).addBox(-3.0F, -2.0F, -5.0F, 1.0F, 2.0F, 1.0F), PartPose.ZERO);
    head.addOrReplaceChild("left_ear", CubeListBuilder.create().texOffs(51, 6).addBox(0.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F), PartPose.offsetAndRotation(4.5F, -6.0F, 0.0F, 0.0F, 0.0F, (-(float)Math.PI / 6F)));
    head.addOrReplaceChild("right_ear", CubeListBuilder.create().texOffs(39, 6).addBox(-1.0F, 0.0F, -2.0F, 1.0F, 5.0F, 4.0F), PartPose.offsetAndRotation(-4.5F, -6.0F, 0.0F, 0.0F, 0.0F, ((float)Math.PI / 6F)));
    return LayerDefinition.create(mesh, 64, 64);
  }
}

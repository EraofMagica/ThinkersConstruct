package dev.tocraft.thconstruct.tools.modifiers.traits.ranged;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.EntityHitResult;
import dev.tocraft.eomantle.recipe.helper.TagPreference;
import dev.tocraft.thconstruct.library.modifiers.Modifier;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileLaunchModifierHook;
import dev.tocraft.thconstruct.library.module.ModuleHookMap.Builder;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;
import dev.tocraft.thconstruct.shared.TinkerMaterials;

import javax.annotation.Nullable;

public class OlympicModifier extends Modifier implements ProjectileLaunchModifierHook, ProjectileHitModifierHook {
  private static final ResourceLocation OLYMPIC_START = dev.tocraft.thconstruct.ThConstruct.getResource("olympic_start");
  private static final TagKey<Item> PLATINUM_NUGGET = ItemTags.create(new ResourceLocation("forge", "nuggets/platinum"));

  /** Gets the nugget for the given distance */
  private static Item getNugget(double distanceSq) {
    // 50 meters - platinum
    if (distanceSq > 2500) {
      return TagPreference.getPreference(PLATINUM_NUGGET).orElse(TinkerMaterials.cobalt.getNugget());
    }
    // 40 meters - gold
    if (distanceSq > 1600) {
      return Items.GOLD_NUGGET;
    }
    // 30 meters - iron
    if (distanceSq > 900) {
      return Items.IRON_NUGGET;
    }
    // 20 meters - copper
    if (distanceSq > 400) {
      return TinkerMaterials.copperNugget.get();
    }
    return Items.AIR;
  }

  @Override
  protected void registerHooks(Builder hookBuilder) {
    hookBuilder.addHook(this, ModifierHooks.PROJECTILE_LAUNCH, ModifierHooks.PROJECTILE_HIT);
  }

  @Override
  public void onProjectileLaunch(IToolStackView tool, ModifierEntry modifier, LivingEntity shooter, Projectile projectile, @Nullable AbstractArrow arrow, NamespacedNBT persistentData, boolean primary) {
    // store fired position
    CompoundTag tag = new CompoundTag();
    tag.putDouble("X", shooter.getX());
    tag.putDouble("Y", shooter.getY());
    tag.putDouble("Z", shooter.getZ());
    persistentData.put(OLYMPIC_START, tag);
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    // 10% chance per level
    Entity targetEntity = hit.getEntity();
    if (!projectile.level.isClientSide && targetEntity.getType().getCategory() == MobCategory.MONSTER && RANDOM.nextInt(20) < modifier.getLevel()) {
      CompoundTag startCompound = persistentData.getCompound(OLYMPIC_START);
      if (!startCompound.isEmpty() && startCompound.contains("X", Tag.TAG_ANY_NUMERIC) && startCompound.contains("Y", Tag.TAG_ANY_NUMERIC) && startCompound.contains("Z", Tag.TAG_ANY_NUMERIC)) {
        // nugget type based on distance
        Item nugget = getNugget(targetEntity.distanceToSqr(startCompound.getDouble("X"), startCompound.getDouble("Y"), startCompound.getDouble("Z")));
        if (nugget != Items.AIR) {
          // spawn and play sound
          targetEntity.spawnAtLocation(nugget);
          if (attacker != null) {
            projectile.level.playSound(null, attacker.getX(), attacker.getY(), attacker.getZ(), SoundEvents.PLAYER_LEVELUP, SoundSource.PLAYERS, 1.0f, 1.0f);
          }
        }
      }
    }
    return false;
  }
}

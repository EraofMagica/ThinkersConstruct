package dev.tocraft.thconstruct.tools.recipe.severing;

import com.mojang.authlib.GameProfile;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeSerializer;
import dev.tocraft.eomantle.recipe.helper.ItemOutput;
import dev.tocraft.eomantle.recipe.ingredient.EntityIngredient;
import dev.tocraft.thconstruct.library.recipe.modifiers.severing.SeveringRecipe;
import dev.tocraft.thconstruct.tools.TinkerModifiers;

/** Beheading recipe that sets player skin */
public class PlayerBeheadingRecipe extends SeveringRecipe {
  public PlayerBeheadingRecipe(ResourceLocation id) {
    super(id, EntityIngredient.of(EntityType.PLAYER), ItemOutput.fromItem(Items.PLAYER_HEAD));
  }

  @Override
  public RecipeSerializer<?> getSerializer() {
    return TinkerModifiers.playerBeheadingSerializer.get();
  }

  @Override
  public ItemStack getOutput(Entity entity) {
    ItemStack stack = new ItemStack(Items.PLAYER_HEAD);
    if (entity instanceof Player) {
      GameProfile gameprofile = ((Player)entity).getGameProfile();
      stack.getOrCreateTag().put("SkullOwner", NbtUtils.writeGameProfile(new CompoundTag(), gameprofile));
    }
    return stack;
  }
}

package dev.tocraft.thconstruct.library.recipe;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import dev.tocraft.eomantle.data.loadable.common.NBTLoadable;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;

import javax.annotation.Nullable;
import java.util.function.Predicate;

/** Extended implementation of {@link net.minecraft.advancements.critereon.NbtPredicate} that supports syncing over the network */
public record TagPredicate(@Nullable CompoundTag tag) implements Predicate<CompoundTag> {
  /** Loadable instance */
  public static final RecordLoadable<TagPredicate> LOADABLE = NBTLoadable.ALLOW_STRING.flatXmap(TagPredicate::new, p -> p.tag);
  /** Instance that matches any NBT */
  public static final TagPredicate ANY = new TagPredicate(null);

  @Override
  public boolean test(@Nullable CompoundTag toTest) {
    return NbtUtils.compareNbt(this.tag, toTest, true);
  }
}

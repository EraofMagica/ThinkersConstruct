package dev.tocraft.thconstruct.library.modifiers.modules.combat;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.phys.EntityHitResult;
import dev.tocraft.eomantle.data.loadable.Loadables;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.predicate.IJsonPredicate;
import dev.tocraft.eomantle.data.predicate.entity.LivingEntityPredicate;
import dev.tocraft.thconstruct.library.json.RandomLevelingValue;
import dev.tocraft.thconstruct.library.modifiers.ModifierEntry;
import dev.tocraft.thconstruct.library.module.ModuleHook;
import dev.tocraft.thconstruct.library.modifiers.ModifierHooks;
import dev.tocraft.thconstruct.library.modifiers.hook.armor.OnAttackedModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.combat.MeleeHitModifierHook;
import dev.tocraft.thconstruct.library.modifiers.hook.ranged.ProjectileHitModifierHook;
import dev.tocraft.thconstruct.library.module.HookProvider;
import dev.tocraft.thconstruct.library.modifiers.modules.ModifierModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModifierCondition.ConditionalModule;
import dev.tocraft.thconstruct.library.modifiers.modules.util.ModuleBuilder;
import dev.tocraft.thconstruct.library.tools.context.EquipmentContext;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.helper.ToolDamageUtil;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;
import dev.tocraft.thconstruct.library.tools.nbt.ModifierNBT;
import dev.tocraft.thconstruct.library.tools.nbt.NamespacedNBT;

import javax.annotation.Nullable;
import java.util.List;

import static dev.tocraft.thconstruct.ThConstruct.RANDOM;

/**
 * Module that applies a mob effect on melee attack, projectile hit, and counterattack
 */
public record MobEffectModule(IJsonPredicate<LivingEntity> target, MobEffect effect, RandomLevelingValue level, RandomLevelingValue time, ModifierCondition<IToolStackView> condition) implements OnAttackedModifierHook, MeleeHitModifierHook, ProjectileHitModifierHook, ModifierModule, ConditionalModule<IToolStackView> {
  private static final List<ModuleHook<?>> DEFAULT_HOOKS = HookProvider.<MobEffectModule>defaultHooks(ModifierHooks.ON_ATTACKED, ModifierHooks.MELEE_HIT, ModifierHooks.PROJECTILE_HIT);
  public static final RecordLoadable<MobEffectModule> LOADER = RecordLoadable.create(
    LivingEntityPredicate.LOADER.defaultField("target", MobEffectModule::target),
    Loadables.MOB_EFFECT.requiredField("effect", MobEffectModule::effect),
    RandomLevelingValue.LOADABLE.requiredField("level", MobEffectModule::level),
    RandomLevelingValue.LOADABLE.requiredField("time", MobEffectModule::time),
    ModifierCondition.TOOL_FIELD,
    MobEffectModule::new);

  /** Creates a builder instance */
  public static MobEffectModule.Builder builder(MobEffect effect) {
    return new Builder(effect);
  }

  /** Applies the effect for the given level */
  private void applyEffect(@Nullable LivingEntity target, float scaledLevel) {
    if (target == null || !this.target.matches(target)) {
      return;
    }
    int level = Math.round(this.level.computeValue(scaledLevel)) - 1;
    if (level < 0) {
      return;
    }
    float duration = this.time.computeValue(scaledLevel);
    if (duration > 0) {
      target.addEffect(new MobEffectInstance(effect, (int)duration, level));
    }
  }

  @Override
  public void onAttacked(IToolStackView tool, ModifierEntry modifier, EquipmentContext context, EquipmentSlot slotType, DamageSource source, float amount, boolean isDirectDamage) {
    Entity attacker = source.getEntity();
    if (isDirectDamage && attacker instanceof LivingEntity living) {
      // 15% chance of working per level
      float scaledLevel = modifier.getEffectiveLevel();
      if (RANDOM.nextFloat() < (scaledLevel * 0.25f)) {
        applyEffect(living, scaledLevel);
        ToolDamageUtil.damageAnimated(tool, 1, context.getEntity(), slotType);
      }
    }
  }

  @Override
  public void afterMeleeHit(IToolStackView tool, ModifierEntry modifier, ToolAttackContext context, float damageDealt) {
    applyEffect(context.getLivingTarget(), modifier.getEffectiveLevel());
  }

  @Override
  public boolean onProjectileHitEntity(ModifierNBT modifiers, NamespacedNBT persistentData, ModifierEntry modifier, Projectile projectile, EntityHitResult hit, @Nullable LivingEntity attacker, @Nullable LivingEntity target) {
    applyEffect(target, modifier.getEffectiveLevel());
    return false;
  }

  @Override
  public List<ModuleHook<?>> getDefaultHooks() {
    return DEFAULT_HOOKS;
  }

  @Override
  public RecordLoadable<MobEffectModule> getLoader() {
    return LOADER;
  }

  /** Builder for this modifier in datagen */
  @RequiredArgsConstructor(access = AccessLevel.PRIVATE)
  @Accessors(fluent = true)
  @Setter
  public static class Builder extends ModuleBuilder.Stack<Builder> {
    private final MobEffect effect;
    private IJsonPredicate<LivingEntity> target = LivingEntityPredicate.ANY;
    private RandomLevelingValue level = RandomLevelingValue.flat(1);
    private RandomLevelingValue time = RandomLevelingValue.flat(0);

    /** Builds the finished modifier */
    public MobEffectModule build() {
      return new MobEffectModule(target, effect, level, time, condition);
    }
  }
}

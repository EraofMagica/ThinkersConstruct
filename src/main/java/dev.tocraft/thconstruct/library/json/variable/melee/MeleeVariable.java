package dev.tocraft.thconstruct.library.json.variable.melee;

import net.minecraft.world.entity.LivingEntity;
import dev.tocraft.eomantle.data.loadable.record.RecordLoadable;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IGenericLoader;
import dev.tocraft.eomantle.data.registry.GenericLoaderRegistry.IHaveLoader;
import dev.tocraft.thconstruct.library.json.variable.VariableLoaderRegistry;
import dev.tocraft.thconstruct.library.modifiers.modules.combat.ConditionalMeleeDamageModule;
import dev.tocraft.thconstruct.library.tools.context.ToolAttackContext;
import dev.tocraft.thconstruct.library.tools.nbt.IToolStackView;

import javax.annotation.Nullable;

/** Variable for use in {@link ConditionalMeleeDamageModule} */
public interface MeleeVariable extends IHaveLoader {
  GenericLoaderRegistry<MeleeVariable> LOADER = new VariableLoaderRegistry<>("Melee Variable", Constant::new);

  /**
   * Gets the value of the variable
   * @param tool     Tool instance
   * @param context  Attack context, will be null in tooltips
   * @param attacker Entity using the tool, may be null conditionally in tooltips
   * @return  Value of this variable
   */
  float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker);

  
  /** Constant value instance for this object */
  record Constant(float value) implements VariableLoaderRegistry.ConstantFloat, MeleeVariable {
    public static final RecordLoadable<Constant> LOADER = VariableLoaderRegistry.constantLoader(Constant::new);

    @Override
    public float getValue(IToolStackView tool, @Nullable ToolAttackContext context, @Nullable LivingEntity attacker) {
      return value;
    }

    @Override
    public IGenericLoader<? extends MeleeVariable> getLoader() {
      return LOADER;
    }
  }
}

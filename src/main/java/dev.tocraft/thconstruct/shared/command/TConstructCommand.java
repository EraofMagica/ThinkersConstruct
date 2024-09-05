package dev.tocraft.thconstruct.shared.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import dev.tocraft.thconstruct.common.registration.ArgumentTypeDeferredRegister;
import dev.tocraft.thconstruct.shared.command.argument.MaterialArgument;
import dev.tocraft.thconstruct.shared.command.argument.ModifierArgument;
import dev.tocraft.thconstruct.shared.command.argument.ModifierHookArgument;
import dev.tocraft.thconstruct.shared.command.argument.SlotTypeArgument;
import dev.tocraft.thconstruct.shared.command.argument.ToolStatArgument;
import dev.tocraft.thconstruct.shared.command.subcommand.GeneratePartTexturesCommand;
import dev.tocraft.thconstruct.shared.command.subcommand.ModifierPriorityCommand;
import dev.tocraft.thconstruct.shared.command.subcommand.ModifierUsageCommand;
import dev.tocraft.thconstruct.shared.command.subcommand.ModifiersCommand;
import dev.tocraft.thconstruct.shared.command.subcommand.SlotsCommand;
import dev.tocraft.thconstruct.shared.command.subcommand.StatsCommand;

import java.util.function.Consumer;

public class TConstructCommand {
  private static final ArgumentTypeDeferredRegister ARGUMENT_TYPE = new ArgumentTypeDeferredRegister(dev.tocraft.thconstruct.ThConstruct.MOD_ID);

  /** Registers all TConstruct command related content */
  public static void init() {
    ARGUMENT_TYPE.register(FMLJavaModLoadingContext.get().getModEventBus());
    ARGUMENT_TYPE.registerSingleton("slot_type", SlotTypeArgument.class, SlotTypeArgument::slotType);
    ARGUMENT_TYPE.registerSingleton("tool_stat", ToolStatArgument.class, ToolStatArgument::stat);
    ARGUMENT_TYPE.registerSingleton("modifier", ModifierArgument.class, ModifierArgument::modifier);
    ARGUMENT_TYPE.registerSingleton("material", MaterialArgument.class, MaterialArgument::material);
    ARGUMENT_TYPE.registerSingleton("modifier_hook", ModifierHookArgument.class, ModifierHookArgument::modifierHook);

    // add command listener
    MinecraftForge.EVENT_BUS.addListener(TConstructCommand::registerCommand);
  }

  /** Registers a sub command for the root Mantle command */
  private static void register(LiteralArgumentBuilder<CommandSourceStack> root, String name, Consumer<LiteralArgumentBuilder<CommandSourceStack>> consumer) {
    LiteralArgumentBuilder<CommandSourceStack> subCommand = Commands.literal(name);
    consumer.accept(subCommand);
    root.then(subCommand);
  }

  /** Event listener to register the Mantle command */
  private static void registerCommand(RegisterCommandsEvent event) {
    LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(dev.tocraft.thconstruct.ThConstruct.MOD_ID);

    // sub commands
    register(builder, "modifiers", ModifiersCommand::register);
    register(builder, "tool_stats", StatsCommand::register);
    register(builder, "slots", SlotsCommand::register);
    register(builder, "report", b -> {
      register(b, "modifier_usage", ModifierUsageCommand::register);
      register(b, "modifier_priority", ModifierPriorityCommand::register);
    });
    register(builder, "generate_part_textures", GeneratePartTexturesCommand::register);

    // register final command
    event.getDispatcher().register(builder);
  }
}

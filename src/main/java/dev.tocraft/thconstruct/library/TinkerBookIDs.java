package dev.tocraft.thconstruct.library;

import net.minecraft.resources.ResourceLocation;
import dev.tocraft.eomantle.command.BookTestCommand;

/** This class can safely be accessed serverside for book IDs */
public class TinkerBookIDs {
  public static final ResourceLocation MATERIALS_BOOK_ID = dev.tocraft.thconstruct.ThConstruct.getResource("materials_and_you");
  public static final ResourceLocation MIGHTY_SMELTING_ID = dev.tocraft.thconstruct.ThConstruct.getResource("mighty_smelting");
  public static final ResourceLocation PUNY_SMELTING_ID = dev.tocraft.thconstruct.ThConstruct.getResource("puny_smelting");
  public static final ResourceLocation TINKERS_GADGETRY_ID = dev.tocraft.thconstruct.ThConstruct.getResource("tinkers_gadgetry");
  public static final ResourceLocation FANTASTIC_FOUNDRY_ID = dev.tocraft.thconstruct.ThConstruct.getResource("fantastic_foundry");
  public static final ResourceLocation ENCYCLOPEDIA_ID = dev.tocraft.thconstruct.ThConstruct.getResource("encyclopedia");

  /** Regsiters suggestions with the mantle command */
  public static void registerCommandSuggestion() {
    BookTestCommand.addBookSuggestion(MATERIALS_BOOK_ID);
    BookTestCommand.addBookSuggestion(MIGHTY_SMELTING_ID);
    BookTestCommand.addBookSuggestion(PUNY_SMELTING_ID);
    BookTestCommand.addBookSuggestion(TINKERS_GADGETRY_ID);
    BookTestCommand.addBookSuggestion(FANTASTIC_FOUNDRY_ID);
    BookTestCommand.addBookSuggestion(ENCYCLOPEDIA_ID);
  }
}

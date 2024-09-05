package dev.tocraft.thconstruct.tools.data;

import com.google.gson.JsonObject;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import dev.tocraft.thconstruct.library.data.AbstractToolItemModelProvider;
import dev.tocraft.thconstruct.tools.TinkerTools;

import java.io.IOException;

import static dev.tocraft.thconstruct.ThConstruct.getResource;

/** Provider for tool models, mostly used for duplicating displays */
public class ToolItemModelProvider extends AbstractToolItemModelProvider {
  public ToolItemModelProvider(DataGenerator generator, ExistingFileHelper existingFileHelper) {
    super(generator, existingFileHelper, dev.tocraft.thconstruct.ThConstruct.MOD_ID);
  }

  @Override
  protected void addModels() throws IOException {
    JsonObject toolBlocking = readJson(getResource("base/tool_blocking"));
    JsonObject shieldBlocking = readJson(getResource("base/shield_blocking"));

    // blocking //
    // pickaxe
    tool(TinkerTools.pickaxe, toolBlocking, "head");
    tool(TinkerTools.sledgeHammer, toolBlocking, "head", "front", "back");
    tool(TinkerTools.veinHammer, toolBlocking, "head", "front");
    // shovel
    tool(TinkerTools.pickadze, toolBlocking, "axe", "pick");
    tool(TinkerTools.mattock, toolBlocking, "axe", "pick");
    tool(TinkerTools.excavator, toolBlocking, "head");
    // axe
    tool(TinkerTools.handAxe, toolBlocking, "head");
    tool(TinkerTools.broadAxe, toolBlocking, "head", "back");
    // sword
    tool(TinkerTools.dagger, toolBlocking, "head");
    tool(TinkerTools.sword, toolBlocking, "head");
    tool(TinkerTools.cleaver, toolBlocking, "head", "shield");
    // scythe
    tool(TinkerTools.kama, toolBlocking, "head");
    tool(TinkerTools.scythe, toolBlocking, "head");
    // shield
    armor("travelers", TinkerTools.travelersGear, "tool");
    armor("plate", TinkerTools.plateArmor, "plating", "maille");
    armor("slime", TinkerTools.slimesuit, "tool");
    shield("travelers", TinkerTools.travelersShield, shieldBlocking, "tool");
    shield("plate", TinkerTools.plateShield, readJson(getResource("base/shield_large_blocking")), "plating", "core");
    // misc
    tool(TinkerTools.flintAndBrick, shieldBlocking, "tool");
    // bow
    bow(TinkerTools.longbow, toolBlocking, false, "limb_bottom", "limb_top", "bowstring", "arrow");
    bow(TinkerTools.crossbow, toolBlocking, true, "bowstring");
    // staff
    staff(TinkerTools.skyStaff, toolBlocking);
    staff(TinkerTools.earthStaff, toolBlocking);
    staff(TinkerTools.ichorStaff, toolBlocking);
    staff(TinkerTools.enderStaff, toolBlocking);
  }

  @Override
  public String getName() {
    return "Tinkers Construct Tool Item Model Provider";
  }
}

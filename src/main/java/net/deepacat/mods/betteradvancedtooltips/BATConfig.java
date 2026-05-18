package net.deepacat.mods.betteradvancedtooltips;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

public class BATConfig {
	public static ForgeConfigSpec.ConfigValue<Boolean> removeCreativeTabTooltip;
	public static ForgeConfigSpec.ConfigValue<Boolean> removeComponentCountTooltip;
	public static ForgeConfigSpec.ConfigValue<Boolean> nbtTooltip;
	public static ForgeConfigSpec.ConfigValue<Boolean> tagTooltip;
	public static ForgeConfigSpec.ConfigValue<Boolean> fuelTooltip;
	public static ForgeConfigSpec.ConfigValue<Boolean> energyTooltip;
	public static ForgeConfigSpec.ConfigValue<Boolean> foodTooltip;

	public static void registerClientConfig() {
		ForgeConfigSpec.Builder CLIENT = new ForgeConfigSpec.Builder();
		CLIENT.comment("Tooltip Settings").push("settings");

		removeCreativeTabTooltip = CLIENT.comment("Remove creative tab tooltips from items in creative menu").define("removeCreativeTabTooltip", true);
		removeComponentCountTooltip = CLIENT.comment("Remove text telling how many nbt tags an item has").define("removeComponentCountTooltip", true);
		nbtTooltip = CLIENT.comment("Displays an items NBT data (ALT)").define("componentTooltip", true);
		tagTooltip = CLIENT.comment("Displays the item/block/fluid and other tags on items (SHIFT)").define("tagTooltip", true);
		fuelTooltip = CLIENT.comment("Displays furnace/smelting fuel values (SHIFT)").define("fuelTooltip", true);
		energyTooltip = CLIENT.comment("Displays item energy capabilities (SHIFT)").define("energyTooltip", true);
		foodTooltip = CLIENT.comment("Displays food and saturation values (SHIFT)").define("foodTooltip", true);

		CLIENT.pop();

		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, CLIENT.build(), "better-advanced-tooltips-client.toml");
	}
}
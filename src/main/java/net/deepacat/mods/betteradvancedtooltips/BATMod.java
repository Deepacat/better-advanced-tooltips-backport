package net.deepacat.mods.betteradvancedtooltips;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import org.slf4j.Logger;

@Mod(BATMod.MODID)
public class BATMod {
	public static final String MODID = "betteradvancedtooltips";
	public static final String NAME = "Better Advanced Tooltips";

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public BATMod() {
		BATConfig.registerClientConfig();
	}
}

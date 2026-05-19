package net.deepacat.mods.betteradvancedtooltips;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BATMod.MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = BATMod.MODID)
public class BATMod {
	public static final String MODID = "betteradvancedtooltips";
	public static final String NAME = "Better Advanced Tooltips";
	public static final Logger LOGGER = LogManager.getLogger(MODID);

	public static ResourceLocation id(String path) {
		return ResourceLocation.fromNamespaceAndPath(MODID, path);
	}

	public BATMod() {
		Config.registerClientConfig();
	}
}

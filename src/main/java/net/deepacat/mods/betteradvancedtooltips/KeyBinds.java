package net.deepacat.mods.betteradvancedtooltips;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.settings.IKeyConflictContext;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.glfw.GLFW;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT, modid = BATMod.MODID)
public class KeyBinds {

	private static final IKeyConflictContext NO_CONFLICT = new IKeyConflictContext() {
		@Override
		public boolean isActive() {
			return Minecraft.getInstance().screen != null;
		}

		@Override
		public boolean conflicts(IKeyConflictContext other) {
			return false;
		}
	};

	public static final KeyMapping VIEW_TAGS = new KeyMapping(
		"key." + BATMod.MODID + ".view_tags",
		NO_CONFLICT,
		InputConstants.getKey(InputConstants.KEY_LSHIFT, -1),
		"key.categories." + BATMod.MODID
	);

	public static final KeyMapping VIEW_NBT = new KeyMapping(
		"key." + BATMod.MODID + ".view_nbt",
		NO_CONFLICT,
		InputConstants.getKey(InputConstants.KEY_LALT, -1),
		"key.categories." + BATMod.MODID
	);

	@SubscribeEvent
	public static void registerKeys(RegisterKeyMappingsEvent event) {
		event.register(VIEW_TAGS);
		event.register(VIEW_NBT);
	}

	public static boolean isKeyPressed(KeyMapping keyBinding) {
		InputConstants.Key key = keyBinding.getKey();
		long window = Minecraft.getInstance().getWindow().getWindow();

		if (key.getType() == InputConstants.Type.KEYSYM) {
			return InputConstants.isKeyDown(window, key.getValue());
		} else if (key.getType() == InputConstants.Type.MOUSE) {
			return GLFW.glfwGetMouseButton(window, key.getValue()) == GLFW.GLFW_PRESS;
		}
		return false;
	}
}
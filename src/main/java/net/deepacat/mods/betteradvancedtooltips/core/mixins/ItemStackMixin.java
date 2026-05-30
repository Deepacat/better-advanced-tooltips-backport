package net.deepacat.mods.betteradvancedtooltips.core.mixins;

import net.deepacat.mods.betteradvancedtooltips.Config;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.contents.TranslatableContents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

// Removes the "NBT Tags: x" text from tooltips
@Mixin(ItemStack.class)
public class ItemStackMixin {
	@Inject(method = "getTooltipLines", at = @At("RETURN"))
	private void bat$postProcessTooltip(Player pPlayer, TooltipFlag pIsAdvanced, CallbackInfoReturnable<List<Component>> cir) {
		if (Config.removeComponentCountTooltip.get()) {
			List<Component> tooltip = cir.getReturnValue();
			tooltip.removeIf(component -> {
				if (component.getContents() instanceof TranslatableContents translatableContents) {
					return "item.nbt_tags".equals(translatableContents.getKey());
				}
				return false;
			});
		}
	}
}

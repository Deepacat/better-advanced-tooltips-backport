package net.deepacat.mods.betteradvancedtooltips.core.mixins;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import net.deepacat.mods.betteradvancedtooltips.BATConfig;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

// Disables the "NBT Tags: x" on tooltips
@Mixin(ItemStack.class)
public class ItemStackMixin {
	@ModifyExpressionValue(
		method = "getTooltipLines",
		at = @At(value = "INVOKE",
			target = "Lnet/minecraft/world/item/ItemStack;hasTag()Z")
	)
	private boolean bat$getTooltipLines(boolean original) {
		if (original && BATConfig.removeComponentCountTooltip.get()) {
			return false;
		}
		return original;
	}
}

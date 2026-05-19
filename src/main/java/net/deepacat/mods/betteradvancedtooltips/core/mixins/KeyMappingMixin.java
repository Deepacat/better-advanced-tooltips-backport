package net.deepacat.mods.betteradvancedtooltips.core.mixins;

import net.minecraft.client.KeyMapping;
import net.deepacat.mods.betteradvancedtooltips.KeyBinds; // your combined class
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyMapping.class)
public class KeyMappingMixin {
	@Inject(method = "same", at = @At("HEAD"), cancellable = true)
	private void ignoreConflicts(KeyMapping other, CallbackInfoReturnable<Boolean> cir) {
		KeyMapping self = (KeyMapping)(Object)this;
		if (self == KeyBinds.VIEW_TAGS || self == KeyBinds.VIEW_NBT ||
			other == KeyBinds.VIEW_TAGS || other == KeyBinds.VIEW_NBT) {
			cir.setReturnValue(false); // never report a conflict
		}
	}
}
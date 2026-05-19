package net.deepacat.mods.betteradvancedtooltips;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.LinkedHashSet;
import java.util.Set;

public class TagInstance implements Comparable<TagInstance> {
	public final ResourceLocation tag;
	public final Set<TooltipTagType<?>> registries;

	public TagInstance(ResourceLocation tag) {
		this.tag = tag;
		this.registries = new LinkedHashSet<>(2);
	}

	public Component toText() {
		var component = Component.empty();
		component.append(Icons.TAG);
		component.append(Icons.SMALL_SPACE);
		component.append(Component.literal("#" + tag).withStyle(ChatFormatting.DARK_GRAY));
		component.append(Icons.SMALL_SPACE);

		for (var type : registries) {
			component.append(type.component());
		}

		return component;
	}

	@Override
	public int compareTo(@NotNull TagInstance o) {
		return tag.compareNamespaced(o.tag);
	}
}

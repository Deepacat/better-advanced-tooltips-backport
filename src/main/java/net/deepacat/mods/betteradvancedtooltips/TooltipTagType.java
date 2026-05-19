package net.deepacat.mods.betteradvancedtooltips;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.material.Fluid;

public record TooltipTagType<T>(ResourceKey<? extends Registry<T>> registryKey, Component component) {
	private static final Style BUILTIN_TAG_ICONS = Style.EMPTY.withFont(BATMod.id("tags")).applyFormat(ChatFormatting.WHITE);

	public static final TooltipTagType<BannerPattern> BANNER_PATTERN = new TooltipTagType<>(Registries.BANNER_PATTERN, "A");
	public static final TooltipTagType<Block> BLOCK = new TooltipTagType<>(Registries.BLOCK, "B");
	public static final TooltipTagType<BlockEntityType<?>> BLOCK_ENTITY_TYPE = new TooltipTagType<>(Registries.BLOCK_ENTITY_TYPE, "R");
	public static final TooltipTagType<Enchantment> ENCHANTMENT = new TooltipTagType<>(Registries.ENCHANTMENT, "C");
	public static final TooltipTagType<EntityType<?>> ENTITY_TYPE = new TooltipTagType<>(Registries.ENTITY_TYPE, "E");
	public static final TooltipTagType<Fluid> FLUID = new TooltipTagType<>(Registries.FLUID, "F");
	public static final TooltipTagType<Item> ITEM = new TooltipTagType<>(Registries.ITEM, "I");
	public static final TooltipTagType<Instrument> INSTRUMENT = new TooltipTagType<>(Registries.INSTRUMENT, "N");
	public static final TooltipTagType<PaintingVariant> PAINTING_VARIANT = new TooltipTagType<>(Registries.PAINTING_VARIANT, "P");

	public TooltipTagType(ResourceKey<? extends Registry<T>> registryKey, Style style, String symbol) {
		this(registryKey, Component.literal(symbol).setStyle(style));
	}

	private TooltipTagType(ResourceKey<? extends Registry<T>> registryKey, String symbol) {
		this(registryKey, BUILTIN_TAG_ICONS, symbol);
	}

	@Override
	public int hashCode() {
		return registryKey.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof TooltipTagType<?> t && registryKey == t.registryKey;
	}

	@Override
	public String toString() {
		return registryKey.location().toString();
	}
}

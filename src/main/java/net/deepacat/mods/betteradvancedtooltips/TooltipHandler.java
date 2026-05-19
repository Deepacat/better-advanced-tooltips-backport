package net.deepacat.mods.betteradvancedtooltips;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.item.BannerPatternItem;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;
import java.util.stream.Stream;

import static net.deepacat.mods.betteradvancedtooltips.KeyBinds.isKeyPressed;

@Mod.EventBusSubscriber(modid = BATMod.MODID, value = Dist.CLIENT)
public class TooltipHandler {

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onItemTooltip(ItemTooltipEvent event) {
		Minecraft mc = Minecraft.getInstance();
		if (mc.level == null) {
			return;
		}

		if (!event.getFlags().isAdvanced()) {
			return;
		}

		ItemStack stack = event.getItemStack();
		if (stack.isEmpty()) {
			return;
		}

		var registryAccess = mc.level.registryAccess();
		var lines = event.getToolTip();

		boolean showNBT = isKeyPressed(KeyBinds.VIEW_NBT);
		boolean showTagInfo = isKeyPressed(KeyBinds.VIEW_TAGS);

		// Alt: shows all item NBT data
		if (showNBT && Config.nbtTooltip.get()) {
			CompoundTag tag = stack.getTag();
			if (tag != null && !tag.isEmpty()) {
				for (String key : tag.getAllKeys()) {
					Tag nbt = tag.get(key);
					MutableComponent line = Component.empty();
					line.append(Icons.PATCHED_COMPONENT);
					line.append(Icons.SMALL_SPACE);
					line.append(Component.literal(key).withStyle(ChatFormatting.YELLOW));
					line.append(Component.literal("="));
					appendNbtValue(line, nbt);
					lines.add(line);
				}
			}
		}

		// Shift: fuel + tag tooltip
		else if (showTagInfo) {
			if (Config.fuelTooltip.get()) {
				int fuel = ForgeHooks.getBurnTime(stack, RecipeType.SMELTING);
				if (fuel > 0) {
					MutableComponent line = Component.empty();
					line.append(Icons.FIRE);
					line.append(Icons.SMALL_SPACE);
					MutableComponent txt = Component.empty().withStyle(ChatFormatting.GOLD);
					txt.append("Fuel: ");
					String s = String.valueOf(fuel / 20F);
					txt.append(Component.literal(fuel + " t").withStyle(ChatFormatting.YELLOW));
					txt.append(" | ");
					txt.append(Component.literal((s.endsWith(".0") ? s.substring(0, s.length() - 2) : s) + " s").withStyle(ChatFormatting.YELLOW));
					txt.append(" | ");
					String i = String.valueOf(fuel / 200F);
					txt.append(Component.literal((i.endsWith(".0") ? i.substring(0, i.length() - 2) : i) + "x").withStyle(ChatFormatting.YELLOW));
					line.append(txt);
					lines.add(line);
				}
			}

			// Food & Saturation tooltip
			if (Config.foodTooltip.get()) {
				FoodProperties food = stack.getFoodProperties(null);
				if (food != null) {
					MutableComponent line = Component.empty();
					// Always show food icon
					line.append(Icons.FOOD);
					line.append(Icons.SMALL_SPACE);

					// Analyse effects
					boolean hasHarmful = false;
					boolean hasBeneficial = false;

					for (com.mojang.datafixers.util.Pair<MobEffectInstance, Float> pair : food.getEffects()) {
						MobEffectInstance effectInstance = pair.getFirst();
						if (effectInstance != null) {
							MobEffect effect = effectInstance.getEffect();
							if (effect.isBeneficial()) {
								hasBeneficial = true;
							} else {
								hasHarmful = true;
							}
						}
					}

					// Hardcoded harmful items (chance‑based negative effects may not appear in effects list)
					Item item = stack.getItem();
					if (item == Items.PUFFERFISH || item == Items.ROTTEN_FLESH ||
						item == Items.POISONOUS_POTATO || item == Items.SPIDER_EYE ||
						item == Items.CHICKEN || item == Items.RABBIT) {
						hasHarmful = true;
					}

					// Choose second icon: poison if harmful, heart if beneficial only, none otherwise
					if (hasHarmful) {
						line.append(Icons.POISON);
						line.append(Icons.SMALL_SPACE);
					} else if (hasBeneficial) {
						line.append(Icons.HEART);
						line.append(Icons.SMALL_SPACE);
					}

					// Food stats
					int nutrition = food.getNutrition();
					float saturationMod = food.getSaturationModifier();
					float saturation = nutrition * saturationMod * 2.0f;

					MutableComponent txt = Component.empty().withStyle(ChatFormatting.GOLD);
					txt.append("Food: ");
					txt.append(Component.literal(String.valueOf(nutrition)).withStyle(ChatFormatting.YELLOW));
					txt.append(" (");
					String satStr = String.format("%.1f", saturation);
					txt.append(Component.literal(satStr).withStyle(ChatFormatting.YELLOW));
					txt.append(" saturation)");

					line.append(txt);
					lines.add(line);
				}
			}

			// Energy tooltip (FE capability)
			if (Config.energyTooltip.get()) {
				stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energy -> {
					int stored = energy.getEnergyStored();
					int capacity = energy.getMaxEnergyStored();
					if (capacity > 0) {
						MutableComponent line = Component.empty();
						line.append(Icons.ENERGY);
						line.append(Icons.SMALL_SPACE);
						MutableComponent txt = Component.empty().withStyle(ChatFormatting.GOLD);
						txt.append("Energy: ");
						txt.append(Component.literal(stored + " / " + capacity).withStyle(ChatFormatting.YELLOW));
						txt.append(" FE");
						line.append(txt);
						lines.add(line);
					}
				});
			}

			// Tags
			if (Config.tagTooltip.get()) {
				Map<ResourceLocation, TagInstance> tempTagNames = new LinkedHashMap<>();
				ItemTagIconsEvent tEvent = new ItemTagIconsEvent(event, tempTagNames);

				// Item tags
				addTagsForItem(tEvent, stack.getItem(), registryAccess);

				// Block tags (if block item)
				if (stack.getItem() instanceof BlockItem blockItem) {
					addTagsForBlock(tEvent, blockItem.getBlock(), registryAccess);
				}

				// Fluid tags (if bucket)
				if (stack.getItem() instanceof BucketItem bucketItem) {
					Fluid fluid = bucketItem.getFluid();
					if (fluid != Fluids.EMPTY) {
						addTagsForFluid(tEvent, fluid, registryAccess);
					}
				}

				// Entity type tags (if a spawn egg)
				if (stack.getItem() instanceof SpawnEggItem spawnEgg) {
					EntityType<?> entityType = spawnEgg.getType(stack.getTag());
					if (entityType != null) {
						addTagsForEntityType(tEvent, entityType, registryAccess);
					}
				}

				// Enchantment tags (single stored enchantment)
				CompoundTag enchantTag = stack.getTagElement("StoredEnchantments");
				if (enchantTag != null && enchantTag.contains("id", Tag.TAG_STRING)) {
					ResourceLocation enchId = ResourceLocation.tryParse(enchantTag.getString("id"));
					if (enchId != null) {
						Registry<Enchantment> enchantRegistry = registryAccess.registryOrThrow(Registries.ENCHANTMENT);
						enchantRegistry.getHolder(ResourceKey.create(Registries.ENCHANTMENT, enchId))
							.ifPresent(holder -> {
								Stream<TagKey<Enchantment>> tagStream = holder.tags();
								tEvent.append(TooltipTagType.ENCHANTMENT, tagStream);
							});
					}
				}

				// Instrument tags (goat horn)
				if (stack.hasTag() && stack.getTag().contains("instrument", Tag.TAG_STRING)) {
					ResourceLocation instrId = ResourceLocation.tryParse(stack.getTag().getString("instrument"));
					if (instrId != null) {
						Registry<Instrument> instrumentRegistry = registryAccess.registryOrThrow(Registries.INSTRUMENT);
						instrumentRegistry.getHolder(ResourceKey.create(Registries.INSTRUMENT, instrId))
							.ifPresent(holder -> {
								Stream<TagKey<Instrument>> tagStream = holder.tags();
								tEvent.append(TooltipTagType.INSTRUMENT, tagStream);
							});
					}
				}

				// Painting variant tags (painting entity with custom data)
				if (stack.getItem() == Items.PAINTING) {
					if (stack.hasTag() && stack.getTag().contains("EntityTag", Tag.TAG_COMPOUND)) {
						CompoundTag entityTag = stack.getTag().getCompound("EntityTag");
						if (entityTag.contains("variant", Tag.TAG_STRING)) {
							ResourceLocation variantId = ResourceLocation.tryParse(entityTag.getString("variant"));
							if (variantId != null) {
								Registry<PaintingVariant> paintingRegistry = registryAccess.registryOrThrow(Registries.PAINTING_VARIANT);
								paintingRegistry.getHolder(ResourceKey.create(Registries.PAINTING_VARIANT, variantId))
									.ifPresent(holder -> {
										Stream<TagKey<PaintingVariant>> tagStream = holder.tags();
										tEvent.append(TooltipTagType.PAINTING_VARIANT, tagStream);
									});
							}
						}
					}
				}

				// Banner pattern tags
				if (stack.getItem() instanceof BannerPatternItem bannerItem) {
					TagKey<BannerPattern> patternTag = bannerItem.getBannerPattern();
					tEvent.append(TooltipTagType.BANNER_PATTERN, Stream.of(patternTag));
				}

				// Fire event for other mods
				MinecraftForge.EVENT_BUS.post(tEvent);

				// Add lines
				tempTagNames.values().stream()
					.sorted()
					.map(TagInstance::toText)
					.forEach(lines::add);
			}
		}
	}

	// NBT text prettifier
	private static void appendNbtValue(MutableComponent line, Tag tag) {
		try {
			line.append(NbtUtils.toPrettyComponent(tag));
		} catch (Throwable ex) {
			line.append(Component.literal(tag.getAsString()).withStyle(ChatFormatting.RED));
		}
	}

	private static <T> void addTagsToEvent(ItemTagIconsEvent event, TooltipTagType<T> type,
										   ResourceKey<T> objectKey, Registry<T> registry) {
		registry.getHolder(objectKey).ifPresent(holder -> {
			Stream<TagKey<T>> tagStream = holder.tags();
			event.append(type, tagStream);
		});
	}

	private static void addTagsForItem(ItemTagIconsEvent event,
									   Item item, RegistryAccess registryAccess) {
		ResourceKey<Item> key = ForgeRegistries.ITEMS.getResourceKey(item).orElse(null);
		if (key != null) {
			Registry<Item> registry = registryAccess.registryOrThrow(Registries.ITEM);
			addTagsToEvent(event, TooltipTagType.ITEM, key, registry);
		}
	}

	private static void addTagsForBlock(ItemTagIconsEvent event,
										Block block, RegistryAccess registryAccess) {
		ResourceKey<Block> key = ForgeRegistries.BLOCKS.getResourceKey(block).orElse(null);
		if (key != null) {
			Registry<Block> registry = registryAccess.registryOrThrow(Registries.BLOCK);
			addTagsToEvent(event, TooltipTagType.BLOCK, key, registry);
		}
	}

	private static void addTagsForFluid(ItemTagIconsEvent event,
										Fluid fluid, RegistryAccess registryAccess) {
		ResourceKey<Fluid> key = ForgeRegistries.FLUIDS.getResourceKey(fluid).orElse(null);
		if (key != null) {
			Registry<Fluid> registry = registryAccess.registryOrThrow(Registries.FLUID);
			addTagsToEvent(event, TooltipTagType.FLUID, key, registry);
		}
	}

	private static void addTagsForEntityType(ItemTagIconsEvent event,
											 EntityType<?> entityType, RegistryAccess registryAccess) {
		ResourceKey<EntityType<?>> key = ForgeRegistries.ENTITY_TYPES.getResourceKey(entityType).orElse(null);
		if (key != null) {
			Registry<EntityType<?>> registry = registryAccess.registryOrThrow(Registries.ENTITY_TYPE);
			addTagsToEvent(event, TooltipTagType.ENTITY_TYPE, key, registry);
		}
	}
}
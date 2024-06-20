package com.quantumgoddess;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.quantumgoddess.component.ModEnchantmentEffectComponentTypes;
import com.quantumgoddess.enchantment.effect.entity.CastProjectileEnchantmentEffect;
import com.quantumgoddess.item.ModItems;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ExampleMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
	public static final Logger LOGGER = LoggerFactory.getLogger("modid");
	public static ModItems ITEMS = new ModItems();
	public static ModEnchantmentEffectComponentTypes ENCHANTMENT_EFFECT_COMPONENT_TYPES = new ModEnchantmentEffectComponentTypes();

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		Registry.register(Registries.ENCHANTMENT_ENTITY_EFFECT_TYPE, Identifier.of("quantummagic", "projectile_spell"), CastProjectileEnchantmentEffect.CODEC);
		LOGGER.info("Hello Fabric world!");
	}
}
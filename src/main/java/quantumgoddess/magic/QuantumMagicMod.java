package quantumgoddess.magic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import quantumgoddess.magic.entity.attribute.MagicEntityAttributes;
import quantumgoddess.magic.item.Items;

public class QuantumMagicMod implements ModInitializer {
	// This logger is used to write text to the console and the log file.
	// It is considered best practice to use your mod id as the logger's name.
	// That way, it's clear which mod wrote info, warnings, and errors.
    public static final Logger LOGGER = LoggerFactory.getLogger("quantumpack-magic");

	@Override
	public void onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.

		LOGGER.info("Hello Fabric world!");
		MagicEntityAttributes.registerAll();

		Registry.register(Registries.ITEM, new Identifier("quantumpack-magic", "wand"), Items.BasicFireWand);
	}
}
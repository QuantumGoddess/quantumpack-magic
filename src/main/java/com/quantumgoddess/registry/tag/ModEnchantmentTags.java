package com.quantumgoddess.registry.tag;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public interface ModEnchantmentTags {
    public static final TagKey<Enchantment> PROJECTILE_SPELLS = ModEnchantmentTags.of("projectile_spells");

    private static TagKey<Enchantment> of(String id) {
        return TagKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("quantummagic", id));
    }
}

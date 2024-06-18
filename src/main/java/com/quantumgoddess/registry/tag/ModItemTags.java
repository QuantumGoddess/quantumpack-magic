package com.quantumgoddess.registry.tag;

import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModItemTags {

    public static final TagKey<Item> WANDS = ModItemTags.of("wands");
    public static final TagKey<Item> SPELL_ENCHANTABLE = ModItemTags.of("enchantable/spell");

    private ModItemTags() {
    }

    private static TagKey<Item> of(String id) {
        return TagKey.of(RegistryKeys.ITEM, Identifier.of("quantummagic", id));
    }
}

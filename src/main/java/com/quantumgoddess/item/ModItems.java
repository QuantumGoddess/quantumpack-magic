package com.quantumgoddess.item;

import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ToolMaterials;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

public class ModItems {

    public static final Item WOODEN_WAND = register("wooden_wand", (Item) new WandItem(ToolMaterials.WOOD, 5.0f, new Item.Settings().maxCount(1).maxDamage(465).component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT)));
    public static final Item DIAMOND_WAND = register("diamond_wand", (Item) new WandItem(ToolMaterials.DIAMOND, 1.0f, new Item.Settings().maxCount(1).maxDamage(465).component(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT)));

    public static Item register(String id, Item item) {
        return register(Identifier.of("quantummagic", id), item);
    }

    public static Item register(Identifier id, Item item) {
        return register(RegistryKey.of(Registries.ITEM.getKey(), id), item);
    }

    public static Item register(RegistryKey<Item> key, Item item) {
        if (item instanceof BlockItem) {
            ((BlockItem) item).appendBlocks(Item.BLOCK_ITEMS, item);
        }
        return Registry.register(Registries.ITEM, key, item);
    }
}

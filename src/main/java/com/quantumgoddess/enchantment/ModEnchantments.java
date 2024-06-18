package com.quantumgoddess.enchantment;

import java.util.Optional;

import com.quantumgoddess.registry.tag.ModItemTags;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelBasedValue;
import net.minecraft.enchantment.effect.DamageImmunityEnchantmentEffect;
import net.minecraft.enchantment.effect.entity.ReplaceDiskEnchantmentEffect;
import net.minecraft.entity.damage.DamageType;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.Item;
import net.minecraft.loot.condition.DamageSourcePropertiesLootCondition;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.predicate.TagPredicate;
import net.minecraft.predicate.entity.DamageSourcePredicate;
import net.minecraft.predicate.entity.EntityFlagsPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.BlockTags;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.gen.blockpredicate.BlockPredicate;
import net.minecraft.world.gen.stateprovider.BlockStateProvider;

public class ModEnchantments {

    public static final RegistryKey<Enchantment> FIREBOLT = ModEnchantments.of("firebolt");

    public static void bootstrap(Registerable<Enchantment> registry) {
        RegistryEntryLookup<DamageType> registryEntryLookup = registry.getRegistryLookup(RegistryKeys.DAMAGE_TYPE);
        RegistryEntryLookup<Enchantment> registryEntryLookup2 = registry.getRegistryLookup(RegistryKeys.ENCHANTMENT);
        RegistryEntryLookup<Item> registryEntryLookup3 = registry.getRegistryLookup(RegistryKeys.ITEM);
        RegistryEntryLookup<Block> registryEntryLookup4 = registry.getRegistryLookup(RegistryKeys.BLOCK);

        ModEnchantments.register(registry, FIREBOLT, Enchantment.builder(Enchantment.definition(registryEntryLookup3.getOrThrow(ModItemTags.SPELL_ENCHANTABLE), 10, 3, Enchantment.leveledCost(10, 10), Enchantment.leveledCost(25, 10), 4, AttributeModifierSlot.MAINHAND)).exclusiveSet(registryEntryLookup2.getOrThrow(EnchantmentTags.BOOTS_EXCLUSIVE_SET)).addEffect(EnchantmentEffectComponentTypes.DAMAGE_IMMUNITY, DamageImmunityEnchantmentEffect.INSTANCE, DamageSourcePropertiesLootCondition.builder(DamageSourcePredicate.Builder.create().tag(TagPredicate.expected(DamageTypeTags.BURN_FROM_STEPPING)).tag(TagPredicate.unexpected(DamageTypeTags.BYPASSES_INVULNERABILITY)))).addEffect(EnchantmentEffectComponentTypes.LOCATION_CHANGED,
                new ReplaceDiskEnchantmentEffect(new EnchantmentLevelBasedValue.Clamped(EnchantmentLevelBasedValue.linear(3.0f, 1.0f), 0.0f, 16.0f), EnchantmentLevelBasedValue.constant(1.0f), new Vec3i(0, -1, 0), Optional.of(BlockPredicate.allOf(BlockPredicate.matchingBlockTag(new Vec3i(0, 1, 0), BlockTags.AIR), BlockPredicate.matchingBlocks(Blocks.WATER), BlockPredicate.matchingFluids(Fluids.WATER), BlockPredicate.unobstructed())), BlockStateProvider.of(Blocks.FROSTED_ICE), Optional.of(GameEvent.BLOCK_PLACE)), EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().flags(EntityFlagsPredicate.Builder.create().onGround(true)))));
    }

    private static void register(Registerable<Enchantment> registry, RegistryKey<Enchantment> key, Enchantment.Builder builder) {
        registry.register(key, builder.build(key.getValue()));
    }

    private static RegistryKey<Enchantment> of(String id) {
        return RegistryKey.of(RegistryKeys.ENCHANTMENT, Identifier.of("quantummagic", id));
    }
}

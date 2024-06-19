package com.quantumgoddess.mixin;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.effect.EnchantmentEffectEntry;
import net.minecraft.entity.Entity;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

@Mixin(Enchantment.class)
public interface EnchantmentInvoker {

    @Invoker
    public static <T> void callApplyEffects(List<EnchantmentEffectEntry<T>> entries, LootContext lootContext, Consumer<T> effectConsumer) {

    }

    @Invoker
    public static LootContext callCreateEnchantedEntityLootContext(ServerWorld world, int level, Entity entity, Vec3d pos) {
        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world).add(LootContextParameters.THIS_ENTITY, entity).add(LootContextParameters.ENCHANTMENT_LEVEL, level).add(LootContextParameters.ORIGIN, pos).build(LootContextTypes.ENCHANTED_ENTITY);
        return new LootContext.Builder(lootContextParameterSet).build(Optional.empty());
    }

}

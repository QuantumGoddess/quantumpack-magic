package com.quantumgoddess.enchantment.effect.entity;

import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public interface EnchantmentSpellEffect extends EnchantmentEntityEffect {

    @Override
    default void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        castSpell(world, level, context, user, pos);
    }

    abstract void castSpell(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos);
}

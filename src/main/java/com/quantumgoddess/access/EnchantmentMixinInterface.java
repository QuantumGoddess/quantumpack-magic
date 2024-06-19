package com.quantumgoddess.access;

import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public interface EnchantmentMixinInterface {

    void onItemUsed(ServerWorld world, int level, EnchantmentEffectContext context, ItemStack stack, LivingEntity user, EquipmentSlot slot);

}

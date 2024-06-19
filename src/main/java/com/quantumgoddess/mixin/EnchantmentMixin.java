package com.quantumgoddess.mixin;

import org.spongepowered.asm.mixin.Mixin;

import com.quantumgoddess.access.EnchantmentMixinInterface;
import com.quantumgoddess.component.ModEnchantmentEffectComponentTypes;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

@Mixin(Enchantment.class)
public class EnchantmentMixin implements EnchantmentMixinInterface {

    @Override
    public void onItemUsed(ServerWorld world, int level, EnchantmentEffectContext context, ItemStack stack, LivingEntity user, EquipmentSlot slot) {
        EnchantmentInvoker.callApplyEffects(((Enchantment) (Object) this).getEffect(ModEnchantmentEffectComponentTypes.ITEM_USED), EnchantmentInvoker.callCreateEnchantedEntityLootContext(world, level, user, user.getPos()), effect -> effect.apply(world, level, context, user, user.getPos()));
    }
}

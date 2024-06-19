package com.quantumgoddess.enchantment;

import com.quantumgoddess.access.EnchantmentMixinInterface;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.ItemEnchantmentsComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.world.ServerWorld;

public class ModEnchantmentHelper {

    public static void onItemUsed(ServerWorld world, ItemStack stack, LivingEntity user, EquipmentSlot slot, java.util.function.Consumer<Item> onBreak) {
        EnchantmentEffectContext enchantmentEffectContext = new EnchantmentEffectContext(stack, slot, user, onBreak);
        ModEnchantmentHelper.forEachEnchantment(stack, (RegistryEntry<Enchantment> enchantment, int level) -> ((EnchantmentMixinInterface) (Object) ((Enchantment) enchantment.value())).onItemUsed(world, level, enchantmentEffectContext, stack, user, slot));
    }

    private static void forEachEnchantment(ItemStack stack, Consumer consumer) {
        ItemEnchantmentsComponent itemEnchantmentsComponent = stack.getOrDefault(DataComponentTypes.ENCHANTMENTS, ItemEnchantmentsComponent.DEFAULT);
        for (Object2IntMap.Entry<RegistryEntry<Enchantment>> entry : itemEnchantmentsComponent.getEnchantmentEntries()) {
            consumer.accept((RegistryEntry) entry.getKey(), entry.getIntValue());
        }
    }

    @FunctionalInterface
    static interface Consumer {
        public void accept(RegistryEntry<Enchantment> var1, int var2);
    }
}

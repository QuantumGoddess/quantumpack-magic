package com.quantumgoddess.access;

import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;

public interface EnchantmentHelperMixinInterface {

    static void onSpellFired(ServerWorld world, ItemStack weaponStack, PersistentProjectileEntity projectileEntity, java.util.function.Consumer<Item> onBreak) {

    }
}

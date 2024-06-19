package com.quantumgoddess.enchantment.effect.entity;

import java.util.Optional;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.enchantment.EnchantmentEffectContext;
import net.minecraft.enchantment.effect.EnchantmentEntityEffect;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryCodecs;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public record LoadProjectileSpellEnchantmentEffect(RegistryEntryList<EntityType<?>> entityTypes) implements EnchantmentEntityEffect {

    public static final MapCodec<LoadProjectileSpellEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec<RegistryEntryList<EntityType<?>>>) RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("entity")).forGetter(LoadProjectileSpellEnchantmentEffect::entityTypes)).apply(instance, LoadProjectileSpellEnchantmentEffect::new));

    @Override
    public void apply(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {

        BlockPos blockPos = BlockPos.ofFloored(pos);
        if (!World.isValid(blockPos)) {
            return;
        }
        Optional<RegistryEntry<EntityType<?>>> optional = this.entityTypes().getRandom(world.getRandom());
        if (optional.isEmpty()) {
            return;
        }
        ProjectileEntity projectileEntity = null;
        LivingEntity shooter = context.owner();
        ItemStack wandStack = shooter.getMainHandStack();
        EntityType<?> entityType = optional.get().value();

        if (entityType == EntityType.FIREWORK_ROCKET) {
            projectileEntity = new FireworkRocketEntity(world, wandStack, shooter,
                    shooter.getX(), shooter.getEyeY() - (double) 0.15f, shooter.getZ(), true);
        } else if (entityType == EntityType.ARROW) {
            projectileEntity = new ArrowEntity(world, shooter, wandStack.copyWithCount(1), wandStack);
        }

        if (projectileEntity == null) {
            return;
        }

        if (projectileEntity instanceof PersistentProjectileEntity) {
            ((PersistentProjectileEntity) projectileEntity).setCritical(true);
            ((PersistentProjectileEntity) projectileEntity).setSound(SoundEvents.ITEM_CROSSBOW_HIT);
        }
        Vector3f vector3f;
        Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
        Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double) (0 * 0.017453292F), vec3d.x, vec3d.y, vec3d.z);
        Vec3d vec3d2 = shooter.getRotationVec(1.0F);
        vector3f = vec3d2.toVector3f().rotate(quaternionf);

        projectileEntity.setVelocity((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), 1.6F, 1.0F);
        float h = 1.0F;
        shooter.getWorld().playSound((PlayerEntity) null, shooter.getX(), shooter.getY(), shooter.getZ(), SoundEvents.ITEM_CROSSBOW_SHOOT, shooter.getSoundCategory(), 1.0F, h);
        world.spawnEntity(projectileEntity);
    }

    public MapCodec<LoadProjectileSpellEnchantmentEffect> getCodec() {
        return CODEC;
    }
}

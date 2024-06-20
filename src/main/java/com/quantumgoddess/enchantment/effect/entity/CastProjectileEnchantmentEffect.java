package com.quantumgoddess.enchantment.effect.entity;

import java.util.Optional;

import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.enchantment.EnchantmentEffectContext;
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
import net.minecraft.util.math.Vec3d;

public record CastProjectileEnchantmentEffect(RegistryEntryList<EntityType<?>> entityTypes) implements EnchantmentSpellEffect {

    public static final MapCodec<CastProjectileEnchantmentEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(((MapCodec<RegistryEntryList<EntityType<?>>>) RegistryCodecs.entryList(RegistryKeys.ENTITY_TYPE).fieldOf("entity")).forGetter(CastProjectileEnchantmentEffect::entityTypes)).apply(instance, CastProjectileEnchantmentEffect::new));

    @Override
    public void castSpell(ServerWorld world, int level, EnchantmentEffectContext context, Entity user, Vec3d pos) {
        Optional<RegistryEntry<EntityType<?>>> optional = this.entityTypes().getRandom(world.getRandom());
        if (optional.isEmpty()) {
            return;
        }
        EntityType<?> projectileType = optional.get().value();
        LivingEntity shooter = context.owner();
        ItemStack wandStack = shooter.getMainHandStack();

        ProjectileEntity projectileEntity = getProjectileEntity(projectileType, world, shooter, wandStack);
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

    public ProjectileEntity getProjectileEntity(EntityType<?> projectileType, ServerWorld world, LivingEntity shooter, ItemStack fromStack) {
        if (projectileType == EntityType.FIREWORK_ROCKET) {
            return new FireworkRocketEntity(world, fromStack, shooter,
                    shooter.getX(), shooter.getEyeY() - (double) 0.15f, shooter.getZ(), true);

        } else if (projectileType == EntityType.ARROW) {
            return new ArrowEntity(world, shooter, fromStack.copyWithCount(1), fromStack);
        }
        return null;
    }

    public MapCodec<CastProjectileEnchantmentEffect> getCodec() {
        return CODEC;
    }
}

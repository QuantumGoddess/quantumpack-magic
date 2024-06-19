// Source code is decompiled from a .class file using FernFlower decompiler.
package com.quantumgoddess.item;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import com.google.common.collect.Lists;
import com.quantumgoddess.component.ModDataComponentTypes;
import com.quantumgoddess.component.type.WandComponent;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.EnchantmentEffectComponentTypes;
import net.minecraft.component.type.ChargedProjectilesComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireworkRocketEntity;
import net.minecraft.entity.projectile.PersistentProjectileEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.CrossbowItem.LoadingSounds;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.RangedWeaponItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.EnchantmentTags;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.screen.ScreenTexts;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.UseAction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.random.Random;
import net.minecraft.world.World;

public class WandItem extends RangedWeaponItem {
    private static final float DEFAULT_RECHARGE_TIME = 1.25F;
    public static final int RANGE = 8;
    private boolean charged = false;
    private boolean loaded = false;
    private static final float CHARGE_PROGRESS = 0.2F;
    private static final float LOAD_PROGRESS = 0.5F;
    private static final float DEFAULT_SPEED = 3.15F;
    private static final float FIREWORK_ROCKET_SPEED = 1.6F;
    public static final float field_49258 = 1.6F;
    private static final LoadingSounds DEFAULT_LOADING_SOUNDS;
    private int remainingChargeTicks = MathHelper.floor(DEFAULT_RECHARGE_TIME * 20.0f);

    public WandItem(ToolMaterial toolMaterial, float rechargeTime, Item.Settings settings) {
        super(settings.component(ModDataComponentTypes.WAND, WandItem.createWandComponent(rechargeTime)));
    }

    private static WandComponent createWandComponent(float rechargeTime) {
        return new WandComponent(rechargeTime);
    }

    public static final Predicate<ItemStack> WAND_PROJECTILES = (stack) -> {
        return stack.isIn(ItemTags.ARROWS);
    };

    public Predicate<ItemStack> getProjectiles() {
        return WAND_PROJECTILES;
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        super.use(world, user, hand);
        ItemStack itemStack = user.getStackInHand(hand);
        ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent) itemStack
                .get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            this.shootAll(world, user, hand, itemStack, getSpeed(chargedProjectilesComponent), 1.0F,
                    (LivingEntity) null);
            remainingChargeTicks = getRechargeTime(itemStack, user);
            return TypedActionResult.consume(itemStack);
        } else if (!user.getProjectileType(itemStack).isEmpty()) {
            this.charged = false;
            this.loaded = false;
            user.setCurrentHand(hand);
            return TypedActionResult.consume(itemStack);
        } else {
            return TypedActionResult.fail(itemStack);
        }
    }

    private static float getSpeed(ChargedProjectilesComponent stack) {
        return stack.contains(Items.FIREWORK_ROCKET) ? 1.6F : 3.15F;
    }

    private static boolean loadProjectiles(LivingEntity shooter, ItemStack crossbow) {
        List<ItemStack> list = load(crossbow, shooter.getProjectileType(crossbow), shooter);
        if (!list.isEmpty()) {
            crossbow.set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.of(list));
            return true;
        } else {
            return false;
        }
    }

    public static boolean isCharged(ItemStack stack) {
        ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent) stack
                .getOrDefault(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
        return !chargedProjectilesComponent.isEmpty();
    }

    protected void shoot(LivingEntity shooter, ProjectileEntity projectile, int index, float speed, float divergence,
            float yaw, @Nullable LivingEntity target) {
        Vector3f vector3f;
        if (target != null) {
            double d = target.getX() - shooter.getX();
            double e = target.getZ() - shooter.getZ();
            double f = Math.sqrt(d * d + e * e);
            double g = target.getBodyY(0.3333333333333333) - projectile.getY() + f * 0.20000000298023224;
            vector3f = calcVelocity(shooter, new Vec3d(d, g, e), yaw);
        } else {
            Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((double) (yaw * 0.017453292F), vec3d.x, vec3d.y,
                    vec3d.z);
            Vec3d vec3d2 = shooter.getRotationVec(1.0F);
            vector3f = vec3d2.toVector3f().rotate(quaternionf);
        }

        projectile.setVelocity((double) vector3f.x(), (double) vector3f.y(), (double) vector3f.z(), speed, divergence);
        float h = getSoundPitch(shooter.getRandom(), index);
        shooter.getWorld().playSound((PlayerEntity) null, shooter.getX(), shooter.getY(), shooter.getZ(),
                SoundEvents.ITEM_CROSSBOW_SHOOT, shooter.getSoundCategory(), 1.0F, h);
    }

    private static Vector3f calcVelocity(LivingEntity shooter, Vec3d direction, float yaw) {
        Vector3f vector3f = direction.toVector3f().normalize();
        Vector3f vector3f2 = (new Vector3f(vector3f)).cross(new Vector3f(0.0F, 1.0F, 0.0F));
        if ((double) vector3f2.lengthSquared() <= 1.0E-7) {
            Vec3d vec3d = shooter.getOppositeRotationVector(1.0F);
            vector3f2 = (new Vector3f(vector3f)).cross(vec3d.toVector3f());
        }

        Vector3f vector3f3 = (new Vector3f(vector3f)).rotateAxis(1.5707964F, vector3f2.x, vector3f2.y, vector3f2.z);
        return (new Vector3f(vector3f)).rotateAxis(yaw * 0.017453292F, vector3f3.x, vector3f3.y, vector3f3.z);
    }

    @Override
    protected ProjectileEntity createArrowEntity(World world, LivingEntity shooter, ItemStack weaponStack,
            ItemStack projectileStack, boolean critical) {

        RegistryKey<Enchantment> enchantment = weaponStack.getEnchantments().getEnchantmentEntries().stream().filter(entry -> entry.getKey().isIn(EnchantmentTags.CURSE)).findFirst().get().getKey().getKey().orElse(null);

        if (enchantment == Enchantments.BINDING_CURSE) {
            return new FireworkRocketEntity(world, projectileStack, shooter, shooter.getX(),
                    shooter.getEyeY() - 0.15000000596046448, shooter.getZ(), true);
        }

        else if (enchantment == Enchantments.VANISHING_CURSE) {
            ProjectileEntity projectileEntity = super.createArrowEntity(world, shooter, weaponStack, projectileStack,
                    critical);
            if (projectileEntity instanceof PersistentProjectileEntity) {
                PersistentProjectileEntity persistentProjectileEntity = (PersistentProjectileEntity) projectileEntity;
                persistentProjectileEntity.setSound(SoundEvents.ITEM_CROSSBOW_HIT);
            }

            return projectileEntity;
        }

        else {
            return null;
        }

    }

    protected int getWeaponStackDamage(ItemStack projectile) {
        return projectile.isOf(Items.FIREWORK_ROCKET) ? 3 : 1;
    }

    public void shootAll(World world, LivingEntity shooter, Hand hand, ItemStack stack, float speed, float divergence,
            @Nullable LivingEntity target) {
        if (world instanceof ServerWorld serverWorld) {
            ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent) stack
                    .set(DataComponentTypes.CHARGED_PROJECTILES, ChargedProjectilesComponent.DEFAULT);
            if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
                this.shootAll(serverWorld, shooter, hand, stack, chargedProjectilesComponent.getProjectiles(), speed,
                        divergence, shooter instanceof PlayerEntity, target);
                if (shooter instanceof ServerPlayerEntity) {
                    ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) shooter;
                    Criteria.SHOT_CROSSBOW.trigger(serverPlayerEntity, stack);
                    serverPlayerEntity.incrementStat(Stats.USED.getOrCreateStat(stack.getItem()));
                }

            }
        }
    }

    private static float getSoundPitch(Random random, int index) {
        return index == 0 ? 1.0F : getSoundPitch((index & 1) == 1, random);
    }

    private static float getSoundPitch(boolean flag, Random random) {
        float f = flag ? 0.63F : 0.43F;
        return 1.0F / (random.nextFloat() * 0.5F + 1.8F) + f;
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity user, int slot, boolean selected) {
        if (!world.isClient) {
            LoadingSounds loadingSounds = this.getLoadingSounds(stack);
            LivingEntity user2 = (LivingEntity) user;
            int i = getRechargeTime(stack, user2) - remainingChargeTicks;
            float f = getRechargeProgress(i, stack, user2);
            if (f < 0.2F) {
                this.charged = false;
                this.loaded = false;
            }

            if (f >= 0.2F && !this.charged) {
                this.charged = true;
                loadingSounds.start().ifPresent((sound) -> {
                    world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(),
                            (SoundEvent) sound.value(), SoundCategory.PLAYERS, 0.5F, 1.0F);
                });
            }

            if (f >= 0.5F && !this.loaded) {
                this.loaded = true;
                loadingSounds.mid().ifPresent((sound) -> {
                    world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(),
                            (SoundEvent) sound.value(), SoundCategory.PLAYERS, 0.5F, 1.0F);
                });
            }

            if (f >= 1.0F && !isCharged(stack) && loadProjectiles(user2, stack)) {
                loadingSounds.end().ifPresent((sound) -> {
                    world.playSound((PlayerEntity) null, user.getX(), user.getY(), user.getZ(),
                            (SoundEvent) sound.value(),
                            user.getSoundCategory(), 1.0F, 1.0F / (world.getRandom().nextFloat() * 0.5F + 1.0F) + 0.2F);
                });
            }
            remainingChargeTicks = Math.max(0, --remainingChargeTicks);
        }

    }

    public static int getRechargeTime(ItemStack stack, LivingEntity user) {
        WandComponent wandComponent = stack.get(ModDataComponentTypes.WAND);
        float i = wandComponent != null ? wandComponent.getRechargeTime() : DEFAULT_RECHARGE_TIME;
        float f = EnchantmentHelper.getCrossbowChargeTime(stack, user, i);
        return MathHelper.floor(f * 20.0F);
    }

    public UseAction getUseAction(ItemStack stack) {
        return UseAction.CROSSBOW;
    }

    LoadingSounds getLoadingSounds(ItemStack stack) {
        return (LoadingSounds) EnchantmentHelper
                .getEffect(stack, EnchantmentEffectComponentTypes.CROSSBOW_CHARGING_SOUNDS)
                .orElse(DEFAULT_LOADING_SOUNDS);
    }

    private static float getRechargeProgress(int ticksPassed, ItemStack stack, LivingEntity user) {
        float f = (float) ticksPassed / (float) getRechargeTime(stack, user);
        if (f > 1.0F) {
            f = 1.0F;
        }

        return f;
    }

    public void appendTooltip(ItemStack stack, Item.TooltipContext context, List<Text> tooltip, TooltipType type) {
        ChargedProjectilesComponent chargedProjectilesComponent = (ChargedProjectilesComponent) stack
                .get(DataComponentTypes.CHARGED_PROJECTILES);
        if (chargedProjectilesComponent != null && !chargedProjectilesComponent.isEmpty()) {
            ItemStack itemStack = (ItemStack) chargedProjectilesComponent.getProjectiles().get(0);
            tooltip.add(Text.translatable("item.minecraft.crossbow.projectile").append(ScreenTexts.SPACE)
                    .append(itemStack.toHoverableText()));
            if (type.isAdvanced() && itemStack.isOf(Items.FIREWORK_ROCKET)) {
                List<Text> list = Lists.newArrayList();
                Items.FIREWORK_ROCKET.appendTooltip(itemStack, context, list, type);
                if (!list.isEmpty()) {
                    for (int i = 0; i < list.size(); ++i) {
                        list.set(i, Text.literal("  ").append((Text) list.get(i)).formatted(Formatting.GRAY));
                    }

                    tooltip.addAll(list);
                }
            }

        }
    }

    public boolean isUsedOnRelease(ItemStack stack) {
        return stack.isOf(this);
    }

    public int getRange() {
        return 8;
    }

    static {
        DEFAULT_LOADING_SOUNDS = new LoadingSounds(Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_START),
                Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_MIDDLE),
                Optional.of(SoundEvents.ITEM_CROSSBOW_LOADING_END));
    }
}

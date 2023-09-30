package quantumgoddess.magic.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import net.minecraft.block.BlockState;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterials;
import net.minecraft.item.Vanishable;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import quantumgoddess.magic.mixinhelper.LivingEntityMixinInterface;
import quantumgoddess.magic.projectile.Projectile;
import quantumgoddess.magic.projectile.ProjectileType;

public class WandItem extends ToolItem implements Vanishable {
    private final float attackDamage;
    private final float attackSpeed;
    private final float projectileSpeed;
    private final float range;
    private float cooldown;
    private final float manaCost;
    private final ProjectileType projectileType;
    private final Multimap<EntityAttribute, EntityAttributeModifier> attributeModifiers;

    public WandItem(ProjectileType projectileType, float attackDamage, float attackSpeed, float projectileSpeed, float range, float manaCost, Settings settings) {
        super(ToolMaterials.WOOD, settings);
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.projectileSpeed = projectileSpeed;
        this.range = range;
        this.manaCost = manaCost;
        this.projectileType = projectileType;
        
        ImmutableMultimap.Builder<EntityAttribute, EntityAttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(EntityAttributes.GENERIC_ATTACK_DAMAGE, new EntityAttributeModifier(ATTACK_DAMAGE_MODIFIER_ID, "Weapon modifier", (double)0.0f, EntityAttributeModifier.Operation.ADDITION));
        builder.put(EntityAttributes.GENERIC_ATTACK_SPEED, new EntityAttributeModifier(ATTACK_SPEED_MODIFIER_ID, "Weapon modifier", (double)-3.0f, EntityAttributeModifier.Operation.ADDITION));
        this.attributeModifiers = builder.build();
    }

    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public boolean canMine(BlockState state, World world, BlockPos pos, PlayerEntity miner) {
        return !miner.isCreative();
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        stack.damage(1, attacker, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        return true;
    }

    @Override
    public boolean postMine(ItemStack stack, World world, BlockState state, BlockPos pos, LivingEntity miner) {
        if (state.getHardness(world, pos) != 0.0f) {
            stack.damage(2, miner, e -> e.sendEquipmentBreakStatus(EquipmentSlot.MAINHAND));
        }
        return true;
    }

    @Override
    public Multimap<EntityAttribute, EntityAttributeModifier> getAttributeModifiers(EquipmentSlot slot) {
        if (slot == EquipmentSlot.MAINHAND) {
            return this.attributeModifiers;
        }
        return super.getAttributeModifiers(slot);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(!user.getAbilities().creativeMode && (((LivingEntityMixinInterface)user).getMana() <= 0.0f || Util.getMeasuringTimeMs() - this.cooldown < (1000L / attackSpeed))){
            return TypedActionResult.fail(itemStack);  
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!world.isClient) {
            Projectile projectile = new Projectile(projectileType, attackDamage, projectileSpeed, range);
            projectile.instantiate(world, user);
            this.cooldown = Util.getMeasuringTimeMs();
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.damage(1, user, null);
            ((LivingEntityMixinInterface)user).useMana(this.manaCost);
        }
        return TypedActionResult.success(itemStack, world.isClient());     
    }
}

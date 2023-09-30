package quantumgoddess.magic.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.thrown.SnowballEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldEvents;
import quantumgoddess.magic.mixinhelper.LivingEntityMixinInterface;

public class WandItem extends Item {

    public WandItem(Settings settings) {
        super(settings);
    }


    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand);
        if(((LivingEntityMixinInterface)user).getMana() <= 0.0f && !user.getAbilities().creativeMode){
            return TypedActionResult.fail(itemStack);  
        }
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5f, 0.4f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!world.isClient) {
            Vec3d vec3d = user.getRotationVec(1.0f);
                    

                    FireballEntity fireballEntity = new FireballEntity(world, (LivingEntity)user, vec3d.x, vec3d.y, vec3d.z, 1);
                    fireballEntity.setPosition(user.getX() + vec3d.x * 4.0, user.getBodyY(0.5) + vec3d.y * 4.0, fireballEntity.getZ() + vec3d.z * 4.0);
                    world.spawnEntity(fireballEntity);
        }
        user.incrementStat(Stats.USED.getOrCreateStat(this));
        if (!user.getAbilities().creativeMode) {
            itemStack.damage(1, user, null);
            ((LivingEntityMixinInterface)user).useMana(1);
        }
        return TypedActionResult.success(itemStack, world.isClient());     
    }
}

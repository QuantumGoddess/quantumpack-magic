package quantumgoddess.magic.projectile;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.FireballEntity;
import net.minecraft.entity.projectile.ProjectileEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class Projectile {
    
    private final ProjectileType projectileType;
    private final float attackDamage;
    private final float attackSpeed;
    private final float range;
    private ProjectileEntity projectileEntity;


    public Projectile(ProjectileType projectileType, float attackDamage, float attackSpeed, float range){
        this.projectileType = projectileType;
        this.attackDamage = attackDamage;
        this.attackSpeed = attackSpeed;
        this.range = range;
    }

    public ProjectileType getType() {
        return projectileType;
    }

    public void instantiate(World world, PlayerEntity instantiator){
        Vec3d vec3d = instantiator.getRotationVec(1.0f);
        switch((ProjectileTypes)projectileType){
            case FIRE:
                projectileEntity = new FireballEntity(world, (LivingEntity)instantiator, vec3d.x, vec3d.y, vec3d.z, 1);
                projectileEntity.setPosition(instantiator.getX() + vec3d.x * 4.0, instantiator.getBodyY(0.5) + vec3d.y * 4.0, projectileEntity.getZ() + vec3d.z * 4.0);
                projectileEntity.setVelocity(vec3d.x, vec3d.y, vec3d.z, attackSpeed, 0.0f);
                world.spawnEntity(projectileEntity);
        }
    }
}

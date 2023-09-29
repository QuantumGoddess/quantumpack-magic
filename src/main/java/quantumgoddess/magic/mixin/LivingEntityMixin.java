package quantumgoddess.magic.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import quantumgoddess.magic.entity.attribute.MagicEntityAttributes;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity{

    private static final TrackedData<Float> MANA = DataTracker.registerData(LivingEntity.class, TrackedDataHandlerRegistry.FLOAT);
    public final int defaultMaxMana =  20;
    
    public LivingEntityMixin(EntityType<?> type, World world) {
            super(type, world);
    }

    @Inject(at = @At("TAIL"), method = "<init>(Lnet/minecraft/entity/EntityType;Lnet/minecraft/world/World;)V")
    private void init(EntityType<? extends LivingEntity> entityType, World world, CallbackInfo info) {
        this.setMana(this.getMaxMana());
    }

    public final float getMaxMana() {
        return (float)((LivingEntity)(Object)this).getAttributeValue(MagicEntityAttributes.GENERIC_MAX_MANA);
    }

    public float getMana() {
        return this.dataTracker.get(MANA).floatValue();
    }

    public void setMana(float mana) {
        this.dataTracker.set(MANA, Float.valueOf(MathHelper.clamp(mana, 0.0f, this.getMaxMana())));
    }

    @Inject(at = @At("TAIL"), method = "initDataTracker()V")
    private void initDataTracker(CallbackInfo info) {
        this.dataTracker.startTracking(MANA, Float.valueOf(1.0f));
    }

	@Inject(method = "createLivingAttributes()Lnet/minecraft/entity/attribute/DefaultAttributeContainer$Builder;", at = @At("RETURN"))
	private static void createLivingAttributes(CallbackInfoReturnable<DefaultAttributeContainer.Builder> info) {
		info.getReturnValue().add(MagicEntityAttributes.GENERIC_MAX_MANA);
	}

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt(Lnet/minecraft/nbt/NbtCompound;)V")
    private void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo info) {
         nbt.putFloat("Mana", this.getMana());
    }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt(Lnet/minecraft/nbt/NbtCompound;)V")
    private void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo info) {
        if (nbt.contains("Mana", 99)) {
            this.setMana(nbt.getFloat("Mana"));
         }
    }

    @Inject(at = @At("TAIL"), method = "updateAttribute(Lnet/minecraft/entity/attribute/EntityAttribute;)V")
    private void updateAttribute(EntityAttribute attribute, CallbackInfo info) {
        float f;
        if (attribute == MagicEntityAttributes.GENERIC_MAX_MANA) {
            f = this.getMaxMana();
            if (this.getMana() > f) {
                this.setMana(f);
            }
        }
    }

    public void replenishMana(float amount) {
        float f = this.getMana();
        if (f > 0.0F) {
           this.setMana(f + amount);
        }
    }

    protected void useMana(float amount) {
        if (amount == 0.0f) {
            return;
        }
        this.setMana(this.getMana() - amount);
        this.emitGameEvent(GameEvent.ENTITY_DAMAGE);
    }
}
package quantumgoddess.magic.mixin.client;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.HungerManager;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import quantumgoddess.magic.entity.attribute.MagicEntityAttributes;
import quantumgoddess.magic.mixinhelper.LivingEntityMixinInterface;
import quantumgoddess.magic.mixinhelper.StarType;

@Mixin(InGameHud.class)
public abstract class InGameHudMixin {

    @Shadow
    @Final
    private static Identifier ARMOR_EMPTY_TEXTURE;
    @Shadow
    @Final
    private static Identifier ARMOR_HALF_TEXTURE;
    @Shadow
    @Final
    private static Identifier ARMOR_FULL_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_EMPTY_HUNGER_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_HALF_HUNGER_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_FULL_HUNGER_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_EMPTY_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_HALF_TEXTURE;
    @Shadow
    @Final
    private static Identifier FOOD_FULL_TEXTURE;
    @Shadow
    @Final
    private static Identifier AIR_TEXTURE;
    @Shadow
    @Final
    private static Identifier AIR_BURSTING_TEXTURE;
    @Shadow
    @Final
    private MinecraftClient client;
    @Shadow
    @Final
    private Random random;

    @Shadow
    private int ticks;
    @Shadow
    private int lastHealthValue;
    @Shadow
    private int renderHealthValue;
    @Shadow
    private long lastHealthCheckTime;
    @Shadow
    private long heartJumpEndTick;
    @Shadow
    private int scaledWidth;
    @Shadow
    private int scaledHeight;

    private int lastManaValue;
    private int renderManaValue;
    private long lastManaCheckTime;
    private long starJumpEndTick;

    @Shadow
    private void renderHealthBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxHealth, int lastHealth, int health, int absorption, boolean blinking){}
    @Shadow
    private LivingEntity getRiddenEntity(){return null;}
    @Shadow
    private int getHeartCount(LivingEntity entity){return 0;}
    @Shadow
    private int getHeartRows(int heartCount){return 0;}

    @Inject(at = @At("HEAD"), method = "renderStatusBars(Lnet/minecraft/client/gui/DrawContext;)V", cancellable = true)
    private void renderStatusBars(DrawContext context, CallbackInfo info){
        int aa;
        int z;
        int y;
        int x;
        PlayerEntity playerEntity = this.getCameraPlayer();
        if (playerEntity == null) {
            return;
        }
        int i = MathHelper.ceil((float)playerEntity.getHealth());
        boolean bl = this.heartJumpEndTick > (long)this.ticks && (this.heartJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
        long l = Util.getMeasuringTimeMs();
        if (i < this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 20;
        } else if (i > this.lastHealthValue && playerEntity.timeUntilRegen > 0) {
            this.lastHealthCheckTime = l;
            this.heartJumpEndTick = this.ticks + 10;
        }
        if (l - this.lastHealthCheckTime > 1000L) {
            this.lastHealthValue = i;
            this.renderHealthValue = i;
            this.lastHealthCheckTime = l;
        }
        this.lastHealthValue = i;
        int j = this.renderHealthValue;
        this.random.setSeed((long)(this.ticks * 312871));
        HungerManager hungerManager = playerEntity.getHungerManager();
        int k = hungerManager.getFoodLevel();
        int m = this.scaledWidth / 2 - 91;
        int n = this.scaledWidth / 2 + 91;
        int o = this.scaledHeight - 39;
        float f = Math.max((float)playerEntity.getAttributeValue(EntityAttributes.GENERIC_MAX_HEALTH), (float)Math.max(j, i));
        int p = MathHelper.ceil((float)playerEntity.getAbsorptionAmount());
        int q = MathHelper.ceil((float)((f + (float)p) / 2.0f / 10.0f));
        int r = Math.max(10 - (q - 2), 3);

        int i2 = MathHelper.ceil((float)((LivingEntityMixinInterface)playerEntity).getMana());
        boolean bl2 = this.starJumpEndTick > (long)this.ticks && (this.starJumpEndTick - (long)this.ticks) / 3L % 2L == 1L;
        long l2 = Util.getMeasuringTimeMs();
        if (i2 < this.lastManaValue && playerEntity.timeUntilRegen > 0) {
            this.lastManaCheckTime = l2;
            this.starJumpEndTick = this.ticks + 20;
        } else if (i2 > this.lastManaValue && playerEntity.timeUntilRegen > 0) {
            this.lastManaCheckTime = l2;
            this.starJumpEndTick = this.ticks + 10;
        }
        if (l - this.lastManaCheckTime > 1000L) {
            this.lastManaValue = i2;
            this.renderManaValue = i2;
            this.lastManaCheckTime = l2;
        }
        this.lastManaValue = i2;
        int j2 = this.renderManaValue;
        float f2 = Math.max((float)playerEntity.getAttributeValue(MagicEntityAttributes.GENERIC_MAX_MANA), (float)Math.max(j2, i2));
        int q2 = MathHelper.ceil((float)((f2) / 2.0f / 10.0f));
        int r2 = Math.max(10 - (q2 - 2), 3);
        int v2 = -1;

        int o2 = o - (q - 1) * r - 10;
        int s = o2 - (q2 - 1) * r2 - 10;

        int t = o - 10;
        int u = playerEntity.getArmor();
        int v = -1;
        if (playerEntity.hasStatusEffect(StatusEffects.REGENERATION)) {
            v = this.ticks % MathHelper.ceil((float)(f + 5.0f));
        }
        this.client.getProfiler().push("armor");
        for (int w = 0; w < 10; ++w) {
            if (u <= 0) continue;
            x = m + w * 8;
            if (w * 2 + 1 < u) {
                context.drawGuiTexture(ARMOR_FULL_TEXTURE, x, s, 9, 9);
            }
            if (w * 2 + 1 == u) {
                context.drawGuiTexture(ARMOR_HALF_TEXTURE, x, s, 9, 9);
            }
            if (w * 2 + 1 <= u) continue;
            context.drawGuiTexture(ARMOR_EMPTY_TEXTURE, x, s, 9, 9);
        }
        this.client.getProfiler().swap("health");
        this.renderHealthBar(context, playerEntity, m, o, r, v, f, i, j, p, bl);
        LivingEntity livingEntity = this.getRiddenEntity();
        x = this.getHeartCount(livingEntity);
        if (x == 0) {
            this.client.getProfiler().swap("food");
            for (y = 0; y < 10; ++y) {
                Identifier identifier3;
                Identifier identifier2;
                Identifier identifier;
                z = o;
                if (playerEntity.hasStatusEffect(StatusEffects.HUNGER)) {
                    identifier = FOOD_EMPTY_HUNGER_TEXTURE;
                    identifier2 = FOOD_HALF_HUNGER_TEXTURE;
                    identifier3 = FOOD_FULL_HUNGER_TEXTURE;
                } else {
                    identifier = FOOD_EMPTY_TEXTURE;
                    identifier2 = FOOD_HALF_TEXTURE;
                    identifier3 = FOOD_FULL_TEXTURE;
                }
                if (playerEntity.getHungerManager().getSaturationLevel() <= 0.0f && this.ticks % (k * 3 + 1) == 0) {
                    z += this.random.nextInt(3) - 1;
                }
                aa = n - y * 8 - 9;
                context.drawGuiTexture(identifier, aa, z, 9, 9);
                if (y * 2 + 1 < k) {
                    context.drawGuiTexture(identifier3, aa, z, 9, 9);
                }
                if (y * 2 + 1 != k) continue;
                context.drawGuiTexture(identifier2, aa, z, 9, 9);
            }
            t -= 10;
        }
        this.client.getProfiler().swap("air");
        y = playerEntity.getMaxAir();
        z = Math.min(playerEntity.getAir(), y);
        if (playerEntity.isSubmergedIn(FluidTags.WATER) || z < y) {
            int ab = this.getHeartRows(x) - 1;
            t -= ab * 10;
            int ac = MathHelper.ceil((double)((double)(z - 2) * 10.0 / (double)y));
            int ad = MathHelper.ceil((double)((double)z * 10.0 / (double)y)) - ac;
            for (aa = 0; aa < ac + ad; ++aa) {
                if (aa < ac) {
                    context.drawGuiTexture(AIR_TEXTURE, n - aa * 8 - 9, t, 9, 9);
                    continue;
                }
                context.drawGuiTexture(AIR_BURSTING_TEXTURE, n - aa * 8 - 9, t, 9, 9);
            }
        }
        this.client.getProfiler().swap("mana");
        
        this.renderManaBar(context, playerEntity, m, o2, r2, v2, f2, i2, j2, bl2);
        this.client.getProfiler().pop();
        info.cancel();
    }
    
    private PlayerEntity getCameraPlayer() {
        if (!(this.client.getCameraEntity() instanceof PlayerEntity)) {
            return null;
        }
        return (PlayerEntity)this.client.getCameraEntity();
    }

    private void renderManaBar(DrawContext context, PlayerEntity player, int x, int y, int lines, int regeneratingHeartIndex, float maxMana, int lastMana, int mana, boolean blinking) {
        StarType starType = StarType.fromPlayerState(player);
        int i = MathHelper.ceil((double)((double)maxMana / 2.0));
        // int k = i * 2;
        for (int l = i - 1; l >= 0; --l) {
            boolean bl4;
            // int r;
            // boolean bl2;
            int m = l / 10;
            int n = l % 10;
            int o = x + n * 8;
            int p = y - m * lines;
            // if (lastHealth + absorption <= 4) {
            //     p += this.random.nextInt(2);
            // }
            if (l < i && l == regeneratingHeartIndex) {
                p -= 2;
            }
            this.drawHeart(context, StarType.CONTAINER, o, p, blinking, false);
            int q = l * 2;
            // boolean bl3 = bl2 = l >= i;
            // if (bl2 && (r = q - k) < absorption) {
            //     boolean bl32 = r + 1 == absorption;
            //     this.drawHeart(context, heartType == HeartType.WITHERED ? heartType : HeartType.ABSORBING, o, p, false, bl32);
            // }
            if (blinking && q < mana) {
                bl4 = q + 1 == mana;
                this.drawHeart(context, starType, o, p, true, bl4);
            }
            if (q >= lastMana) continue;
            bl4 = q + 1 == lastMana;
            this.drawHeart(context, starType, o, p, false, bl4);
        }
    }

    private void drawHeart(DrawContext context, StarType type, int x, int y, boolean blinking, boolean half) {
        context.drawGuiTexture(type.getTexture(half, blinking), x, y, 9, 9);
    }
}

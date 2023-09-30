package quantumgoddess.magic.mixinhelper;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

@Environment(value=EnvType.CLIENT)
public enum StarType {
        CONTAINER(new Identifier("hud/heart/container"), new Identifier("hud/heart/container_blinking"), new Identifier("hud/heart/container"), new Identifier("hud/heart/container_blinking")),
        NORMAL(new Identifier("hud/heart/full"), new Identifier("hud/heart/full_blinking"), new Identifier("hud/heart/half"), new Identifier("hud/heart/half_blinking"));

        private final Identifier fullTexture;
        private final Identifier fullBlinkingTexture;
        private final Identifier halfTexture;
        private final Identifier halfBlinkingTexture;

        private StarType(Identifier fullTexture, Identifier fullBlinkingTexture, Identifier halfTexture, Identifier halfBlinkingTexture) {
            this.fullTexture = fullTexture;
            this.fullBlinkingTexture = fullBlinkingTexture;
            this.halfTexture = halfTexture;
            this.halfBlinkingTexture = halfBlinkingTexture;
        }

        public Identifier getTexture(boolean half, boolean blinking) {
            if (half) {
                return blinking ? this.halfBlinkingTexture : this.halfTexture;
            }
            return blinking ? this.fullBlinkingTexture : this.fullTexture;
        }

        public static StarType fromPlayerState(PlayerEntity player) {
            StarType heartType = NORMAL;
            return heartType;
        }
    }

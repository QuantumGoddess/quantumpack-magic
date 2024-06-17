package com.quantumgoddess.component.type;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;

public record WandComponent(float rechargeTime) {

        public static final Codec<WandComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(Codec.FLOAT.optionalFieldOf("recharge_time", Float.valueOf(1.0f)).forGetter(WandComponent::rechargeTime)).apply(instance, (rechargeTime) -> new WandComponent(rechargeTime)));
        public static final PacketCodec<RegistryByteBuf, WandComponent> PACKET_CODEC = PacketCodec.tuple(PacketCodecs.FLOAT, WandComponent::rechargeTime, WandComponent::new);

        public float getRechargeTime() {
                return this.rechargeTime;
        }
}

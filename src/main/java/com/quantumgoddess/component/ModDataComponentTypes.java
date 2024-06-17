package com.quantumgoddess.component;

import java.util.function.UnaryOperator;

import com.quantumgoddess.component.type.WandComponent;

import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;

public class ModDataComponentTypes {

    public static final ComponentType<WandComponent> WAND = register("wand", builder -> builder.codec(WandComponent.CODEC).packetCodec(WandComponent.PACKET_CODEC).cache());

    private static <T> ComponentType<T> register(String id, UnaryOperator<ComponentType.Builder<T>> builderOperator) {
        return Registry.register(Registries.DATA_COMPONENT_TYPE, id, ((ComponentType.Builder<T>) builderOperator.apply(ComponentType.builder())).build());
    }
}

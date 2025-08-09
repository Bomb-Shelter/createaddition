package com.mrh0.createaddition.network.fabric;

import io.netty.buffer.ByteBuf;
import net.createmod.catnip.platform.CatnipServices;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public class PayloadRegistrar {
    public <T extends CustomPacketPayload> void playBidirectional(CustomPacketPayload.Type<T> type, StreamCodec<? super RegistryFriendlyByteBuf, T> codec, IPayloadHandler<T> handler) {
        PayloadTypeRegistry.playC2S().register(type, codec);
        PayloadTypeRegistry.playS2C().register(type, codec);

        ServerPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
            handler.handle(payload, ServerPayloadContext.play(context));
        });

        CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> {
            ClientPayloadRegistrar.registerClientReceiver(type, handler);
        });
    }

    private static class ClientPayloadRegistrar {
        public static <T extends CustomPacketPayload> void registerClientReceiver(CustomPacketPayload.Type<T> type, IPayloadHandler<T> handler) {
            ClientPlayNetworking.registerGlobalReceiver(type, (payload, context) -> {
                handler.handle(payload, ClientPayloadContext.play(context));
            });
        }
    }
}

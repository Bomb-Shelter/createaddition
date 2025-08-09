package com.mrh0.createaddition.network.fabric;

import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Helper class that merges two unidirectional handlers into a single bidirectional handler.
 */
public record DirectionalPayloadHandler<T extends CustomPacketPayload>(IPayloadHandler<T> clientSide, IPayloadHandler<T> serverSide) implements IPayloadHandler<T> {
    @Override
    public void handle(T payload, IPayloadContext context) {
        if (context.flow() == PacketFlow.CLIENTBOUND) {
            clientSide.handle(payload, context);
        } else if (context.flow() == PacketFlow.SERVERBOUND) {
            serverSide.handle(payload, context);
        }
    }
}


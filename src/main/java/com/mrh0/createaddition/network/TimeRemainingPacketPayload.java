package com.mrh0.createaddition.network;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.network.fabric.PacketDistributor;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record TimeRemainingPacketPayload(long timeRemaining) implements CustomPacketPayload {
    public static long clientTimeRemaining = 0;

    public static final Type<TimeRemainingPacketPayload> TYPE = new Type<>(CreateAddition.asResource("time_remaining_packet"));

    public static final StreamCodec<ByteBuf, TimeRemainingPacketPayload> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.VAR_LONG,
            TimeRemainingPacketPayload::timeRemaining,
            TimeRemainingPacketPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void updateClientCache(long timeRemaining) {
        clientTimeRemaining = timeRemaining;
    }

    public static boolean send(long timeRemaining, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new TimeRemainingPacketPayload(timeRemaining));
        return true;
    }
}

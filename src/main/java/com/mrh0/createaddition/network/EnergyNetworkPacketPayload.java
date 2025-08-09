package com.mrh0.createaddition.network;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.network.fabric.PacketDistributor;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

public record EnergyNetworkPacketPayload(BlockPos pos, long demand, long buff) implements CustomPacketPayload {
    public static double clientSaturation = 0;
    public static long clientDemand = 0;
    public static long clientBuff = 0;

    public static final CustomPacketPayload.Type<EnergyNetworkPacketPayload> TYPE = new CustomPacketPayload.Type<>(CreateAddition.asResource("energy_network_packet"));

    public static final StreamCodec<ByteBuf, EnergyNetworkPacketPayload> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC,
            EnergyNetworkPacketPayload::pos,
            ByteBufCodecs.VAR_LONG,
            EnergyNetworkPacketPayload::demand,
            ByteBufCodecs.VAR_LONG,
            EnergyNetworkPacketPayload::buff,
            EnergyNetworkPacketPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void updateClientCache(BlockPos pos, long demand, long buff) {
        clientDemand = demand;
        clientBuff = buff;
        clientSaturation = buff - demand;
    }

    public static boolean send(BlockPos pos, long demand, long buff, ServerPlayer player) {
        PacketDistributor.sendToPlayer(player, new EnergyNetworkPacketPayload(pos, demand, buff));
        return true;
    }
}

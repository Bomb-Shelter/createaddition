package com.mrh0.createaddition.network;

import com.mrh0.createaddition.network.fabric.IPayloadContext;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.entity.BlockEntity;

public class ServerPayloadHandler {
    public static void handleObservePayload(final ObservePacketPayload pkt, final IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            try {
                ServerPlayer player = (ServerPlayer) ctx.player();

                sendUpdate(pkt, player);

            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private static void sendUpdate(ObservePacketPayload pkt, ServerPlayer player) {
        BlockEntity be = (BlockEntity) player.level().getBlockEntity(pkt.pos());
        if (be != null) {
            if (be instanceof IObserveBlockEntity) {
                IObserveBlockEntity ote = (IObserveBlockEntity) be;
                ote.onObserved(player, pkt);
                Packet<ClientGamePacketListener> supdatepacket = be.getUpdatePacket();
                if (supdatepacket != null) player.connection.send(supdatepacket);
            }
        }
    }

    public static void handleEnergyNetworkPayload(final EnergyNetworkPacketPayload pkt, final IPayloadContext ctx) {}

    public static void handleTimeRemainingPayload(final TimeRemainingPacketPayload pkt, final IPayloadContext ctx) {}
}

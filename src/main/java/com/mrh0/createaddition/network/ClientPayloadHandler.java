package com.mrh0.createaddition.network;

import com.mrh0.createaddition.network.fabric.IPayloadContext;

public class ClientPayloadHandler {
    public static void handleObservePayload(final ObservePacketPayload pkt, final IPayloadContext ctx) {

    }

    public static void handleEnergyNetworkPayload(final EnergyNetworkPacketPayload pkt, final IPayloadContext ctx) {
        EnergyNetworkPacketPayload.updateClientCache(pkt.pos(), pkt.demand(), pkt.buff());
    }

    public static void handleTimeRemainingPayload(final TimeRemainingPacketPayload pkt, final IPayloadContext ctx) {
        TimeRemainingPacketPayload.updateClientCache(pkt.timeRemaining());
    }
}

package com.mrh0.createaddition.network.fabric;

import io.github.fabricators_of_create.porting_lib.core.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.server.network.ServerCommonPacketListenerImpl;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ServerPayloadContext implements IPayloadContext {
    public static IPayloadContext configuration(ServerConfigurationNetworking.Context context) {
        return new ServerPayloadContext(context.responseSender(), context.networkHandler());
    }

    public static IPayloadContext play(ServerPlayNetworking.Context context) {
        return new ServerPayloadContext(context.responseSender(), context.player().connection);
    }

    private final PacketSender responseSender;
    private final ServerCommonPacketListenerImpl packetListener;

    public ServerPayloadContext(PacketSender responseSender, ServerCommonPacketListenerImpl packetListener) {
        this.responseSender = responseSender;
        this.packetListener = packetListener;
    }

    @Override
    public PacketSender responseSender() {
        return responseSender;
    }

    @Override
    public Player player() {
        if (packetListener instanceof ServerGamePacketListenerImpl gamePacketListener) {
            return gamePacketListener.getPlayer();
        }

        throw new UnsupportedOperationException();
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable task) {
        return ServerLifecycleHooks.getCurrentServer().submit(task);
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
        return ServerLifecycleHooks.getCurrentServer().submit(task);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.SERVERBOUND;
    }

    @Override
    public void handle(CustomPacketPayload payload) {

    }

    @Override
    public void finishCurrentTask(ConfigurationTask.Type type) {

    }
}
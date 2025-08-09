package com.mrh0.createaddition.network.fabric;

import net.fabricmc.fabric.api.client.networking.v1.ClientConfigurationNetworking;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class ClientPayloadContext implements IPayloadContext {
    public static IPayloadContext configuration(ClientConfigurationNetworking.Context context) {
        return new ClientPayloadContext(context.responseSender(), context.client());
    }

    public static IPayloadContext play(ClientPlayNetworking.Context context) {
        return new ClientPayloadContext(context.responseSender(), context.client());
    }

    private final PacketSender responseSender;
    private final Minecraft minecraft;

    public ClientPayloadContext(PacketSender responseSender, Minecraft minecraft) {
        this.responseSender = responseSender;
        this.minecraft = minecraft;
    }

    @Override
    public PacketSender responseSender() {
        return responseSender;
    }

    @Override
    public Player player() {
        return minecraft.player;
    }

    @Override
    public CompletableFuture<Void> enqueueWork(Runnable task) {
        return minecraft.submit(task);
    }

    @Override
    public <T> CompletableFuture<T> enqueueWork(Supplier<T> task) {
        return minecraft.submit(task);
    }

    @Override
    public PacketFlow flow() {
        return PacketFlow.CLIENTBOUND;
    }

    @Override
    public void handle(CustomPacketPayload payload) {

    }

    @Override
    public void finishCurrentTask(ConfigurationTask.Type type) {

    }
}

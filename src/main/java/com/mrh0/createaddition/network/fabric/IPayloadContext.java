package com.mrh0.createaddition.network.fabric;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.ConnectionProtocol;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ConfigurationTask;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

/**
 * Common context interface for payload handlers.
 */
@ApiStatus.NonExtendable
public interface IPayloadContext {

    PacketSender responseSender();

    /**
     * Retrieves the player relevant to this payload. Players are only available in the {@link ConnectionProtocol#PLAY} phase.
     * <p>
     * For server-bound payloads, retrieves the sending {@link ServerPlayer}.
     * <p>
     * For client-bound payloads, retrieves the receiving {@link LocalPlayer}.
     *
     * @throws UnsupportedOperationException when called during the configuration phase.
     */
    Player player();

    /**
     * Sends the given payload back to the sender.
     *
     * @param payload The payload to send.
     */
    default void reply(CustomPacketPayload payload) {
        this.responseSender().sendPacket(payload);
    }

    /**
     * Disconnects the player from the network.
     */
    default void disconnect(Component reason) {
        this.responseSender().disconnect(reason);
    }

    /**
     * For handlers running on the network thread, submits the given task to be run on the main thread of the game.
     * <p>
     * For handlers running on the main thread, immediately executes the task.
     * <p>
     * On the network thread, the future will be automatically guarded against exceptions using {@link CompletableFuture#exceptionally}.
     * If you need to catch your own exceptions, use a try/catch block within your task.
     *
     * @param task The task to run.
     */
    CompletableFuture<Void> enqueueWork(Runnable task);

    /**
     * @see #enqueueWork(Runnable)
     */
    <T> CompletableFuture<T> enqueueWork(Supplier<T> task);

    /**
     * {@return the flow of the received payload}
     */
    PacketFlow flow();

    /**
     * Handles a payload using the current context.
     * <p>
     * Used to handle sub-payloads if necessary.
     *
     * @param payload The payload.
     */
    void handle(CustomPacketPayload payload);

    /**
     * Marks a {@link ConfigurationTask} as completed.
     *
     * @param type The type of task that was completed.
     * @throws UnsupportedOperationException if called on the client, or called on the server outside of the configuration phase.
     */
    void finishCurrentTask(ConfigurationTask.Type type);
}

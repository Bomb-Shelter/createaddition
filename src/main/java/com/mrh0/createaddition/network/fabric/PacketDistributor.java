package com.mrh0.createaddition.network.fabric;

import io.github.fabricators_of_create.porting_lib.core.util.ServerLifecycleHooks;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.ClientboundCustomPayloadPacket;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBundlePacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerChunkCache;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class PacketDistributor {

    /**
     * Send the given payload(s) to the given player
     */
    public static void sendToPlayer(ServerPlayer player, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        ServerPlayNetworking.send(player, payload);

        for (CustomPacketPayload p : payloads) {
            ServerPlayNetworking.send(player, p);
        }
    }

    public static void sendToAllPlayers(CustomPacketPayload payload) {
        MinecraftServer server = Objects.requireNonNull(ServerLifecycleHooks.getCurrentServer(), "Cannot send clientbound payloads on the client");
        PlayerLookup.all(server).forEach(player -> ServerPlayNetworking.send(player, payload));
    }

    public static void sendToServer(CustomPacketPayload payload) {
        ClientPlayNetworking.send(payload);
    }

    /**
     * Send the given payload(s) to all players tracking the given entity
     */
    public static void sendToPlayersTrackingEntity(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        if (entity.level().isClientSide()) {
            throw new IllegalStateException("Cannot send clientbound payloads on the client");
        } else {
            for (ServerPlayer player : PlayerLookup.tracking(entity)) {
                sendToPlayer(player, payload, payloads);
            }
        }
        // Silently ignore custom Level implementations which may not return ServerChunkCache.
    }

    /**
     * Send the given payload(s) to all players tracking the given entity and the entity itself if it is a player
     */
    public static void sendToPlayersTrackingEntityAndSelf(Entity entity, CustomPacketPayload payload, CustomPacketPayload... payloads) {
        if (entity.level().isClientSide()) {
            throw new IllegalStateException("Cannot send clientbound payloads on the client");
        } else {
            List<ServerPlayer> players = new ArrayList<>(PlayerLookup.tracking(entity));

            if (entity instanceof ServerPlayer serverPlayer && !players.contains(serverPlayer)) {
                players.add(serverPlayer);
            }

            for (ServerPlayer player : players) {
                sendToPlayer(player, payload, payloads);
            }
        }
        // Silently ignore custom Level implementations which may not return ServerChunkCache.
    }
}

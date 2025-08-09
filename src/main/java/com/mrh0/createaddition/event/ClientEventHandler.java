package com.mrh0.createaddition.event;

import com.mrh0.createaddition.CreateAddition;

import com.mrh0.createaddition.item.WireSpool;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.ClientMinecraftWrapper;
import com.mrh0.createaddition.util.Util;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.item.ItemStack;

public class ClientEventHandler implements ClientModInitializer {

    public static boolean clientRenderHeldWire = false;

    @Override
    public void onInitializeClient() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            playerRendererEvent();
            tickSoundscapes();
        });

        ModBusEvents.registerReloadListener();
    }

    public static void playerRendererEvent() {
        if(ClientMinecraftWrapper.getPlayer() == null) return;
        ItemStack stack = ClientMinecraftWrapper.getPlayer().getInventory().getSelected();
        if(stack.isEmpty()) return;
        if(WireSpool.isRemover(stack.getItem())) return;
        clientRenderHeldWire = Util.getWireNodeOfSpools(stack) != null;
    }

    public static void tickSoundscapes() {
        CASoundScapes.tick();
    }

    public static class ModBusEvents {
        public static void registerReloadListener() {
            ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(new ResourceReloadListener());
        }
    }
}
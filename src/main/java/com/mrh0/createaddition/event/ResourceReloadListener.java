package com.mrh0.createaddition.event;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.sound.CASoundScapes;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;

public class ResourceReloadListener implements ResourceManagerReloadListener, IdentifiableResourceReloadListener {
    public static final ResourceLocation ID = CreateAddition.asResource("reload_listener");

    @Override
    public void onResourceManagerReload(ResourceManager resourceManager) {
        CASoundScapes.invalidateAll();
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
    }
}

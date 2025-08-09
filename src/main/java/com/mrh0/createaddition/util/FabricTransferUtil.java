package com.mrh0.createaddition.util;

import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import org.jetbrains.annotations.Nullable;

public class FabricTransferUtil {
    @Nullable
    public static <T> StorageView<T> getFirstInStorage(Storage<T> storage) {
        for (StorageView<T> view : storage) {
            return view;
        }

        return null;
    }
}

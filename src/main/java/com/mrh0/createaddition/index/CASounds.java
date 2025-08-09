package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.registry.DeferredRegister;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

public class CASounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, CreateAddition.MODID);

    public static final DeferredHolder<SoundEvent, SoundEvent> ELECTRIC_MOTOR_BUZZ = registerSoundEvent("electric_motor_buzz");
    public static final DeferredHolder<SoundEvent, SoundEvent> TESLA_COIL = registerSoundEvent("tesla_coil");
    public static final DeferredHolder<SoundEvent, SoundEvent> ELECTRIC_CHARGE = registerSoundEvent("electric_charge");
    public static final DeferredHolder<SoundEvent, SoundEvent> LOUD_ZAP = registerSoundEvent("loud_zap");
    public static final DeferredHolder<SoundEvent, SoundEvent> LITTLE_ZAP = registerSoundEvent("little_zap");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        ResourceLocation id = CreateAddition.asResource(name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }

    public static void register() {
        SOUND_EVENTS.register();
    }
}

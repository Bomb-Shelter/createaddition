package com.mrh0.createaddition.recipe.charging;


import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.simibubi.create.content.processing.recipe.ProcessingRecipeParams;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.function.Function;

public class ChargingRecipeParams extends ProcessingRecipeParams {
    public static MapCodec<ChargingRecipeParams> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            codec(ChargingRecipeParams::new).forGetter(Function.identity()),
            Codec.LONG.fieldOf("energy").forGetter(ChargingRecipeParams::getEnergy),
            Codec.INT.fieldOf("max_charge_rate").forGetter(ChargingRecipeParams::getMaxChargeRate)
    ).apply(instance, (params, energy, maxChargeRate) -> {
        params.energy = energy;
        params.maxChargeRate = maxChargeRate;
        return params;
    }));
    public static StreamCodec<RegistryFriendlyByteBuf, ChargingRecipeParams> STREAM_CODEC = streamCodec(ChargingRecipeParams::new);

    long energy;
    int maxChargeRate;

    public long getEnergy() {
        return energy;
    }

    public int getMaxChargeRate() {
        return maxChargeRate;
    }

    @Override
    protected void encode(RegistryFriendlyByteBuf buffer) {
        super.encode(buffer);
        ByteBufCodecs.VAR_LONG.encode(buffer, energy);
        ByteBufCodecs.INT.encode(buffer, maxChargeRate);
    }

    @Override
    protected void decode(RegistryFriendlyByteBuf buffer) {
        super.decode(buffer);
        energy = ByteBufCodecs.VAR_LONG.decode(buffer);
        maxChargeRate = ByteBufCodecs.INT.decode(buffer);
    }
}

package com.mrh0.createaddition.datagen.TagProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CAFluids;
import com.simibubi.create.foundation.mixin.accessor.fabric.TagAppenderAccessor;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.FluidTagsProvider;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.material.Fluid;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CAFluidTagProvider extends FabricTagProvider.FluidTagProvider {
    public CAFluidTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> provider) {
        super(output, provider);
    }

    @Override
    protected WrappedTagAppender tag(TagKey<Fluid> tag) {
        return new WrappedTagAppender(super.tag(tag));
    }

    protected static class WrappedTagAppender extends TagAppender<Fluid> {
        private final TagAppender<Fluid> wrapped;

        protected WrappedTagAppender(TagAppender<Fluid> original) {
            super(((TagAppenderAccessor) original).getBuilder());
            this.wrapped = original;
        }

        public WrappedTagAppender add(Fluid... fluids) {
            for (Fluid fluid : fluids) {
                this.wrapped.addOptional(BuiltInRegistries.FLUID.getKey(fluid));
            }

            return this;
        }
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CATagRegister.Fluids.BIOFUEL).add( new Fluid[]{
                CAFluids.BIOETHANOL.get(),
                CAFluids.BIOETHANOL.getSource()
        });
        tag(CATagRegister.Fluids.PLANTOIL).add(new Fluid[] {
                CAFluids.SEED_OIL.get(),
                CAFluids.SEED_OIL.getSource()
        });

        tag(FluidTags.WATER).add(new Fluid[] {
                CAFluids.SEED_OIL.get(),
                CAFluids.SEED_OIL.getSource(),
                CAFluids.BIOETHANOL.get(),
                CAFluids.BIOETHANOL.getSource(),
        });

        tag(CATagRegister.Fluids.CREOSOTE);
        tag(CATagRegister.Fluids.CRUDE_OIL);

    }
}

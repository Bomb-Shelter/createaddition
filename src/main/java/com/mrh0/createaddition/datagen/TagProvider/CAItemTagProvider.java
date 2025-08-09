package com.mrh0.createaddition.datagen.TagProvider;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAFluids;
import com.mrh0.createaddition.index.CAItems;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.mixin.accessor.fabric.TagAppenderAccessor;
import io.github.fabricators_of_create.porting_lib.tags.Tags;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class CAItemTagProvider extends FabricTagProvider.ItemTagProvider {


    public CAItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, FabricTagProvider.BlockTagProvider blockTags) {
        super(output, lookupProvider, blockTags);
    }

    @Override
    protected WrappedTagAppender tag(TagKey<Item> tag) {
        return new WrappedTagAppender(super.tag(tag));
    }

    protected static class WrappedTagAppender extends TagAppender<Item> {
        private final TagAppender<Item> wrapped;

        protected WrappedTagAppender(TagAppender<Item> original) {
            super(((TagAppenderAccessor) original).getBuilder());
            this.wrapped = original;
        }

        public WrappedTagAppender add(ItemLike... items) {
            for (ItemLike item : items) {
                this.wrapped.addOptional(BuiltInRegistries.ITEM.getKey(item.asItem()));
            }

            return this;
        }
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(CATagRegister.Items.ELECTRUM_BLOCKS).add(new Item[]{
                CABlocks.ELECTRUM_BLOCK.asItem()
        });
        tag(CATagRegister.Items.DIAMOND_DUSTS).add(new Item[]{
                CAItems.DIAMOND_GRIT.asItem()
        });
        tag(CATagRegister.Items.OBSIDIAN_DUSTS).add(AllItems.POWDERED_OBSIDIAN.asItem());
        tag(CATagRegister.Items.BIO_FUELS).add(new Item[]{
           CAItems.BIOMASS.asItem()
        });
        tag(CATagRegister.Items.ELECTRUM_INGOTS).add(new Item[] {
                CAItems.ELECTRUM_INGOT.asItem()
        });
        tag(CATagRegister.Items.ELECTRUM_NUGGETS).add(new Item[]{
                CAItems.ELECTRUM_NUGGET.asItem()
        });
        tag(CATagRegister.Items.ELECTRUM_PLATES).add(new Item[] {
                CAItems.ELECTRUM_SHEET.asItem()
        });
        tag(CATagRegister.Items.ZINC_PLATES).add(new Item[]{
           CAItems.ZINC_SHEET.asItem()
        });
        tag(CATagRegister.Items.ALL_METAL_RODS).add(new Item[] {
           CAItems.GOLD_ROD.asItem(),
           CAItems.IRON_ROD.asItem(),
           CAItems.ELECTRUM_ROD.asItem(),
           CAItems.BRASS_ROD.asItem(),
           CAItems.COPPER_ROD.asItem()
        });
        tag(CATagRegister.Items.GOLD_RODS).add(CAItems.GOLD_ROD.asItem());
        tag(CATagRegister.Items.IRON_RODS).add(CAItems.IRON_ROD.asItem());
        tag(CATagRegister.Items.ELECTRUM_RODS).add(CAItems.ELECTRUM_ROD.asItem());
        tag(CATagRegister.Items.BRASS_RODS).add(CAItems.BRASS_ROD.asItem());
        tag(CATagRegister.Items.COPPER_RODS).add(CAItems.COPPER_ROD.asItem());

        tag(CATagRegister.Items.ALL_METAL_WIRES).add(new Item[] {
                CAItems.GOLD_WIRE.asItem(),
                CAItems.IRON_WIRE.asItem(),
                CAItems.ELECTRUM_WIRE.asItem(),
                CAItems.COPPER_WIRE.asItem()
        });
        tag(CATagRegister.Items.GOLD_WIRES).add(CAItems.GOLD_WIRE.asItem());
        tag(CATagRegister.Items.IRON_WIRES).add(CAItems.IRON_WIRE.asItem());
        tag(CATagRegister.Items.ELECTRUM_WIRES).add(CAItems.ELECTRUM_WIRE.asItem());
        tag(CATagRegister.Items.COPPER_WIRES).add(CAItems.COPPER_WIRE.asItem());

        tag(CATagRegister.Items.DUSTS).addTags(new TagKey[] {
                CATagRegister.Items.DIAMOND_DUSTS,
                CATagRegister.Items.OBSIDIAN_DUSTS
        });


        tag(CATagRegister.Items.FUELS).addTag(CATagRegister.Items.BIO_FUELS);
        tag(CATagRegister.Items.INGOTS).addTag(CATagRegister.Items.ELECTRUM_INGOTS);
        tag(CATagRegister.Items.NUGGETS).addTag(CATagRegister.Items.ELECTRUM_NUGGETS);
        tag(CATagRegister.Items.PLATES).addTags(new TagKey[]{
                CATagRegister.Items.ELECTRUM_PLATES,
                CATagRegister.Items.ZINC_PLATES
        });
        tag(CATagRegister.Items.RODS).addTag(CATagRegister.Items.ALL_METAL_RODS);
        tag(CATagRegister.Items.WIRES).addTag(CATagRegister.Items.ALL_METAL_WIRES);

        tag(AllTags.AllItemTags.SANDPAPER.tag).add(CAItems.DIAMOND_GRIT_SANDPAPER.asItem());
        tag(AllTags.AllItemTags.UPRIGHT_ON_BELT.tag).add(new Item[]{
            CAItems.CAKE_BASE.asItem(),
            CAItems.CAKE_BASE_BAKED.asItem(),
            CABlocks.CHOCOLATE_CAKE.asItem(),
            CABlocks.HONEY_CAKE.asItem(),
            Items.CAKE,
            CAFluids.SEED_OIL.get().getBucket(),
            CAFluids.BIOETHANOL.get().getBucket()
        });

        tag(CATagRegister.Items.PLANT_FOODS).add(new Item[] {
                Items.APPLE,
                Items.MELON_SLICE,
                Items.POTATO,
                Items.SWEET_BERRIES,
                Items.BEETROOT,
                Items.GLOW_BERRIES,
                Items.CARROT,
                Items.CHORUS_FRUIT
        });

        tag(CATagRegister.Items.PLANTS).add(new Item[] {
                Items.CACTUS,
                Items.BAMBOO,
                Items.KELP,
                Items.VINE,
                Items.LILY_PAD,
                Items.WEEPING_VINES,
                Items.TWISTING_VINES,
                Items.SHORT_GRASS,
                Items.FERN,
                Items.TALL_GRASS,
                Items.LARGE_FERN,
                Items.SEAGRASS,
                Items.WARPED_ROOTS,
                Items.CRIMSON_ROOTS,
                Items.HANGING_ROOTS,
                Items.NETHER_SPROUTS,
                Items.CHORUS_PLANT,
                Items.CHORUS_FLOWER,
                Items.BROWN_MUSHROOM,
                Items.RED_MUSHROOM,
                Items.CRIMSON_FUNGUS,
                Items.WARPED_FUNGUS,
                Items.SUGAR_CANE,
                Items.DEAD_BUSH,
                Items.SEA_PICKLE,
                Items.MOSS_BLOCK,
                Items.BIG_DRIPLEAF,
                Items.SMALL_DRIPLEAF,
                Items.MOSS_CARPET,
                Items.GLOW_LICHEN
        });

        tag(CATagRegister.Items.SPOOLS).add(new Item[] {
                CAItems.COPPER_SPOOL.asItem(),
                CAItems.GOLD_SPOOL.asItem(),
                CAItems.ELECTRUM_SPOOL.asItem(),
                CAItems.FESTIVE_SPOOL.asItem()
        });

        tag(ItemTags.BEACON_PAYMENT_ITEMS).addTag(CATagRegister.Items.ELECTRUM_INGOTS);

        tag(CATagRegister.Items.LARGE_CONNECTOR_USABLE_RODS).addTags(new TagKey[]{
                CATagRegister.Items.GOLD_RODS,
                CATagRegister.Items.ELECTRUM_RODS
        });

        tag(CATagRegister.Items.MODULAR_ACCUMULATOR_USABLE_WIRES).addTags(new TagKey[]{
                CATagRegister.Items.GOLD_WIRES,
                CATagRegister.Items.ELECTRUM_WIRES
        });

        tag(Tags.Items.STORAGE_BLOCKS).add(
                CABlocks.BIOMASS_PALLET.asItem(),
                CABlocks.ELECTRUM_BLOCK.asItem()
        );

        tag(CATagRegister.Items.STORAGE_BLOCKS_BIO).add(CABlocks.BIOMASS_PALLET.asItem());
        tag(CATagRegister.Items.STORAGE_BLOCKS_BIOMASS).add(CABlocks.BIOMASS_PALLET.asItem());
        tag(CATagRegister.Items.STORAGE_BLOCKS_ELECTRUM).add(CABlocks.ELECTRUM_BLOCK.asItem());

        tag(CATagRegister.Items.FOODS).add(
                CABlocks.CHOCOLATE_CAKE.asItem(),
                CABlocks.HONEY_CAKE.asItem()
        );

        tag(CATagRegister.Items.FOODS_EDIBLE).add(
                CABlocks.CHOCOLATE_CAKE.asItem(),
                CABlocks.HONEY_CAKE.asItem()
        );
    }
}

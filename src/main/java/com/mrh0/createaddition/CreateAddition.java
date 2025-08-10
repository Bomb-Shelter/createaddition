package com.mrh0.createaddition;

import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.event.GameEvents;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.index.CASounds;
import com.mrh0.createaddition.network.*;
import com.mrh0.createaddition.network.fabric.DirectionalPayloadHandler;
import com.mrh0.createaddition.network.fabric.PayloadRegistrar;
import com.mrh0.createaddition.ponder.CAPonderPlugin;
import com.mrh0.createaddition.trains.schedule.CASchedule;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.foundation.item.ItemDescription;
import com.simibubi.create.foundation.item.KineticStats;
import io.github.fabricators_of_create.porting_lib.config.ConfigRegistry;
import io.github.fabricators_of_create.porting_lib.config.ModConfig;
import io.github.fabricators_of_create.porting_lib.config.ModConfigSpec;
import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.registry.DeferredRegister;
import net.createmod.catnip.lang.FontHelper;
import net.createmod.catnip.platform.CatnipServices;
import net.createmod.ponder.foundation.PonderIndex;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.level.ItemLike;
import net.neoforged.fml.config.ModConfigs;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.mojang.brigadier.CommandDispatcher;
import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.commands.CCApiCommand;
import com.simibubi.create.foundation.data.CreateRegistrate;
import com.simibubi.create.foundation.item.TooltipModifier;
import com.simibubi.create.api.boiler.BoilerHeater;

import static net.minecraft.network.chat.Component.translatable;

public class CreateAddition implements ModInitializer {
    public static final Logger LOGGER = LogManager.getLogger();

    public static final String MODID = "createaddition";

    public static boolean IE_ACTIVE = false;
    public static boolean CC_ACTIVE = false;
    public static boolean AE2_ACTIVE = false;

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(CreateAddition.MODID)
            .defaultCreativeTab((ResourceKey<CreativeModeTab>) null)
            .setTooltipModifierFactory(item ->
                    new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                            .andThen(TooltipModifier.mapNull(KineticStats.create(item)))
            );

    static {
        REGISTRATE.setTooltipModifierFactory(item -> new ItemDescription.Modifier(item, FontHelper.Palette.STANDARD_CREATE)
                .andThen(TooltipModifier.mapNull(KineticStats.create(item))));
    }

    private static final ItemLike[] excludedItemsList = new ItemLike[]{
            CAItems.CAKE_BASE,
            CAItems.CAKE_BASE_BAKED,
            CAItems.BIOMASS
    };

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MAIN_TAB = CREATIVE_MODE_TABS.register(MODID, () -> FabricItemGroup.builder()
            //.withTabsBefore(CreativeModeTabs.SPAWN_EGGS)
            .icon(() -> CABlocks.ELECTRIC_MOTOR.get().asItem().getDefaultInstance())
            .title(Component.translatable("itemGroup.createaddition.main"))
            .displayItems((itemDisplayParameters, output) -> REGISTRATE.getAll(Registries.ITEM).forEach((item -> {
                for (ItemLike excluded : excludedItemsList) {
                    if (item.is(excluded.asItem())) return;
                }
                output.accept(item.get());
            })))
            .build());

    public void onInitialize() {
        this.setup();
        this.onRegister();
        registerPackets();
        onRegisterCommandEvent();
        //FMLJavaModLoadingContext.get().getModEventBus().addGenericListener(RecipeSerializer.class, CARecipes::register);

        //IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //MinecraftForge.EVENT_BUS.register(this);

        ConfigRegistry.registerConfig(MODID, ModConfig.Type.COMMON, CommonConfig.COMMON_CONFIG);
        //

        IE_ACTIVE = FabricLoader.getInstance().isModLoaded("immersiveengineering");
        CC_ACTIVE = FabricLoader.getInstance().isModLoaded("computercraft");
        AE2_ACTIVE = FabricLoader.getInstance().isModLoaded("ae2");

        CABlocks.register();
        CABlockEntities.register();
        CAItems.register();
        CREATIVE_MODE_TABS.register();
        CAFluids.register();
        CAEffects.register();
        CARecipes.register();
        CASounds.register();
        CASchedule.register();
        CADamageTypes.register();
        CADisplaySources.register();

        REGISTRATE.registerEventListeners();
        CACapabilities.register();
        CatnipServices.PLATFORM.executeOnClientOnly(() -> CAPartials::init);
        CatnipServices.PLATFORM.executeOnClientOnly(() -> this::doClientStuff);

        GameEvents.init();

        this.postInit();
    }

    private void setup() {
    	// BlockStressValues.CAPACITIES.registerProvider(MODID, AllConfigs.server().kinetics.stressValues);
    }

    private void doClientStuff() {
    	// event.enqueueWork(CAPonder::register);
        CAItemProperties.register();
        CAFluids.registerClient();

        PonderIndex.addPlugin(new CAPonderPlugin());

        RenderType cutout = RenderType.cutoutMipped();

        BlockRenderLayerMap.INSTANCE.putBlock(CABlocks.TESLA_COIL.get(), cutout);
        BlockRenderLayerMap.INSTANCE.putBlock(CABlocks.BARBED_WIRE.get(), cutout);
        BlockRenderLayerMap.INSTANCE.putBlock(CABlocks.SMALL_LIGHT_CONNECTOR.get(), cutout);
    }

    public void postInit() {
        //Network.registerMessage(0, ObservePacketLegacy.class, ObservePacketLegacy::encode, ObservePacketLegacy::decode, ObservePacketLegacy::handle);
        //Network.registerMessage(1, EnergyNetworkPacket.class, EnergyNetworkPacket::encode, EnergyNetworkPacket::decode, EnergyNetworkPacket::handle);

        BoilerHeater.REGISTRY.register(CABlocks.LIQUID_BLAZE_BURNER.get(), (level, pos, state) -> {
            BlazeBurnerBlock.HeatLevel value = state.getValue(BlazeBurnerBlock.HEAT_LEVEL);
            if (value == BlazeBurnerBlock.HeatLevel.NONE) return -1;
            if (value == BlazeBurnerBlock.HeatLevel.SEETHING) return 2;
            if (value.isAtLeast(BlazeBurnerBlock.HeatLevel.FADING)) return 1;
            return 0;
        });

        LOGGER.info("Create Crafts & Additions Initialized!");
    }

    public void onRegister() {
        CAArmInteractions.register();
    }

    public void onRegisterCommandEvent() {
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            CCApiCommand.register(dispatcher);
        });
    }

    private static final String PROTOCOL = "1";
    public static void registerPackets() {
        PayloadRegistrar registrar = new PayloadRegistrar();

        registrar.playBidirectional(
                ObservePacketPayload.TYPE,
                ObservePacketPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleObservePayload,
                        ServerPayloadHandler::handleObservePayload
                )
        );

        registrar.playBidirectional(
                EnergyNetworkPacketPayload.TYPE,
                EnergyNetworkPacketPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleEnergyNetworkPayload,
                        ServerPayloadHandler::handleEnergyNetworkPayload
                )
        );

        registrar.playBidirectional(
                TimeRemainingPacketPayload.TYPE,
                TimeRemainingPacketPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handleTimeRemainingPayload,
                        ServerPayloadHandler::handleTimeRemainingPayload
                )
        );
    }

    public static ResourceLocation asResource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}

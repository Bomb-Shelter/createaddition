package com.mrh0.createaddition.index;

import com.mrh0.createaddition.CreateAddition;
import com.simibubi.create.AllFluids;

import com.simibubi.create.infrastructure.fabric.client.CustomRenderHandlerFluidType;
import com.tterrag.registrate.util.entry.FluidEntry;
import io.github.fabricators_of_create.porting_lib.event.client.FogEvents;
import io.github.fabricators_of_create.porting_lib.fluids.BaseFlowingFluid;
import io.github.fabricators_of_create.porting_lib.fluids.FluidStack;
import net.createmod.catnip.platform.CatnipServices;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.render.fluid.v1.FluidRenderHandlerRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.joml.Vector3f;

public class CAFluids {
	static {
		CreateAddition.REGISTRATE.setCreativeTab(CreateAddition.MAIN_TAB);
	}

	public static FluidEntry<BaseFlowingFluid.Flowing> SEED_OIL;
	public static FluidEntry<BaseFlowingFluid.Flowing> BIOETHANOL;


	/**
	 * (From Create)
	 * Removing alpha from tint prevents optifine from forcibly applying biome
	 * colors to modded fluids (Makes translucent fluids disappear)
	 */
	private static class NoColorFluidAttributes extends AllFluids.TintedFluidType {

		public NoColorFluidAttributes(Properties properties, ResourceLocation stillTexture,
			ResourceLocation flowingTexture) {
			super(properties, stillTexture, flowingTexture);
		}

		@Override
		protected int getTintColor(FluidStack stack) {
			return NO_TINT;
		}

		@Override
		public int getTintColor(FluidState state, BlockAndTintGetter world, BlockPos pos) {
			return 0x00ffffff;
		}

	}

	public static void register() {
		var seedOil = CreateAddition.REGISTRATE.fluid("seed_oil", CreateAddition.asResource("fluid/seed_oil_still"), CreateAddition.asResource("fluid/seed_oil_flow"),
				NoColorFluidAttributes::new)
				.properties(b -> b.viscosity(2000)
						.density(1400))
				.fluidProperties(p -> p.levelDecreasePerBlock(2)
						.tickRate(15)
						.slopeFindDistance(6)
						.explosionResistance(100f))
				.source(BaseFlowingFluid.Flowing.Source::new);

		var seedOilBucket = seedOil.bucket()
			.properties(p -> p.stacksTo(1))
			.register();
		SEED_OIL = seedOil.register();

		var bioethanol = CreateAddition.REGISTRATE.fluid("bioethanol", CreateAddition.asResource("fluid/bioethanol_still"), CreateAddition.asResource("fluid/bioethanol_flow"),
				NoColorFluidAttributes::new)
				.properties(b -> b.viscosity(2500)
						.density(1600))
				.fluidProperties(p -> p.levelDecreasePerBlock(2)
						.tickRate(15)
						.slopeFindDistance(6)
						.explosionResistance(100f))
				.source(BaseFlowingFluid.Flowing.Source::new);
		var bioethanolBucket = bioethanol.bucket()
			.properties(p -> p.stacksTo(1))
			.register();
		BIOETHANOL = bioethanol.register();

		registerClient();
	}

	public static void registerClient() {
		CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> {
			ClientFluidEvents.registerFluidRenderHandler(SEED_OIL);
			ClientFluidEvents.registerFluidRenderHandler(BIOETHANOL);
		});
	}

	@Environment(EnvType.CLIENT)
	private static class ClientFluidEvents {
		public static <T extends BaseFlowingFluid> void registerFluidRenderHandler(FluidEntry<T> fluid) {
			if (fluid.getType() instanceof CustomRenderHandlerFluidType fluidType) {
				FluidRenderHandlerRegistry.INSTANCE.register(fluid.getSource(), fluid.get(), fluidType.getRenderHandler());
			}

			if (fluid.getType() instanceof AllFluids.TintedFluidType tintedFluidType) {
				FogEvents.SET_COLOR.register((data, partialTicks) -> {
					Level level = data.getCamera().getEntity().level();
					FluidState fluidState = level.getFluidState(data.getCamera().getBlockPosition());
					if (fluidState.is((Fluid) fluid.getSource()) || fluidState.is(fluid.get())) {
						Vector3f modified = tintedFluidType.modifyFogColor(data.getCamera(), partialTicks, (ClientLevel) level, Minecraft.getInstance().options.getEffectiveRenderDistance(), Minecraft.getInstance().gameRenderer.getDarkenWorldAmount(partialTicks), new Vector3f(data.getRed(), data.getGreen(), data.getBlue()));
						data.setRed(modified.x);
						data.setGreen(modified.y);
						data.setBlue(modified.z);
					}
				});

				FogEvents.RENDER_FOG.register((mode, type, camera, partialTick, renderDistance, nearDistance, farDistance, shape, fogData) -> {
					Level level = camera.getEntity().level();
					FluidState fluidState = level.getFluidState(camera.getBlockPosition());
					if (fluidState.is((Fluid) fluid.getSource()) || fluidState.is(fluid.get())) {
						tintedFluidType.modifyFogRender(camera, mode, renderDistance, partialTick, nearDistance, farDistance, shape);
					}

					return false;
				});
			}
		}
	}
}

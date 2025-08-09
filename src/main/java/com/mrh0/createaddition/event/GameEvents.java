package com.mrh0.createaddition.event;

import com.mrh0.createaddition.blocks.liquid_blaze_burner.LiquidBlazeBurnerBlock;
import com.mrh0.createaddition.blocks.portable_energy_interface.PortableEnergyManager;
import com.mrh0.createaddition.debug.CADebugger;
import com.mrh0.createaddition.energy.network.EnergyNetworkManager;
import com.mrh0.createaddition.index.CABlocks;
import com.mrh0.createaddition.index.CAItems;
import com.mrh0.createaddition.network.ObservePacketPayload;
import com.simibubi.create.AllBlocks;

import com.simibubi.create.content.processing.burner.BlazeBurnerBlock;
import com.simibubi.create.content.processing.burner.BlazeBurnerBlockEntity;
import io.github.fabricators_of_create.porting_lib.entity.events.player.PlayerInteractEvent;
import io.github.fabricators_of_create.porting_lib.level.events.LevelEvent;
import net.createmod.catnip.platform.CatnipServices;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.state.BlockState;

public class GameEvents {
	public static void init() {
		ServerTickEvents.START_WORLD_TICK.register(GameEvents::levelTickEvent);
		ServerTickEvents.START_SERVER_TICK.register(server -> serverTickEvent());

		CatnipServices.PLATFORM.executeOnClientOnly(() -> GameEvents::initClient);

		LevelEvent.Load.EVENT.register(GameEvents::loadEvent);
		LevelEvent.Unload.EVENT.register(GameEvents::LevelUnload);
		PlayerInteractEvent.RightClickBlock.EVENT.register(GameEvents::interact);
	}

	public static void initClient() {
		ClientTickEvents.END_CLIENT_TICK.register(GameEvents::clientTickEvent);
	}

	public static void levelTickEvent(ServerLevel level) {
		//if(evt.getLevel().isClientSide()) return;
		// if (evt == Phase.END) return;
		EnergyNetworkManager.tickWorld(level);
	}

	public static void serverTickEvent() {
		//if (evt.phase == Phase.END) return;
		// Using ServerTick instead of WorldTick because some contraptions can switch worlds.
		PortableEnergyManager.tick();
	}

	@Environment(EnvType.CLIENT)
	public static void clientTickEvent(Minecraft minecraft) {
		//if (evt.phase == Phase.START) return;
		ObservePacketPayload.tick();
		CADebugger.tick();
	}

	public static void loadEvent(LevelEvent.Load evt) {
		if(evt.getLevel().isClientSide()) return;
		new EnergyNetworkManager(evt.getLevel());
	}

	public static void LevelUnload(LevelEvent.Unload event) {
		if (!event.getLevel().isClientSide()) {
			EnergyNetworkManager.instances.remove(event.getLevel());
		}
	}

    public static void interact(PlayerInteractEvent.RightClickBlock evt) {
		try {
			if(evt.getLevel().isClientSide()) return;
			BlockState state = evt.getLevel().getBlockState(evt.getPos());
			if(evt.getItemStack().getItem() == CAItems.STRAW.get() && evt.getLevel().getBlockEntity(evt.getPos()) instanceof BlazeBurnerBlockEntity) {
				if(state.is(AllBlocks.BLAZE_BURNER.get())) {
					BlockState newState = CABlocks.LIQUID_BLAZE_BURNER.getDefaultState()
							.setValue(BlazeBurnerBlock.HEAT_LEVEL, BlazeBurnerBlock.HeatLevel.SMOULDERING/*state.getValue(BlazeBurnerBlock.HEAT_LEVEL)*/)
							.setValue(LiquidBlazeBurnerBlock.FACING, state.getValue(BlazeBurnerBlock.FACING));
					evt.getLevel().setBlockAndUpdate(evt.getPos(), newState);
					if(!evt.getEntity().isCreative())
						evt.getItemStack().shrink(1);
					evt.setCancellationResult(InteractionResult.SUCCESS);
	            	evt.setCanceled(true);
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}


	private static final Direction[] horizontalDirections = {Direction.NORTH, Direction.EAST, Direction.SOUTH, Direction.WEST};
	/*@SubscribeEvent
	public static void grow(BlockEvent.CropGrowEvent.Pre evt) {
		try {
			double chance = 0.001d;
			for(Direction dir : horizontalDirections) {
				BlockState state = evt.getLevel().getBlockState(evt.getPos().relative(dir));
				if(state.is(CABlocks.HARMFUL_PLANT.get()))
					chance *= state.getValue(HarmfulPlantBlock.AGE)*3;
			}
			if(Math.random() < chance)
				evt.getLevel().setBlock(evt.getPos(), CABlocks.HARMFUL_PLANT.getDefaultState(), 3);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}*/
}

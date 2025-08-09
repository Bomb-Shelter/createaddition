package com.mrh0.createaddition.blocks.tesla_coil;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.config.CommonConfig;
import com.mrh0.createaddition.energy.AbstractElectricBlockEntity;
import com.mrh0.createaddition.index.*;
import com.mrh0.createaddition.network.IObserveBlockEntity;
import com.mrh0.createaddition.network.ObservePacketPayload;
import com.mrh0.createaddition.network.TimeRemainingPacketPayload;
import com.mrh0.createaddition.recipe.charging.ChargingRecipe;
import com.mrh0.createaddition.sound.CASoundScapes;
import com.mrh0.createaddition.util.Util;
import com.simibubi.create.api.equipment.goggles.IHaveGoggleInformation;
import com.simibubi.create.content.kinetics.belt.behaviour.BeltProcessingBehaviour;
import com.simibubi.create.content.kinetics.belt.behaviour.TransportedItemStackHandlerBehaviour;
import com.simibubi.create.content.kinetics.belt.transport.TransportedItemStack;
import com.simibubi.create.foundation.blockEntity.behaviour.BlockEntityBehaviour;
import com.simibubi.create.infrastructure.fabric.transfer.CreateTransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.TransferUtil;
import io.github.fabricators_of_create.porting_lib.transfer.item.ItemStackHandler;
import io.github.fabricators_of_create.porting_lib.transfer.item.RecipeWrapper;
import net.createmod.catnip.platform.CatnipServices;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.transfer.v1.context.ContainerItemContext;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import team.reborn.energy.api.EnergyStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TeslaCoilBlockEntity extends AbstractElectricBlockEntity implements IHaveGoggleInformation, IObserveBlockEntity {

	private Optional<RecipeHolder<ChargingRecipe>> recipeCache = Optional.empty();

	private final ItemStackHandler inputInv;
	private long chargeAccumulator;
	protected int poweredTimer = 0;

	public TeslaCoilBlockEntity(BlockEntityType<?> tileEntityTypeIn, BlockPos pos, BlockState state) {
		super(tileEntityTypeIn, pos, state);
		inputInv = new ItemStackHandler(1);
	}

	public static void registerCapabilities() {
		EnergyStorage.SIDED.registerForBlockEntity(
			(be, context) -> be.localEnergy,
			CABlockEntities.TESLA_COIL.get()
		);
	}

	@Override
	public int getCapacity() {
		return Util.max(CommonConfig.TESLA_COIL_CAPACITY.get(), CommonConfig.TESLA_COIL_CHARGE_RATE.get(), CommonConfig.TESLA_COIL_RECIPE_CHARGE_RATE.get());
	}

	@Override
	public int getMaxIn() {
		return CommonConfig.TESLA_COIL_MAX_INPUT.get();
	}

	@Override
	public int getMaxOut() {
		return 0;
	}

	public BeltProcessingBehaviour processingBehaviour;

	@Override
	public void addBehaviours(List<BlockEntityBehaviour> behaviours) {
		super.addBehaviours(behaviours);
		processingBehaviour =
			new BeltProcessingBehaviour(this).whenItemEnters((s, i) -> TeslaCoilBeltCallbacks.onItemReceived(s, i, this))
				.whileItemHeld((s, i) -> TeslaCoilBeltCallbacks.whenItemHeld(s, i, this));
		behaviours.add(processingBehaviour);
	}

	@Override
	public boolean isEnergyInput(Direction side) {
		return side != getBlockState().getValue(TeslaCoilBlock.FACING).getOpposite();
	}

	@Override
	public boolean isEnergyOutput(Direction side) {
		return false;
	}

	public int getConsumption() {
		return CommonConfig.TESLA_COIL_CHARGE_RATE.get();
	}

	protected float getItemCharge(EnergyStorage energy) {
		if (energy == null) return 0f;
		return (float) energy.getAmount() / (float) energy.getCapacity();
	}

	protected BeltProcessingBehaviour.ProcessingResult onCharge(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
        return chargeCompoundAndStack(transported, handler);
	}

	private void doDmg() {
		localEnergy.internalConsumeEnergy(CommonConfig.TESLA_COIL_HURT_ENERGY_REQUIRED.get());
		BlockPos origin = getBlockPos().relative(getBlockState().getValue(TeslaCoilBlock.FACING).getOpposite());
		List<LivingEntity> ents = getLevel().getEntitiesOfClass(LivingEntity.class, new AABB(origin).inflate(CommonConfig.TESLA_COIL_HURT_RANGE.get()));
		boolean zapped = false;
		for(LivingEntity e : ents) {
			if(e == null) return;

			boolean allChain = true;
			for(ItemStack armor : e.getArmorSlots()) {
				if(armor.is(Items.CHAINMAIL_BOOTS)) continue;
				if(armor.is(Items.CHAINMAIL_LEGGINGS)) continue;
				if(armor.is(Items.CHAINMAIL_CHESTPLATE)) continue;
				if(armor.is(Items.CHAINMAIL_HELMET)) continue;
				allChain = false;
				break;
			}
			if(allChain) continue;

			int dmg = CommonConfig.TESLA_COIL_HURT_DMG_MOB.get();
			int time = CommonConfig.TESLA_COIL_HURT_EFFECT_TIME_MOB.get();
			if(e instanceof Player) {
				dmg = CommonConfig.TESLA_COIL_HURT_DMG_PLAYER.get();
				time = CommonConfig.TESLA_COIL_HURT_EFFECT_TIME_PLAYER.get();
			}

			if(dmg > 0) {
				e.hurt(CADamageTypes.barbedWire(level), dmg);
				if (!zapped) {
					if (CommonConfig.AUDIO_ENABLED.get()) level.playSound(null, worldPosition, CASounds.LOUD_ZAP.get(), SoundSource.BLOCKS, 0.6f, 1f);
					zapped = true;
				}
			}
			if(time > 0) e.addEffect(new MobEffectInstance(CAEffects.SHOCKING, time));
		}
	}

	int dmgTick = 0;
	int zapTimer = 200;

	@Override
	public void tick() {
		super.tick();
		if(level == null) return;

		if (level.isClientSide) {
			CatnipServices.PLATFORM.executeOnClientOnly(() -> this::tickAudio);
			return;
		}
		int signal = level.getBestNeighborSignal(getBlockPos());
		if(signal > 0 && localEnergy.getAmount() >= CommonConfig.TESLA_COIL_HURT_ENERGY_REQUIRED.get()) poweredTimer = 10;

		dmgTick++;
		if((dmgTick%= CommonConfig.TESLA_COIL_HURT_FIRE_COOLDOWN.get()) == 0 && localEnergy.getAmount() >= CommonConfig.TESLA_COIL_HURT_ENERGY_REQUIRED.get() && signal > 0) doDmg();

		if(poweredTimer > 0) {
			if (zapTimer == 0) {
				if (CommonConfig.AUDIO_ENABLED.get()) level.playSound(null, worldPosition, CASounds.LITTLE_ZAP.get(), SoundSource.BLOCKS, 0.1f, 1f);
				zapTimer = level.random.nextInt(100, 300);
			}
			zapTimer--;

			if(!isPoweredState()) CABlocks.TESLA_COIL.get().setPowered(level, getBlockPos(), true);
			poweredTimer--;
		}
		else if(isPoweredState()) CABlocks.TESLA_COIL.get().setPowered(level, getBlockPos(), false);
	}

	@Environment(EnvType.CLIENT)
	public void tickAudio() {
		if (!isPoweredState()) return;
		if (CommonConfig.AUDIO_ENABLED.get()) CASoundScapes.play(CASoundScapes.AmbienceGroup.TESLA, worldPosition, 1f);
	}

	public boolean isPoweredState() {
		return getBlockState().getValue(TeslaCoilBlock.POWERED);
	}

	protected BeltProcessingBehaviour.ProcessingResult chargeCompoundAndStack(TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {

		ItemStack stack = transported.stack;
		if(stack == null) return BeltProcessingBehaviour.ProcessingResult.PASS;
		if(chargeStack(stack, transported, handler)) {
			poweredTimer = 10;
			return BeltProcessingBehaviour.ProcessingResult.HOLD;
		}
		else if(chargeRecipe(stack, transported, handler)) {
			poweredTimer = 10;
			return BeltProcessingBehaviour.ProcessingResult.HOLD;
		}
		return BeltProcessingBehaviour.ProcessingResult.PASS;
	}

	protected boolean chargeStack(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		EnergyStorage es = EnergyStorage.ITEM.find(stack, ContainerItemContext.withConstant(stack));
		if (es == null) return false;
		try (Transaction transaction = TransferUtil.getTransaction()) {
			if(es.insert(1, transaction) != 1) return false;
		}
		if(localEnergy.getAmount() < stack.getCount()) return false;
		try (Transaction transaction = TransferUtil.getTransaction()) {
			localEnergy.internalConsumeEnergy(es.insert(Math.min(getConsumption(), localEnergy.getAmount()), transaction));
			transaction.commit();
		}
		return true;
	}

	private long energyRemoved = 0;
	private boolean chargeRecipe(ItemStack stack, TransportedItemStack transported, TransportedItemStackHandlerBehaviour handler) {
		if(this.getLevel() == null) return false;
		if(!inputInv.getStackInSlot(0).is(stack.getItem())) {
			inputInv.setStackInSlot(0, stack);
			recipeCache = find(new RecipeWrapper(inputInv), this.getLevel());
			chargeAccumulator = 0;
		}
		if(recipeCache.isPresent()) {
			ChargingRecipe recipe = recipeCache.get().value();
			energyRemoved = localEnergy.internalConsumeEnergy(Util.min(CommonConfig.TESLA_COIL_RECIPE_CHARGE_RATE.get(), recipe.getEnergy() - chargeAccumulator, recipe.getMaxChargeRate()));
			chargeAccumulator += energyRemoved;
			if(chargeAccumulator >= recipe.getEnergy()) {
				TransportedItemStack remainingStack = transported.copy();
				TransportedItemStack result = transported.copy();
				result.stack = recipe.getResultItem(this.getLevel().registryAccess()).copy();
				remainingStack.stack.shrink(1);
				List<TransportedItemStack> outList = new ArrayList<>();
				outList.add(result);
				handler.handleProcessingOnItem(transported, TransportedItemStackHandlerBehaviour.TransportedResult.convertToAndLeaveHeld(outList, remainingStack));
				chargeAccumulator = 0;

				if (CommonConfig.AUDIO_ENABLED.get()) level.playSound(null, worldPosition, CASounds.LITTLE_ZAP.get(), SoundSource.BLOCKS, 0.1f, 1f);
			}
			return true;
		}
		return false;
	}

	public Optional<RecipeHolder<ChargingRecipe>> find(RecipeWrapper wrapper, Level level) {
		return level.getRecipeManager().getRecipeFor(CARecipes.CHARGING_TYPE.get(), wrapper, level);
	}

	@Override
	public boolean addToGoggleTooltip(List<Component> tooltip, boolean isPlayerSneaking) {
		if (level == null) return false;
		ObservePacketPayload.send(worldPosition, 0);
		// TODO Add networking
		/*
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
		CALang.builder().add(Component.literal(" " + Util.format(energyRemoved) + "⚡/t ").withStyle(ChatFormatting.AQUA)).forGoggles(tooltip);
		if (recipeCache.isPresent()) {
			ChargingRecipe recipe = recipeCache.get().value();
			CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.energy.consumption").withStyle(ChatFormatting.GRAY)).forGoggles(tooltip);
			CALang.builder().add(Component.literal(" " + Util.format(chargeAccumulator) + " / " + Util.format(recipe.getEnergy()) + "⚡").withStyle(ChatFormatting.AQUA)).forGoggles(tooltip);
		}
		*/
		if (TimeRemainingPacketPayload.clientTimeRemaining <= 20) return false;
		CALang.builder().add(Component.translatable(CreateAddition.MODID + ".tooltip.charging.info").withStyle(ChatFormatting.WHITE)).forGoggles(tooltip);
		CALang.builder().add(Component.literal(" ").append(Component.translatable(CreateAddition.MODID + ".tooltip.charging.time_remaining").withStyle(ChatFormatting.GRAY))
			.append(Component.literal(" " + Util.formatTime(TimeRemainingPacketPayload.clientTimeRemaining)).withStyle(ChatFormatting.AQUA))).forGoggles(tooltip);
		return true;
	}

	@Override
	public void onObserved(ServerPlayer player, ObservePacketPayload pkt) {
		long timeRemaining = 0;
		if(recipeCache.isPresent()) {
			ChargingRecipe recipe = recipeCache.get().value();
			long chargeRate = Util.min(CommonConfig.TESLA_COIL_RECIPE_CHARGE_RATE.get(), recipe.getEnergy() - chargeAccumulator, recipe.getMaxChargeRate());
			if (chargeRate == 0) return;
			timeRemaining = (recipe.getEnergy() - chargeAccumulator) / chargeRate;
		}
		TimeRemainingPacketPayload.send(timeRemaining, player);
	}
}

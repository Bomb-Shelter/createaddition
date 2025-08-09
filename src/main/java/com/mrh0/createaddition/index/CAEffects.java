package com.mrh0.createaddition.index;

import io.github.fabricators_of_create.porting_lib.registry.DeferredHolder;
import io.github.fabricators_of_create.porting_lib.registry.DeferredRegister;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;

import com.mrh0.createaddition.CreateAddition;
import com.mrh0.createaddition.effect.ShockingEffect;


public class CAEffects {
	public static final DeferredRegister<MobEffect> EFFECTS = DeferredRegister.create(Registries.MOB_EFFECT, CreateAddition.MODID);
	public static final DeferredHolder<MobEffect, MobEffect> SHOCKING = EFFECTS.register("shocking", () -> new ShockingEffect()
			.addAttributeModifier(Attributes.MOVEMENT_SPEED, CreateAddition.asResource("shocking"), (double)-100f, AttributeModifier.Operation.ADD_MULTIPLIED_TOTAL));
	
	public static void register() {
		EFFECTS.register();
	}
}

package com.mrh0.createaddition.recipe.conditions;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.fabricators_of_create.porting_lib.resources.conditions.ICondition;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.material.Fluid;

public record HasFluidTagCondition(TagKey<Fluid> tag) implements ICondition {
	public static final MapCodec<HasFluidTagCondition> CODEC = RecordCodecBuilder.mapCodec((builder) -> {
		return builder.group(ResourceLocation.CODEC.xmap((loc) -> {
			return TagKey.create(Registries.FLUID, loc);
		}, TagKey::location).fieldOf("fluidTag").forGetter(HasFluidTagCondition::tag)).apply(builder, HasFluidTagCondition::new);//
	});

	public HasFluidTagCondition(String location) {
		this(ResourceLocation.parse(location));
	}

	public HasFluidTagCondition(String namespace, String path) {
		this(ResourceLocation.fromNamespaceAndPath(namespace, path));
	}

	public HasFluidTagCondition(ResourceLocation tag) {
		this(TagKey.create(Registries.FLUID, tag));
	}

	public HasFluidTagCondition(TagKey<Fluid> tag) {
		this.tag = tag;
	}

	public boolean test(ICondition.IContext context) {
		return !context.getTag(this.tag).isEmpty();
	}

	public MapCodec<? extends ICondition> codec() {
		return CODEC;
	}

	public String toString() {
		return "has_fluid_tag(\"" + String.valueOf(this.tag.location()) + "\")";
	}

	public TagKey<Fluid> tag() {
		return this.tag;
	}
}

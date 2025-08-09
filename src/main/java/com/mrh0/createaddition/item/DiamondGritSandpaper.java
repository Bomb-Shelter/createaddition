package com.mrh0.createaddition.item;

import java.util.function.Consumer;

import com.mrh0.createaddition.config.CommonConfig;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItem;
import com.simibubi.create.content.equipment.sandPaper.SandPaperItemRenderer;
import com.simibubi.create.foundation.item.render.SimpleCustomRenderer;

import io.github.fabricators_of_create.porting_lib.item.DamageableItem;
import net.createmod.catnip.platform.CatnipServices;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;


public class DiamondGritSandpaper extends SandPaperItem implements DamageableItem {
	public DiamondGritSandpaper(Properties properties) {
		super(properties);

		CatnipServices.PLATFORM.executeOnClientOnly(() -> () -> {
			ClientSetup.setup(this);
		});
	}
	
	@Override
	public int getMaxDamage(ItemStack stack) {
		return CommonConfig.DIAMOND_GRIT_SANDPAPER_USES.get();
	}

	private static class ClientSetup {
		public static void setup(Item item) {
			SimpleCustomRenderer.create(item, new SandPaperItemRenderer());
		}
	}
}

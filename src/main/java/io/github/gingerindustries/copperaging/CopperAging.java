package io.github.gingerindustries.copperaging;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.WeatheringCopper;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.ProjectileImpactEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod("copperaging")
public class CopperAging {
	private static final Logger LOGGER = LogUtils.getLogger();
	public static final Set<Block> COPPER_BLOCKS = new HashSet<Block>();

	public CopperAging() {
		LOGGER.info("Hello from Copper Aging!");
		MinecraftForge.EVENT_BUS.register(this);
		COPPER_BLOCKS.add(Blocks.COPPER_BLOCK);
		COPPER_BLOCKS.add(Blocks.CUT_COPPER);
		COPPER_BLOCKS.add(Blocks.CUT_COPPER_STAIRS);
		COPPER_BLOCKS.add(Blocks.CUT_COPPER_SLAB);
		COPPER_BLOCKS.addAll(WeatheringCopper.NEXT_BY_BLOCK.get().values());
		LOGGER.debug(COPPER_BLOCKS.toString());
	}

	@SubscribeEvent
	public void onProjectileImpact(ProjectileImpactEvent event) {
		Projectile projectile = event.getProjectile();
		HitResult hit = event.getRayTraceResult();
		Level level = projectile.getLevel();
		if (level.isClientSide) {
			return;
		}
		if (projectile.getType() == EntityType.POTION) {
			if (PotionUtils.getPotion(((ThrowableItemProjectile) projectile).getItem()) == Potions.WATER) {
				if (hit.getType() == HitResult.Type.BLOCK) {
					BlockState blockState = level.getBlockState(((BlockHitResult) hit).getBlockPos());
					if (COPPER_BLOCKS.contains(blockState.getBlock())) {
						((WeatheringCopper) blockState.getBlock()).applyChangeOverTime(blockState,
								(ServerLevel) level, ((BlockHitResult) hit).getBlockPos(), level.getRandom());
					}
				}
			}
		}
	}

}

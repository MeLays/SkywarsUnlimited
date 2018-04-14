/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.game.arenas;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.melays.swunlimited.game.SoundDebugger;
import de.melays.swunlimited.map_manager.ClusterTools;
import de.melays.swunlimited.teams.error.UnknownTeamException;

public class BlockManager {
	
	Arena arena;
	
	public BlockManager (Arena arena) {
		this.arena = arena;
	}
	
	public ArrayList<Location> placed_blocks = new ArrayList<Location>();
		
	public boolean placeBlock (Location loc , ItemStack stack) {
		Location max = arena.relative.clone().add(arena.cluster.x_size , arena.cluster.y_size , arena.cluster.z_size);
		if (!ClusterTools.isInAreaIgnoreHeight(loc, arena.relative, max)) {
			return false;
		}
		if (!ClusterTools.isInAreaIgnoreHeight(loc, arena.relative.clone().add(3, 3, 3), max.clone().add(-3, -3, -3))) {
			loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.EXTINGUISH, 1);
		}
		placed_blocks.add(loc);
		return true;
	}
	
	public boolean removeBlock (Location loc , Player p) {
		return true;
	}
	
}

package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.map_manager.ClusterTools;

public class BlockManager {
	
	Arena arena;
	
	public BlockManager (Arena arena) {
		this.arena = arena;
	}
	
	ArrayList<Location> placed_blocks = new ArrayList<Location>();
	
	public boolean placeBlock (Location loc) {
		Location max = arena.relative.clone().add(arena.cluster.x_size , arena.cluster.y_size , arena.cluster.z_size);
		if (!ClusterTools.isInAreaIgnoreHeight(loc, arena.relative, max)) {
			return false;
		}
		if (!ClusterTools.isInAreaIgnoreHeight(loc, arena.relative.clone().add(3, 3, 3), max.clone().add(-3, -3, -3))) {
			loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.FLAME, 1);
		}
		if (!placed_blocks.contains(loc))
			placed_blocks.add(loc);
		return true;
	}
	
	public boolean removeBlock (Location loc , Player p) {
		if (loc.getBlock().getType() == Material.BED_BLOCK) {
			Block bed1 = loc.getBlock();
			Block bed2 = null;
			try {
				ArenaTeam team = ArenaTools.getTeamOfLocation(arena, loc);
				if (!team.team.name.equals(arena.teamManager.findPlayer(p).team.name) && team.bed) {
					if (loc.clone().add(1, 0, 0).getBlock().getType() == Material.BED_BLOCK) bed2 = loc.clone().add(1, 0, 0).getBlock();
					if (loc.clone().add(-1, 0, 0).getBlock().getType() == Material.BED_BLOCK) bed2 = loc.clone().add(-1, 0, 0).getBlock();
					if (loc.clone().add(0, 0, -1).getBlock().getType() == Material.BED_BLOCK) bed2 = loc.clone().add(0, 0, -1).getBlock();
					if (loc.clone().add(0, 0, 1).getBlock().getType() == Material.BED_BLOCK) bed2 = loc.clone().add(0, 0, 1).getBlock();
					
					ClusterTools.setBlockFast(bed1.getLocation(), Material.AIR, (byte) 0);
					if (bed2 != null) {
						ClusterTools.setBlockFast(bed2.getLocation(), Material.AIR, (byte) 0);
						bed2.getState().update(true);
					}
					arena.BedDestroyed(team, p);
					return false;
				}
			} catch (Exception e) {
				
			}
		}
		if (!placed_blocks.contains(loc)) {
			return false;
		}
		return true;
	}
	
}

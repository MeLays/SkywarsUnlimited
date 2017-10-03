package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.map_manager.ClusterTools;
import de.melays.bwunlimited.teams.error.UnknownTeamException;

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
		if (!isPlaceable(loc)) {
			return false;
		}
		if (!ClusterTools.isInAreaIgnoreHeight(loc, arena.relative.clone().add(3, 3, 3), max.clone().add(-3, -3, -3))) {
			loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.FLAME, 1);
		}
		if (!placed_blocks.contains(loc))
			placed_blocks.add(loc);
		return true;
	}
	
	@SuppressWarnings("deprecation")
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
					loc.getWorld().refreshChunk(bed1.getX() >> 4 , bed1.getY() >> 4);
					loc.getWorld().refreshChunk(bed2.getX() >> 4 , bed2.getY() >> 4);
					arena.BedDestroyed(team, p);
					arena.scoreBoard.update();
					SoundDebugger.playSound(bed1.getLocation().getWorld(), bed1.getLocation(), "ENDERDRAGON_GROWL" ,"ENTITY_ENDERDRAGON_GROWL");
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
	
	public boolean isPlaceable(Location loc) {
		for (ArenaTeam team : arena.teamManager.getTeams()) {
			try {
				Location spawn = arena.cluster.getClusterMeta().getTeamSpawn(team.team.name).toLocation(arena.relative).getBlock().getLocation();
				if (loc.equals(spawn)) return false;
				spawn = spawn.add(0, 1, 0).getBlock().getLocation();
				if (loc.equals(spawn)) return false;
			} catch (UnknownTeamException e) {

			}
		}
		return true;
	}
	
}

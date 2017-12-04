package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Effect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.map_manager.ClusterTools;
import de.melays.bwunlimited.teams.error.UnknownTeamException;

public class BlockManager {
	
	Arena arena;
	
	public BlockManager (Arena arena) {
		this.arena = arena;
	}
	
	public ArrayList<Location> placed_blocks = new ArrayList<Location>();
	HashMap<Location , ItemStack> item_stacks = new HashMap<Location , ItemStack>();
	
	HashMap<Location , Integer> webs = new HashMap<Location , Integer>();
	
	public static boolean isAtBed (Location loc) {
		Block b = loc.getBlock();
		if (b.getRelative(BlockFace.UP).getType() == Material.BED_BLOCK) return true;
		if (b.getRelative(BlockFace.DOWN).getType() == Material.BED_BLOCK) return true;
		if (b.getRelative(BlockFace.SOUTH).getType() == Material.BED_BLOCK) return true;
		if (b.getRelative(BlockFace.WEST).getType() == Material.BED_BLOCK) return true;
		if (b.getRelative(BlockFace.NORTH).getType() == Material.BED_BLOCK) return true;
		if (b.getRelative(BlockFace.EAST).getType() == Material.BED_BLOCK) return true;
		return false;
	}
	
	public boolean placeBlock (Location loc , ItemStack stack) {
		Location max = arena.relative.clone().add(arena.cluster.x_size , arena.cluster.y_size , arena.cluster.z_size);
		if (!ClusterTools.isInAreaIgnoreHeight(loc, arena.relative, max)) {
			return false;
		}
		if (!isPlaceable(loc)) {
			return false;
		}
		if (!ClusterTools.isInAreaIgnoreHeight(loc, arena.relative.clone().add(3, 3, 3), max.clone().add(-3, -3, -3))) {
			loc.getWorld().playEffect(loc.clone().add(0, 1, 0), Effect.EXTINGUISH, 1);
		}
		if (!placed_blocks.contains(loc))
			placed_blocks.add(loc);
		if (stack.getType() == Material.WEB) {
			if ((arena.settings.cobweb_decay && !isAtBed(loc)) || (arena.settings.cobweb_decay_bed && isAtBed(loc))) {
				@SuppressWarnings("deprecation")
				int id = Bukkit.getScheduler().runTaskLater(arena.main, new BukkitRunnable() {

					@Override
					public void run() {
						if (webs.get(loc) == this.getTaskId()) {
							loc.getBlock().setType(Material.AIR);
							webs.remove(loc);
						}
					}
					
				}, arena.settings.cobweb_decay_ticks).getTaskId();
				webs.put(loc, id);
			}
		}
		item_stacks.put(loc, stack);
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
					arena.main.getStatsManager().addBed(arena, p);
					arena.main.getStatsManager().addPoints(arena, p, arena.main.getConfig().getInt("points.bed"));
					arena.scoreBoard.update();
					SoundDebugger.playSound(p , "LEVEL_UP" ,"ENTITY_PLAYER_LEVELUP");
					return false;
				}
			} catch (Exception e) {
				
			}
		}
		else if (loc.getBlock().getType() == Material.ENDER_CHEST) {
			for (ArenaTeam team : arena.teamManager.teams) {
				if (team.chests.contains(loc.getBlock().getLocation())) {
					team.sendMessage(arena.main.getMessageFetcher().getMessage("game.teamchest_destroyed", true));
				}
				team.chests.remove(loc.getBlock().getLocation());
			}
		}
		if (!placed_blocks.contains(loc)) {
			return false;
		}
		else if (loc.getBlock().getType() == Material.WEB) {
			if (webs.containsKey(loc)) webs.remove(loc);
		}
		return true;
	}
	
	public ItemStack getDrop(Location loc) {
		ItemStack r = this.item_stacks.get(loc);
		r.setAmount(1);
		for (String s : arena.main.getConfig().getStringList("game.no_drop")) {
			if (s.toUpperCase().equals(r.getType().toString())) {
				return null;
			}
		}
		return r;
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

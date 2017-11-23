package de.melays.bwunlimited.game.items;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.teams.error.UnknownTeamException;

public class Teleporter {

	Arena arena;
	
	public Teleporter (Arena arena) {
		this.arena = arena;
	}
	
	ArrayList<Player> teleporting = new ArrayList<Player>();
	HashMap<Player , Location> last_location = new HashMap<Player , Location>();
	HashMap<UUID , Boolean> running = new HashMap<UUID , Boolean>();
	
	public static void removeInventoryItems(PlayerInventory inv, Material type, int amount) {
	    for (ItemStack is : inv.getContents()) {
	        if (is != null && is.getType() == type) {
	            int newamount = is.getAmount() - amount;
	            if (newamount > 0) {
	                is.setAmount(newamount);
	                break;
	            } else {
	                inv.remove(is);
	                amount = -newamount;
	                if (amount == 0) break;
	            }
	        }
	    }
	}
	
	public int getAmount(Inventory inv , Material m){
		int count = 0;  
		for (ItemStack s : inv.getContents()){
			if (s != null){
				if (s.getType() == m){
					count += s.getAmount();
				}
			}
		}  
		return count;
	}
	
	@SuppressWarnings("deprecation")
	public boolean use (Player p , Material m) {
		if (teleporting.contains(p)) {
			return false;
		}
		teleporting.add(p);
		p.sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.3")), arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.3_sub")));
		last_location.put(p, p.getLocation());
		SoundDebugger.playSound(p, arena.main.getSettingsManager().getFile().getString("game.titles.teleport.sound.legacy"), arena.main.getSettingsManager().getFile().getString("game.titles.teleport.sound.new"));
		
		UUID id = UUID.randomUUID();
		running.put(id , true);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!arena.getAll().contains(p) || !p.isOnline() || arena.state != ArenaState.INGAME || !running.get(id)) return;
				if (p.getLocation().getX() != last_location.get(p).getX() || p.getLocation().getY() != last_location.get(p).getY()) {
					p.sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.move")), arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.move_sub")));					
					running.put(id, false);
					return;
				}
				p.sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.2")), arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.2_sub")));
				last_location.put(p, p.getLocation());
				SoundDebugger.playSound(p, arena.main.getSettingsManager().getFile().getString("game.titles.teleport.sound.legacy"), arena.main.getSettingsManager().getFile().getString("game.titles.teleport.sound.new"));
			}
			
		}.runTaskLater(arena.main, 40);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!arena.getAll().contains(p) || !p.isOnline() || arena.state != ArenaState.INGAME || !running.get(id)) return;
				if (p.getLocation().getX() != last_location.get(p).getX() || p.getLocation().getY() != last_location.get(p).getY()) {
					p.sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.move")), arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.move_sub")));					
					running.put(id, false);
					return;
				}
				p.sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.1")), arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.1_sub")));
				last_location.put(p, p.getLocation());
				SoundDebugger.playSound(p, arena.main.getSettingsManager().getFile().getString("game.titles.teleport.sound.legacy"), arena.main.getSettingsManager().getFile().getString("game.titles.teleport.sound.new"));
			}
			
		}.runTaskLater(arena.main, 60);
		
		new BukkitRunnable() {

			@Override
			public void run() {
				if (!arena.getAll().contains(p) || !p.isOnline() || arena.state != ArenaState.INGAME || !running.get(id)) return;
				if (p.getLocation().getX() != last_location.get(p).getX() || p.getLocation().getY() != last_location.get(p).getY()) {
					p.sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.move")), arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.move_sub")));					
					running.put(id, false);
					return;
				}
				p.sendTitle(arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.go")), arena.main.c(arena.main.getSettingsManager().getFile().getString("game.titles.teleport.go_sub")));
				last_location.put(p, p.getLocation());
				SoundDebugger.playSound(p, arena.main.getSettingsManager().getFile().getString("game.titles.teleport.sound_teleport.legacy"), arena.main.getSettingsManager().getFile().getString("game.titles.teleport.sound_teleport.new"));
				teleporting.remove(p);
				if (!(getAmount(p.getInventory() , m) >= 1)) {
					return;
				}
				removeInventoryItems(p.getInventory() , m , 1);
				try {
					p.teleport(arena.cluster.getClusterMeta().getTeamSpawn(arena.teamManager.findPlayer(p).team.name).toLocation(arena.relative));
				} catch (UnknownTeamException e) {
					e.printStackTrace();
				}
			}
			
		}.runTaskLater(arena.main, 80);
		
		return true;
	}
	
}

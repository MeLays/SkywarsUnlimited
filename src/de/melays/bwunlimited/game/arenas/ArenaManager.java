package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.game.arenas.settings.TeamPackage;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;

public class ArenaManager {
	
	Main main;
	
	public ArenaManager(Main main) {
		this.main = main;
	}
	
	HashMap<Integer , Arena> running = new HashMap<Integer , Arena>();
	
	int last_id = 0;
	int x = 0;
	
	public Location getFreeLocation () {		
		return new Location(Bukkit.getWorld(main.gameworld) , x , main.getConfig().getInt("gameplacement.y-position") , main.getConfig().getInt("gameplacement.z-position"));
	}
	
	public int getNewID() {
		last_id = last_id + 1;
		return last_id +1;
	}
	
	public int startGame(Cluster cluster , Settings settings , TeamPackage teampackage) throws ClusterAvailabilityException, UnknownClusterException {
		return startGame(cluster , settings , teampackage , null);
	}
	
	public int startGame(Cluster cluster , Settings settings , ArrayList<Player> players) throws ClusterAvailabilityException, UnknownClusterException {
		return startGame(cluster , settings , null , players);
	}
	
	private int startGame(Cluster cluster , Settings settings , TeamPackage teampackage , ArrayList<Player> players) throws ClusterAvailabilityException, UnknownClusterException {
		Location relative = getFreeLocation();
		int id = getNewID();
		Arena arena = new Arena(main, cluster , relative , settings , id);
		if (players != null) arena.addPlayers(players);
		if (teampackage != null) arena.addTeamPackage(teampackage);
		running.put(id, arena);
		x = x + arena.cluster.x_size + main.getConfig().getInt("gameplacement.gap");
		return id;
	}
	
	public void cancleAll() {
		for (Arena a : running.values()) {
			a.removeAll();
		}
	}
	
	public void updateAll() {
		for (Arena a : running.values()) {
			a.updateTab();
		}
	}
	
	void checkOut (int id) {
		running.remove(id);
	}
	
	//Player methonds
	
	public boolean isInGame(Player p) {
		for (Arena a : running.values()) {
			if (a.getAll().contains(p)) {
				return true;
			}
		}
		return false;
	}
	
	public Arena searchPlayer(Player p) {
		for (Arena a : running.values()) {
			if (a.getAll().contains(p)) {
				return a;
			}
		}
		return null;
	}
}

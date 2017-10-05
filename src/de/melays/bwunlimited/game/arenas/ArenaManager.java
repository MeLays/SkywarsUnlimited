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
	HashMap<Integer , String> category = new HashMap<Integer , String>();
	
	int last_id = 0;
	int x = 0;
	
	public Location getFreeLocation () {		
		return new Location(Bukkit.getWorld(main.gameworld) , x , main.getConfig().getInt("gameplacement.y-position") , main.getConfig().getInt("gameplacement.z-position"));
	}
	
	public int getNewID() {
		last_id = last_id + 1;
		return last_id +1;
	}
	
	public ArrayList<Arena> getArenas (String category){
		ArrayList<Arena> r= new ArrayList<Arena>();
		for (int i : this.category.keySet()) {
			Bukkit.broadcastMessage(this.category.get(i) + " " + category);
			if (this.category.get(i).equals(category)) {
				r.add(this.getArena(i));
			}
		}
		Bukkit.broadcastMessage(r.size() + "");
		return r;
	}
	
	public int startGame(Cluster cluster , Settings settings , String category) throws ClusterAvailabilityException, UnknownClusterException {
		return startGame(cluster , settings , category , null , null);
	}
	
	public int startGame(Cluster cluster , Settings settings , String category , TeamPackage teampackage) throws ClusterAvailabilityException, UnknownClusterException {
		return startGame(cluster , settings , category, teampackage , null);
	}
	
	public int startGame(Cluster cluster , Settings settings , String category , ArrayList<Player> players) throws ClusterAvailabilityException, UnknownClusterException {
		return startGame(cluster , settings , category , null , players);
	}
	
	private int startGame(Cluster cluster , Settings settings , String category, TeamPackage teampackage , ArrayList<Player> players) throws ClusterAvailabilityException, UnknownClusterException {
		Location relative = getFreeLocation();
		int id = getNewID();
		Arena arena = new Arena(main, cluster , relative , settings , id);
		if (players != null) arena.addPlayers(players);
		if (teampackage != null) arena.addTeamPackage(teampackage);
		this.category.put(id, category);
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

	public Arena getArena(int id) {
		return this.running.get(id);
	}
}

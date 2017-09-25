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
	
	public Location getFreeLocation () {
		
		int x = 0;
		
		if (running.size() == 0) {
			x = 0;
		}
		else {
			int biggest = 0;
			for (int i : running.keySet()) {
				if (i > biggest)
					biggest = i;
			}
			x = running.get(biggest).relative.getBlockX() +  running.get(biggest).cluster.x_size + main.getConfig().getInt("gameplacement.gap");
		}
		
		return new Location(Bukkit.getWorld(main.gameworld) , x , main.getConfig().getInt("gameplacement.y-position") , main.getConfig().getInt("gameplacement.z-position"));
	}
	
	public int getNewID() {
		int biggest = 0;
		for (int i : running.keySet()) {
			if (i > biggest)
				biggest = i;
		}
		return biggest + 1;
	}
	
	public void startGame(Cluster cluster , Settings settings , TeamPackage teampackage) throws ClusterAvailabilityException, UnknownClusterException {
		startGame(cluster , settings , teampackage , null);
	}
	
	public void startGame(Cluster cluster , Settings settings , ArrayList<Player> players) throws ClusterAvailabilityException, UnknownClusterException {
		startGame(cluster , settings , null , players);
	}
	
	private void startGame(Cluster cluster , Settings settings , TeamPackage teampackage , ArrayList<Player> players) throws ClusterAvailabilityException, UnknownClusterException {
		Location relative = getFreeLocation();
		Arena arena = new Arena(main, cluster , relative , settings);
		running.put(getNewID(), arena);
	}
}

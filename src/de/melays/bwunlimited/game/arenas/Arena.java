package de.melays.bwunlimited.game.arenas;

import org.bukkit.Location;

import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.map_manager.Cluster;

public class Arena {
	
	public Cluster cluster;
	public Location relative;
	
	public Settings settings;
	
	public ArenaState state = ArenaState.LOBBY;
	
	public Arena (Cluster cluster , Location relative , Settings settings) {
		this.cluster = cluster;
		this.relative = relative;
	}
	
	
	
}

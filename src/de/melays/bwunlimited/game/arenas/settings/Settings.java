package de.melays.bwunlimited.game.arenas.settings;

import de.melays.bwunlimited.game.arenas.Arena;

public class Settings {
	
	boolean fixed_teams = false;
	
	int min_players = 2;
	
	Arena arena;
	
	public Settings (Arena arena) {
		this.arena = arena;
		this.min_players = arena.cluster.teams.size();
	}
	
	
	
}

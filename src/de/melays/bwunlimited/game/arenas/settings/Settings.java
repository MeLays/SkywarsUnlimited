package de.melays.bwunlimited.game.arenas.settings;

import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.teams.Team;

public class Settings {
	
	public boolean fixed_teams = false;
	public boolean allow_join = true;
	public boolean allow_spectate = true;
	
	public LeaveType lobby_leave = LeaveType.NORMAL;
	
	public int min_players = 2;
	public int max_players = 0;
	
	public int min_lobby_countdown = 10;
	public int game_end = 36000;
	public int ending_countdown = 10;
	
	Arena arena;
	
	public void setArena(Arena arena) {
		this.arena = arena;
		this.min_players = arena.cluster.teams.size();
		this.max_players = 0;
		for (Team t : arena.cluster.teams) {
			this.max_players += t.max;
		}
	}
	
	public Arena getArena () {
		return arena;
	}
	
	public int getMinPlayers () {
		return min_players;
	}
	
}

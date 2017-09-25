package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.melays.bwunlimited.teams.Team;

public class ArenaTeam {
	
	public Team team;
	public boolean bed = true;
	
	ArrayList<Player> players = new ArrayList<Player>();
	
	public ArenaTeam (Team team) {
		this.team = team;
	}
	
	public boolean addPlayer(Player p) {
		if (players.size() >= team.max) {
			return false;
		}
		players.add(p);
		return true;
	}
	
	public boolean hasPlayer(Player p) {
		return players.contains(p);
	}
}

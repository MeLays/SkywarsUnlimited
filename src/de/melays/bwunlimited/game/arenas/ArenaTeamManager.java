package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.melays.bwunlimited.teams.Team;

public class ArenaTeamManager {
	
	public Arena arena;
	
	public ArenaTeamManager(Arena arena) {
		this.arena = arena;
	}
	
	ArrayList<ArenaTeam> teams = new ArrayList<ArenaTeam>();
	
	public void load() {
		for (Team team : arena.cluster.getClusterMeta().getTeams()) {
			teams.add(new ArenaTeam(team));
		}
	}
	
	public ArenaTeam findPlayer (Player p) {
		for (ArenaTeam team : teams) {
			if (team.hasPlayer(p)) return team;
		}
		return null;
	}
	
	public ArenaTeam getTeam (String name) {
		for (ArenaTeam t : teams) {
			if (t.team.name.equals(name)) {
				return t;
			}
		}
		return null;
	}
	
	public ArrayList<ArenaTeam> getTeams() {
		return teams;
	}
	
}

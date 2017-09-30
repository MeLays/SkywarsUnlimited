package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.teams.Team;

public class ArenaTeamManager {
	
	public Arena arena;
	
	public ArenaTeamManager(Arena arena) {
		this.arena = arena;
		load();
	}
	
	ArrayList<ArenaTeam> teams = new ArrayList<ArenaTeam>();
	
	public void load() {
		for (Team team : arena.cluster.getClusterMeta().getTeams()) {
			teams.add(new ArenaTeam(arena , team));
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
	
	public boolean setTeam (Player p , String team) {
		if (getTeam(team) == null) {
			return false;
		}
		ArenaTeam old = findPlayer(p);
		if (old == null) {
			arena.updateColors();
			return getTeam(team).addPlayer(p);
		}
		if (getTeam(team).addPlayer(p)) {
			old.removePlayer(p);
			arena.updateColors();
			return true;
		}
		return false;
	}
	
	public void setTeamSound(Player p , String team) {
		if (setTeam (p , team)) {
			SoundDebugger.playSound(p, "SLIME_WALK", "ENTITY_SLIME_SQUISH");
		}
		else {
			SoundDebugger.playSound(p, "CLICK", "UI_BUTTON_CLICK");
		}
	}
	
}

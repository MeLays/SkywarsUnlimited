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
			if (getTeam(team).addPlayer(p)) {
				arena.updateColors();	
				return true;
			}
			return false;
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
	
	
	public void checkWin() {
		int alive = 0;
		ArenaTeam last = null;
		for (ArenaTeam team : getTeams()) {
			team.checkTeam();
			if (team.players.size() >= 1) {
				alive += 1;
				last = team;
			}
		}
		if (alive == 1) {
			arena.endGame(last);
		}
		else if (alive == 0) {
			arena.endGame();
		}
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.melays.swunlimited.game.SoundDebugger;
import de.melays.swunlimited.teams.Team;

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
	
	public int getMaxPlayersPerTeam() {
		return (int) Math.ceil((double)arena.getAllPlayers().size() / (double)teams.size());
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
		if (old.team.name.equals(team)) {
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

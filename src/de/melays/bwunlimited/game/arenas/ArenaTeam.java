package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.entity.Player;

import de.melays.bwunlimited.teams.Team;

public class ArenaTeam {
	
	public Team team;
	public boolean bed = true;
	
	ArrayList<Player> players = new ArrayList<Player>();
	
	Arena arena;
	
	public ArenaTeam (Arena arena , Team team) {
		this.team = team;
		this.arena = arena;
	}
	
	public boolean addPlayer(Player p) {
		if (players.size() >= team.max) {
			return false;
		}
		players.add(p);
		p.sendMessage(arena.main.getMessageFetcher().getMessage("team.enter", true).replaceAll("%color%", team.Color.toChatColor().toString()).replaceAll("%team%", team.display));
		return true;
	}
	
	public void removePlayer(Player p) {
		players.remove(p);
	}
	
	public boolean hasPlayer(Player p) {
		return players.contains(p);
	}
}

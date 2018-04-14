/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.melays.swunlimited.teams.Team;

public class ArenaTeam {
	
	public Team team;
	public boolean bed = false;
	public boolean out_of_game = false;
	
	ArrayList<Player> players = new ArrayList<Player>();
	
	Arena arena;
	
	public ArenaTeam (Arena arena , Team team) {
		this.team = team;
		this.arena = arena;
	}
	
	public boolean addPlayer(Player p) {
		if (players.size() >= team.max || players.size() >= arena.teamManager.getMaxPlayersPerTeam()) {
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
	
	public void checkTeam() {
		if (players.size() == 0 && !out_of_game) {
			bed = false;
			out_of_game = true;
			arena.scoreBoard.update();
			arena.sendMessage(arena.main.getMessageFetcher().getMessage("game.team_out", true).replaceAll("%color%", team.Color.toChatColor().toString()).replaceAll("%display%", team.display));
		}
	}
	
	//Message management
	public void sendMessage (String message) {
		for (Player p : players) {
			p.sendMessage(message);
		}
	}
	
	public void sendColoredMessage (String message) {
		for (Player p : players) {
			p.sendMessage(arena.main.c(message));
		}
	}
	
	@SuppressWarnings("deprecation")
	public void sendTitle(String str , String str2) {
		for (Player p : players) {
			p.sendTitle(arena.main.c(str), arena.main.c(str2));
		}
	}
}

package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.melays.bwunlimited.teams.Team;

public class ArenaTeam {
	
	public Team team;
	public boolean bed = true;
	public boolean out_of_game = false;
	
	public Inventory teamChest;
	public ArrayList<Location> chests = new ArrayList<Location>();
	
	ArrayList<Player> players = new ArrayList<Player>();
	
	Arena arena;
	
	public ArenaTeam (Arena arena , Team team) {
		this.team = team;
		this.arena = arena;
		teamChest = Bukkit.createInventory(null, arena.main.getSettingsManager().getFile().getInt("game.teamchest.size"), 
				arena.main.c(arena.main.getSettingsManager().getFile().getString("game.teamchest.title")
						.replaceAll("%teamcolor%", team.Color.toChatColor() + "")
						.replaceAll("%teamname%", team.display)));
	}
	
	public void openTeamInventory(Player p) {
		p.openInventory(this.teamChest);
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

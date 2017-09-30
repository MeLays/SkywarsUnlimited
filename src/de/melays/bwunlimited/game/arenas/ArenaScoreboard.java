package de.melays.bwunlimited.game.arenas;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import de.melays.bwunlimited.scoreboard.ScoreBoardTools;

public class ArenaScoreboard {
	
	Arena arena;
	
	public ArenaScoreboard (Arena arena) {
		this.arena = arena;
	}
	
	HashMap<Player , ScoreBoardTools> scoreboards = new HashMap<Player , ScoreBoardTools>();
	
	public void create () {
		for (Player p : arena.getAll()) {
			ScoreBoardTools tools = new ScoreBoardTools(p , arena.main.c(arena.main.getSettingsManager().getFile().getString("game.scoreboard.title")));
			List<String> lines = arena.main.getSettingsManager().getFile().getStringList("game.scoreboard.content");
			int value = lines.size() + arena.teamManager.getTeams().size()+1;
			for (String s : lines) {
				if (s.equals("team-lines")) {
					for (ArenaTeam team : arena.teamManager.getTeams()) {
						
						String symbol = arena.main.c(arena.main.getSettingsManager().getFile().getString("game.scoreboard.symbol"));
						if (team.bed) {
							symbol = arena.main.c(arena.main.getSettingsManager().getFile().getString("game.scoreboard.color_alive")) + symbol;
						}
						else {
							symbol = arena.main.c(arena.main.getSettingsManager().getFile().getString("game.scoreboard.color_dead")) + symbol;
						}
						
						tools.addLine(team.team.name
								, symbol
								, team.team.Color.toChatColor() + team.team.display, team.team.Color.toChatColor()+""
								, value);
						value -= 1;
						
					}
				}
				else if (s.equals("time-line")) {

				}
				else {
					
					tools.addNormalLine(s , value);
				}
				value -= 1;
			}
			tools.set();
			scoreboards.put(p, tools);
		}
	}
	
	public void update () {
		for (Player p : arena.getAll()) {
			for (ArenaTeam team : arena.teamManager.getTeams()) {
				
				String symbol = arena.main.c(arena.main.getSettingsManager().getFile().getString("game.scoreboard.symbol"));
				if (team.bed) {
					symbol = arena.main.c(arena.main.getSettingsManager().getFile().getString("game.scoreboard.color_alive")) + symbol;
				}
				else {
					symbol = arena.main.c(arena.main.getSettingsManager().getFile().getString("game.scoreboard.color_dead")) + symbol;
				}
				
				scoreboards.get(p).editPrefix(team.team.name, symbol);
				
			}
		}
	}
	
}
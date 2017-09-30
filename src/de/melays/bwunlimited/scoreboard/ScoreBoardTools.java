package de.melays.bwunlimited.scoreboard;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class ScoreBoardTools {
	
	Player p;
	
	public ScoreBoardTools (Player p , String title) {
		this.p = p;
		initialize(title);
	}
	
	Scoreboard board;
	Objective obj;
	
	public void initialize(String title) {
        board = Bukkit.getScoreboardManager().getNewScoreboard();
        obj = board.registerNewObjective(title, "dummy");
        obj.setDisplaySlot(DisplaySlot.SIDEBAR);
        obj.setDisplayName(title);
	}
	
	public void addLine(String name , String prefix , String middle , String suffix , int value) {
		Team line = board.registerNewTeam(name);
		line.addEntry(middle);
		line.setPrefix(prefix);
		line.setSuffix(suffix);
		obj.getScore(middle).setScore(value);
	}
	
	public void editPrefix (String team , String prefix) {
		board.getTeam(team).setPrefix(prefix);
	}
	
	public void editSuffix (String team , String suffix) {
		board.getTeam(team).setSuffix(suffix);
	}
	
	public void set () {
		p.setScoreboard(board);
	}
	
	public void addNormalLine(String line , int value) {
		Score onlineName = obj.getScore(ChatColor.translateAlternateColorCodes('&', line));
        onlineName.setScore(value);
	}
	
}

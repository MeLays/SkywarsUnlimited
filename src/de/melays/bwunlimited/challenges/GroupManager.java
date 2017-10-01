package de.melays.bwunlimited.challenges;

import java.util.HashMap;

import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;

public class GroupManager {
	
	Main main;
	
	public GroupManager(Main main) {
		this.main = main;
	}
	
	HashMap<Player, Group> players = new HashMap<Player, Group>();
	
	public Group getGroup (Player p) {
		if (players.containsKey(p))
			return players.get(p);
		for (Group g : players.values()) {
			if (g.hasPlayer(p)) {
				return g;
			}
		}
		players.put(p, new Group(this , p));
		return players.get(p);
	}
	
}

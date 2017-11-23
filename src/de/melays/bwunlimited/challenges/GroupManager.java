package de.melays.bwunlimited.challenges;

import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.log.Logger;
import net.md_5.bungee.api.ChatColor;

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
	
	HashMap<String , Integer> permissions = new HashMap<String , Integer>();
	
	public void loadPermissions() {
		permissions = new HashMap<String , Integer>();
		List<String> perms = main.getConfig().getStringList("group_size_permission");
		if (perms == null) return;
		for (String s : perms) {
			try {
				String permission = s.split("->")[0];
				String amount = s.split("->")[1];
				if (amount.equals("*")) {
					permissions.put(permission, -1);
				}
				else {
					permissions.put(permission, Integer.parseInt(amount));
				}
			}catch (Exception ex) {
				Logger.log(main.console_prefix + ChatColor.RED + "Cannot parse group_size_permission '"+s+"'");
			}
		}
	}
	
	public int getMaxPlayers (Player p) {
		int max = main.getConfig().getInt("default_group_size");
		for (String s : permissions.keySet()) {
			if (p.hasPermission(s) && permissions.get(s) > max) {
				max = permissions.get(s);
			}
		}
		return max;
	}
	
}

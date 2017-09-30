package de.melays.bwunlimited.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;

public class PlayerTools {
	
	public static void clearInventory(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}
	
	public static void resetPlayer(Player p) {
		p.closeInventory();
		clearInventory(p);
		p.setFallDistance(0);
		p.setMaxHealth(20);
		p.setHealth(p.getMaxHealth());
		p.setFoodLevel(40);
		p.setLevel(0);
		p.setExp(0);
	}
	
	public static void setLevel (ArrayList<Player> players , int lvl) {
		for (Player p : players) {
			p.setExp(0);
			p.setLevel(lvl);
		}
	}
	
}

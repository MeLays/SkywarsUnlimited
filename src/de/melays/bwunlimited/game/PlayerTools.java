package de.melays.bwunlimited.game;

import org.bukkit.entity.Player;

public class PlayerTools {
	
	public static void clearInventory(Player p) {
		p.getInventory().clear();
		p.getInventory().setArmorContents(null);
	}
	
}

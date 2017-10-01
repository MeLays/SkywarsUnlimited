package de.melays.bwunlimited.game;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.util.Vector;

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
		p.setVelocity(new Vector());
	    for (PotionEffect effect : p.getActivePotionEffects())
	        p.removePotionEffect(effect.getType());
	}
	
	public static void setLevel (ArrayList<Player> players , int lvl) {
		for (Player p : players) {
			p.setExp(0);
			p.setLevel(lvl);
		}
	}
	
}

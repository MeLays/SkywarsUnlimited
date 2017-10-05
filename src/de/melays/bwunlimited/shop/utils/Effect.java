package de.melays.bwunlimited.shop.utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.shop.BedwarsShop;
import net.md_5.bungee.api.ChatColor;

/**
 * Stores effects that will be played when player interacts with shop.
 * Effect contains message, sound, volume and pitch.
 * 
 * @author Coordu
 * @version 1.0 | 5. October 2017
 */
public enum Effect {
	BUY_ITEM("BuyItem"),
	CLICK_CATEGORY("ClickCategory"),
	CLICK_PLACEHOLDER_ITEM("ClickPlaceholderItem"),
	FULL_INVENTORY("FullInventory"),
	SWITCH_CATEGORY("SwitchCategory"),
	SWITCH_SHOP("SwitchShop"),
	TOO_LESS_MONEY("TooLessMoney");
	
	private final String message;
	private final Sound sound;
	private final float volume;
	private final float pitch;
	/**
	 * Creates a new effect from config file.
	 * @param name	The name to find effect in config.
	 */
	Effect(String name) {
		try {
			String msg = BedwarsShop.getConfig().getString("Effects."+name+".Message");
			if (msg == null) {
				message = null;
			} else {
				message = color(msg);
			}
			String soundEntry = BedwarsShop.getConfig().getString("Effects."+name+".Sound");
			if (soundEntry == null || soundEntry == "" || !soundEntry.contains(",")) {
				sound = null;
				volume = 0;
				pitch = 0;
			} else {
				String[] splitted = soundEntry.replaceAll("[;]" , ",").replaceAll(", " , ",").split(",");
				if (splitted.length != 4) {
					throw new IllegalArgumentException("[BedwarsShop] The entry 'Sound' at effect '"+name+"' is not a valid entry!");
				} else {
					sound = SoundDebugger.getSound(splitted[0].trim().toUpperCase(), splitted[1].trim().toUpperCase());
					volume = Float.parseFloat(splitted[2]);
					pitch = Float.parseFloat(splitted[3]);
				}
			}
		} catch (Throwable e) {
			throw e;
		}
	}
	/**
	 * Colores a string an adds the prefix.
	 * @param msg The string,
	 */
	private static String color(String msg) {
		msg = msg.replaceAll("%prefix%", BedwarsShop.getConfig().getString("Prefix"));
		return ChatColor.translateAlternateColorCodes('&', msg);
	}
	/**
	 * Plays the effect to a player.
	 * @param player	The player.
	 */
	public void play(Player player) {
		if (message != null && !message.equals("")) {
			player.sendMessage(message);
		}
		if (sound != null) {
			player.playSound(player.getLocation(), sound, volume, pitch);
		}
	}
}

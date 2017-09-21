package de.melays.bwunlimited.log;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;


public class Logger {
	
	public static void log (String msg) {
		Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', msg));
	}
	
}

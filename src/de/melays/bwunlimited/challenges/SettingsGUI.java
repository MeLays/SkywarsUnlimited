/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.challenges;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.settings.Settings;

public class SettingsGUI {
	
	public Settings settings;
	Player p;
	
	Main main;
	
	public SettingsGUI (Main main ,Player p){
		this.p = p;
		this.main = main;
		this.settings = new Settings(main);
	}
	
	public SettingsGUI (Main main , Player p , Settings s){
		this.p = p;
		this.main = main;
		this.settings = s;
	}
	
	public void openGUI() {
		Inventory inv = Bukkit.createInventory(null, 18, main.c(main.getSettingsManager().getFile().getString("lobby.inventory.settings.title")));
		
		//SPECTATE
		inv.setItem(1, main.getItemManager().getItem("lobby.settings.spectate"));
		inv.setItem(10, main.getItemManager().getItem("lobby.settings.deactivated"));
		if (settings.allow_spectate) inv.setItem(10, main.getItemManager().getItem("lobby.settings.activated"));
		
		//COBWEB DECAY
		inv.setItem(3, main.getItemManager().getItem("lobby.settings.cobweb"));
		inv.setItem(12, main.getItemManager().getItem("lobby.settings.deactivated"));
		if (settings.cobweb_decay) inv.setItem(12, main.getItemManager().getItem("lobby.settings.activated"));
		
		//COBWEB DECAY BED
		inv.setItem(5, main.getItemManager().getItem("lobby.settings.cobweb_bed"));
		inv.setItem(14, main.getItemManager().getItem("lobby.settings.deactivated"));
		if (settings.cobweb_decay_bed) inv.setItem(14, main.getItemManager().getItem("lobby.settings.activated"));
		
		//STATS
		inv.setItem(7, main.getItemManager().getItem("lobby.settings.stats"));
		inv.setItem(16, main.getItemManager().getItem("lobby.settings.deactivated"));
		if (settings.stats) inv.setItem(16, main.getItemManager().getItem("lobby.settings.activated"));
		
		p.openInventory(inv);
	}
	
	public void sendAsString(Player p) {
		String on = main.getMessageFetcher().getMessage("settings.activated", true);
		String off = main.getMessageFetcher().getMessage("settings.deactivated", true);
		
		String spectate = off;
		if (settings.allow_spectate) spectate = on;
		
		String cobweb = off;
		if (settings.cobweb_decay) cobweb = on;
		
		String cobweb_bed = off;
		if (settings.cobweb_decay_bed) cobweb_bed = on;
		
		String stats = off;
		if (settings.stats) stats = on;
		
		for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("settings.list")) {
			s = s.replaceAll("%spectate%", spectate);
			s = s.replaceAll("%cobweb%", cobweb);
			s = s.replaceAll("%cobweb_bed%", cobweb_bed);
			s = s.replaceAll("%player%", this.p.getName());
			s = s.replaceAll("%prefix%", main.getMessageFetcher().getMessage("prefix", false));
			p.sendMessage(main.c(s.replaceAll("%stats%", stats)));
		}
	}
	
	public void onClick (int slot) {
		if (slot == 10) settings.allow_spectate = !settings.allow_spectate;
		if (slot == 12) settings.cobweb_decay = !settings.cobweb_decay;
		if (slot == 14) settings.cobweb_decay_bed = !settings.cobweb_decay_bed;
		if (slot == 16) settings.stats = !settings.stats;
		openGUI();
	}
	
}

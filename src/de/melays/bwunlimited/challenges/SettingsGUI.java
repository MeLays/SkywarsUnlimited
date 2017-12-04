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
	
	public SettingsGUI (Player p){
		this.p = p;
	}
	
	public SettingsGUI (Player p , Settings s){
		this.p = p;
		this.settings = s;
	}
	
	public void openGUI() {
		Inventory inv = Bukkit.createInventory(null, 18, main.c(main.getSettingsManager().getFile().getString("lobby.inventory.settings.title")));
		
		//SPECTATE
		inv.setItem(1, main.getItemManager().getItem("lobby.settings.spectate"));
		inv.setItem(10, main.getItemManager().getItem("lobby.settings.off"));
		if (settings.allow_spectate) inv.setItem(10, main.getItemManager().getItem("lobby.settings.on"));
		
		//COBWEB DECAY
		inv.setItem(3, main.getItemManager().getItem("lobby.settings.cobweb"));
		inv.setItem(12, main.getItemManager().getItem("lobby.settings.off"));
		if (settings.cobweb_decay) inv.setItem(10, main.getItemManager().getItem("lobby.settings.on"));
		
		//COBWEB DECAY BED
		inv.setItem(5, main.getItemManager().getItem("lobby.settings.cobweb_bed"));
		inv.setItem(14, main.getItemManager().getItem("lobby.settings.off"));
		if (settings.cobweb_decay_bed) inv.setItem(10, main.getItemManager().getItem("lobby.settings.on"));
		
		//STATS
		inv.setItem(7, main.getItemManager().getItem("lobby.settings.stats"));
		inv.setItem(16, main.getItemManager().getItem("lobby.settings.off"));
		if (settings.stats) inv.setItem(10, main.getItemManager().getItem("lobby.settings.on"));
		
		p.openInventory(inv);
	}
	
	public void sendAsString(Player p) {
		String on = main.getMessageFetcher().getMessage("settings.on", true);
		String off = main.getMessageFetcher().getMessage("settings.off", true);
		
		String spectate = off;
		if (settings.allow_spectate) spectate = on;
		
		String cobweb = off;
		if (settings.cobweb_decay) cobweb = on;
		
		String cobweb_bed = off;
		if (settings.cobweb_decay_bed) cobweb_bed = on;
		
		String stats = off;
		if (settings.stats) stats = on;
		
		for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("settings.list")) {
			p.sendMessage(main.c(s.replaceAll("%spectate%", spectate)));
			p.sendMessage(main.c(s.replaceAll("%cobweb%", cobweb)));
			p.sendMessage(main.c(s.replaceAll("%cobweb_bed%", cobweb_bed)));
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

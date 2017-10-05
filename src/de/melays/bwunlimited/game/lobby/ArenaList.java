package de.melays.bwunlimited.game.lobby;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;

public class ArenaList {
	
	Player p;
	Main main;
	
	public ArenaList (Main main , Player p) {
		this.main = main;
		this.p = p;
	}
	
	public void openArenaList(String category , int page) {
		ArrayList<Arena> arenas = main.getArenaManager().getArenas(category);
		int pages = (int) Math.ceil((double)arenas.size() / 54.0);
		if (pages == 0) pages = 1;
		if (page > pages) page = pages;
		String title = main.getSettingsManager().getFile().getString("lobby.inventory.arenalist.title");
		title = main.c(title);
		title = title.replaceAll("%page%", page+"");
		title = title.replaceAll("%max%", pages + "");
		Inventory inv = Bukkit.createInventory(null, 54, title);
		
		inv.setItem(0, main.getItemManager().getItem("lobby.inventory.spacer"));
		if (page != 1) {
			inv.setItem(0, main.getItemManager().getItem("lobby.inventory.back"));
		}
		
		inv.setItem(8, main.getItemManager().getItem("lobby.inventory.spacer"));
		if (page != pages) {
			inv.setItem(8, main.getItemManager().getItem("lobby.inventory.next"));
		}
		
		for (int i = 1 ; i < 8 ; i ++) {
			inv.setItem(i, main.getItemManager().getItem("lobby.inventory.spacer"));
		}
		//Adding the Arenas
		for (int i = 0 ; i < 45 ; i++) {
			int c = 54*page + i;
			if (!(arenas.size() -1 >= c)) {
				break;
			}
			Arena arena = arenas.get(c);
			inv.addItem(arena.cluster.getDisplayItem());
		}
		p.openInventory(inv);
	}
	
}

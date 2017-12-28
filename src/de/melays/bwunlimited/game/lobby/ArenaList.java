/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.game.lobby;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;

public class ArenaList {
	
	Player p;
	Main main;
	
	public ArenaList (Main main , Player p) {
		this.main = main;
		this.p = p;
	}
	
	public HashMap<Integer , Integer> slots = new HashMap<Integer , Integer>();
	
	public String title = "";
	
	public void openArenaList(String category , int page) {
		slots = new HashMap<Integer , Integer>();
		ArrayList<Arena> arenas = main.getArenaManager().getArenas(category);
		int pages = (int) Math.ceil((double)arenas.size() / 54.0);
		if (pages == 0) pages = 1;
		if (page > pages) page = pages;
		String title = main.getSettingsManager().getFile().getString("lobby.inventory.arenalist.title");
		title = main.c(title);
		title = title.replaceAll("%page%", page+"");
		title = title.replaceAll("%max%", pages + "");
		this.title = title;
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
			int c = 54*(page-1) + i;
			if (!(arenas.size() -1 >= c)) {
				break;
			}
			Arena arena = arenas.get(c);
			ItemStack stack = arena.cluster.getDisplayItem();
			ItemMeta meta = stack.getItemMeta();
			meta.setDisplayName(main.c(main.getSettingsManager().getFile().getString("lobby.inventory.arenalist.item.title").replaceAll("%id%", arena.id + "").replaceAll("%cluster%", arena.cluster.getDisplayName())));
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			meta.addItemFlags(ItemFlag.HIDE_DESTROYS);
			meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			List<String> lore = main.getSettingsManager().getFile().getStringList("lobby.inventory.arenalist.item.lore");
			for (String s : lore) {
				lore.set(lore.indexOf(s), main.c(s.replaceAll("%size%", arena.getAllPlayers().size() + "").replaceAll("%spec_size%", arena.specs.size() + "")));
			}
			
			meta.setLore(lore);
			stack.setItemMeta(meta);
			inv.addItem(stack);
			
			slots.put(9+i, arena.id);
		}
		p.openInventory(inv);
	}
	
}

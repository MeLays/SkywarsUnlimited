/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.bwshop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.bwunlimited.game.arenas.Arena;

public class ShopItem {
	
	String name;
	ConfigurationSection data;

	ItemStack stack;
	
	HashMap<Material , Integer> price = new HashMap<Material , Integer>();
	
	List<String> hide_categories;
	List<String> hide_clusters;
	
	public ShopItem (String name , ConfigurationSection data) {
		this.name = name;
		this.data = data;
		load();
	}
	
	public void load() {
		stack = BedwarsShop.loadItemStack(data);
		hide_categories = data.getStringList("hide_in_categories");
		if (hide_categories == null) hide_categories = new ArrayList<String>();
		hide_clusters = data.getStringList("hide_in_clusters");
		if (hide_clusters == null) hide_clusters = new ArrayList<String>();
		for (String s : data.getConfigurationSection("price").getKeys(false)) {
			try {
				Material m = Material.getMaterial(s.toUpperCase());
				if (m != null) {
					price.put(m, data.getInt("price." + s));
				}
			}catch (Exception e) {
				
			}
		}
	}
	
	public boolean isHidden (Arena a) {
		if (hide_clusters.contains(a.cluster.name)) {
			return true;
		}
		if (hide_categories.contains(a.main.getArenaManager().getCategory(a.id))) {
			return true;
		}
		return false;
	}
	
	public void addToPlayer(Player p) {
		ItemStack give = stack.clone();
		ItemMeta meta = give.getItemMeta();
		meta.setLore(null);
		give.setItemMeta(meta);
		p.getInventory().addItem(give);
	}
	
	public ItemStack getStack() {
		ItemStack give = stack.clone();
		ItemMeta meta = give.getItemMeta();
		meta.setLore(null);
		give.setItemMeta(meta);
		return give;
	}
	
	public void addToPlayer(Player p , int slot) {
		ItemStack give = stack.clone();
		ItemMeta meta = give.getItemMeta();
		meta.setLore(null);
		give.setItemMeta(meta);
		p.getInventory().setItem(slot , give);
	}
	
}

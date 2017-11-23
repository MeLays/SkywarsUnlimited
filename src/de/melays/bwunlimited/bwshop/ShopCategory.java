package de.melays.bwunlimited.bwshop;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import de.melays.bwunlimited.game.arenas.Arena;

public class ShopCategory {
	String name;
	ConfigurationSection data;

	ItemStack stack;
		
	List<String> hide_categories;
	List<String> hide_clusters;
	
	ShopItem[] items = {null  , null , null , null , null , null , null , null , null};
	
	public ShopCategory (String name , ConfigurationSection data) {
		this.name = name;
		this.data = data;
		load();
	}
	
	public void load() {
		stack = BedwarsShop.loadItemStack(data.getConfigurationSection("displayitem"));
		hide_categories = data.getStringList("hide_in_categories");
		if (hide_categories == null) hide_categories = new ArrayList<String>();
		hide_clusters = data.getStringList("hide_in_clusters");
		if (hide_clusters == null) hide_clusters = new ArrayList<String>();
		//Load Shop-Items
		Set<String> itemset = data.getConfigurationSection("items").getKeys(false);
		ArrayList<String> keys = new ArrayList<String>(itemset);
		for (int i = 0 ; i < 9 ; i ++) {
			if (!keys.get(i).startsWith("placeholder")) {
				this.items[i] = new ShopItem(keys.get(i) , data.getConfigurationSection("items").getConfigurationSection(keys.get(i)));
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
}

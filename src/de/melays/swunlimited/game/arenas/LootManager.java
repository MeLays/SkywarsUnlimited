package de.melays.swunlimited.game.arenas;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Chest;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.swunlimited.Utf8YamlConfiguration;
import de.melays.swunlimited.game.SoundDebugger;
import de.melays.swunlimited.map_manager.ClusterTools;

public class LootManager {
	
	public ArrayList<Location> looted_before = new ArrayList<Location>();
	
	Arena arena;
	
	public LootManager(Arena arena) {
		this.arena = arena;
		getItemFile().options().copyDefaults(true);
		saveFile();
	}
	
	public void openChest(Player p, Location loc) {
		if (!looted_before.contains(loc.getBlock().getLocation())) {
			Chest chest = ((Chest)loc.getBlock().getState());
			fillInventory((int)ClusterTools.distanceIgnoreY(loc, arena.middle) , chest.getInventory());
		}
	}
	
	public void fillInventory(int mid_distance , Inventory inv) {
		int bigger = arena.cluster.x_size;
		if (arena.cluster.x_size < arena.cluster.z_size) {
			bigger = arena.cluster.z_size;
		}
		int map_width_half = bigger / 2;
		double percent = 100 - (((double)mid_distance / (double)map_width_half) * 100);		
		ArrayList<ItemStack> stacks = new ArrayList<ItemStack>();
		for (String s : this.getItemFile().getKeys(false)) {
			if (percent >= this.getItemFile().getInt(s+".spawn.from_percent") && percent <= this.getItemFile().getInt(s+".spawn.to_percent")) {
				int probability = this.getItemFile().getInt(s+".spawn.probability");
				ItemStack add = getItem(s);
				int from = 1;
				int to = 1;
				if (this.getItemFile().contains(s+".spawn.from"))
					from = this.getItemFile().getInt(s+".spawn.from");
				if (this.getItemFile().contains(s+".spawn.to"))
					to = this.getItemFile().getInt(s+".spawn.to");
				for (int i = 0 ; i < probability ; i++) {
					add.setAmount(randInt(from, to));
					stacks.add(add.clone());
				}
			}
		}

		int empty = arena.cluster.main.getConfig().getInt("loot.empty_space_probability");
		
		for (int i = 0 ; i < inv.getSize() ; i++) {
			if (randInt(0,100) > empty) {
				inv.setItem(i, stacks.get((new Random()).nextInt(stacks.size())));
			}
		}
	}
	
	public static int randInt(int min, int max) {
	    Random rand = new Random();
	    int randomNum = rand.nextInt((max - min) + 1) + min;
	    return randomNum;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItem (String id) {
		String material_str = getItemFile().getString(id + ".material");
		String data_str = getItemFile().getString(id + ".byte");
		String amount_str = getItemFile().getString(id + ".amount");
		String displayname = getItemFile().getString(id + ".displayname");
		List<String> lore = getItemFile().getStringList(id + ".lore");
		
		Material material = Material.getMaterial(material_str);
		if (material == null) {
			try {
				material = Material.getMaterial(Integer.parseInt(material_str));
				if (material == null) {
					material = Material.PAPER;
				}
			} catch (NumberFormatException e) {
				material = Material.PAPER;
			}
		}
		
		byte data = 0;
		try {
			data = (byte) Integer.parseInt(data_str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		int amount = 1;
		try {
			amount = Integer.parseInt(amount_str);
		} catch (NumberFormatException e) {
			e.printStackTrace();
		}
		ItemStack stack = new ItemStack(material , amount , data);
		stack.getData().setData(data);
		ItemMeta meta = stack.getItemMeta();
		try {
			meta.setDisplayName(arena.cluster.main.c(displayname));
		} catch (Exception e) {

		}
		ArrayList<String> lore_colored = new ArrayList<String>();
		for (String s : lore) {
			lore_colored.add(arena.cluster.main.c(s));
		}
		meta.setLore(lore_colored);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	//Team File Managment
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "loot.yml";
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(arena.cluster.main.getDataFolder(), filenname);
	    }
	    if (!configurationFile.exists()) {
	    	arena.cluster.main.saveResource(filenname, true);
	    }
	    configuration = new Utf8YamlConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = arena.cluster.main.getResource(filenname);
	    if (defConfigStream != null) {
		    Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        configuration.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getItemFile() {
	    if (configuration == null) {
	    	reloadFile();
	    }
	    return configuration;
	}
	
	public void saveFile() {
	    if (configuration == null || configurationFile == null) {
	    return;
	    }
	    try {
	        configuration.save(configurationFile);
	    } catch (IOException ex) {
	    }
	}

}

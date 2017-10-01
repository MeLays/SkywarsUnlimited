package de.melays.bwunlimited.game;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.Utf8YamlConfiguration;

public class ItemManager {
	
	Main main;
	
	public ItemManager(Main main) {
		this.main = main;
		getItemFile().options().copyDefaults(true);
		saveFile();
	}

	@SuppressWarnings("deprecation")
	public boolean isItem (String id , ItemStack compare) {
		String material_str = getItemFile().getString(id + ".material");
		String data_str = getItemFile().getString(id + ".data");
		String displayname = getItemFile().getString(id + ".displayname");
		
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
		
		if (!compare.getType().equals(material)) {
			return false;
		}
		
		byte data = 0;
		try {
			data = (byte) Integer.parseInt(data_str);
		} catch (NumberFormatException e) {

		}
		
		if (compare.getData().getData() != data) {
			return false;
		}
		
		if (!compare.getItemMeta().getDisplayName().equals(main.c(displayname))) {
			return false;
		}
		
		return true;
	}
	
	@SuppressWarnings("deprecation")
	public ItemStack getItem (String id) {
		String material_str = getItemFile().getString(id + ".material");
		String data_str = getItemFile().getString(id + ".byte");
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
		ItemStack stack = new ItemStack(material , 1 , data);
		stack.getData().setData(data);
		ItemMeta meta = stack.getItemMeta();
		meta.setDisplayName(main.c(displayname));
		ArrayList<String> lore_colored = new ArrayList<String>();
		for (String s : lore) {
			lore_colored.add(main.c(s));
		}
		meta.setLore(lore_colored);
		stack.setItemMeta(meta);
		
		return stack;
	}
	
	//Team File Managment
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "items.yml";
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(main.getDataFolder(), filenname);
	    }
	    if (!configurationFile.exists()) {
	    	main.saveResource(filenname, true);
	    }
	    configuration = new Utf8YamlConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = main.getResource(filenname);
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

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.game.lobby;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.block.SignChangeEvent;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.game.arenas.settings.Settings;
import de.melays.swunlimited.log.Logger;
import de.melays.swunlimited.map_manager.ClusterTools;
import de.melays.swunlimited.map_manager.error.UnknownClusterException;

public class TemplateSignManager {
	
	Main main;
	
	public TemplateSignManager (Main main) {
		this.main = main;
		loadSigns();
	}
	
	HashMap<Integer , TemplateSign> signs = new HashMap<Integer , TemplateSign>();
	
	public void loadSigns() {
		for (TemplateSign sign : signs.values()) {
			sign.cancle();
		}
		signs = new HashMap<Integer , TemplateSign>();
		Set<String> keys = getFile().getKeys(false);
		int loaded = 0;
		for (String s : keys) {
			try {
				signs.put(Integer.parseInt(s), new TemplateSign(main , Integer.parseInt(s) , ClusterTools.getLiteLocation(getFile(), s) , main.getClusterManager().getCluster(getFile().getString(s+".cluster")).name
						,Settings.getFromSection(getFile().getConfigurationSection(s+".settings") , main)));
				loaded += 1;
			}catch (Exception ex) {
				ex.printStackTrace();
				Logger.log(main.console_prefix + "Could not load the template-sign '" + s + "'!");
			}
		}
		Logger.log(main.console_prefix + "Successfully loaded '" + loaded + "' template-signs!");
	}
	
	public HashMap<Integer , Location> getSigns(){
		HashMap<Integer , Location> r = new HashMap<Integer , Location>();
		Set<String> keys = getFile().getKeys(false);
		for (String s : keys) {
			try {
				r.put(Integer.parseInt(s), ClusterTools.getLiteLocation(getFile(), s));
			}catch (Exception ex) {}
		}
		return r;
	}
	
	public boolean removeSign (Location loc) {
		HashMap<Integer , Location> r = getSigns();
		for (int i : r.keySet()) {
			if (r.get(i).equals(loc)) {
				getFile().set(i+"", null);
				this.saveFile();
				loadSigns();
				return true;
			}
		}
		return false;
	}
	
	public TemplateSign getSign (Location loc) {
		HashMap<Integer , Location> r = getSigns();
		for (int i : r.keySet()) {
			if (r.get(i).equals(loc)) {
				return signs.get(i);
			}
		}
		return null;
	}
	
	public int saveSign(SignChangeEvent e) throws UnknownClusterException {
		if (!e.getLine(0).equalsIgnoreCase("[TEMPLATE]")) {
			return -1;
		}
		String cluster = e.getLine(1);
		main.getClusterManager().getCluster(cluster);
		Set<String> keys = getFile().getKeys(false);
		int highest = 0;
		for (String s : keys) {
			try {
				int current = Integer.parseInt(s);
				if (current > highest) {
					highest = current;
				}
			}catch(Exception ex){
				
			}
		}
		int id = highest + 1;
		ClusterTools.saveLiteLocation(getFile(), id+"" , e.getBlock().getLocation());
		getFile().set(id+".cluster", cluster);
		new Settings(main).saveToConfig(getFile(), id+".settings");
		this.saveFile();
		signs.put(id, new TemplateSign(main , id , ClusterTools.getLiteLocation(getFile(), id+"") , main.getClusterManager().getCluster(getFile().getString(id+".cluster")).name
				,Settings.getFromSection(getFile().getConfigurationSection(id+".settings") , main)));
		return id;
	}
	
	//Sign File Managment
	
	FileConfiguration configuration = null;
	File configurationFile = null;
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(main.getDataFolder(), "signs/templatesigns.yml");
	    }
	    configuration = YamlConfiguration.loadConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = main.getResource("signs/templatesigns.yml");
	    if (defConfigStream != null) {
		    Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        configuration.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getFile() {
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

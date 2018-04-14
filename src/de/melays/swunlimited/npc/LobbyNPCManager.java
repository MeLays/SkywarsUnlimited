/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.npc;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.scheduler.BukkitRunnable;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.Utf8YamlConfiguration;
import de.melays.swunlimited.log.Logger;
import de.melays.swunlimited.map_manager.ClusterTools;
import net.md_5.bungee.api.ChatColor;

public class LobbyNPCManager {
	
	Main main;
	
	public LobbyNPCManager (Main main) {
		this.main = main;
		reloadFile();
		new BukkitRunnable() {

			@Override
			public void run() {
				loadAll();
			}
			
		}.runTaskLater(main, 10);
	}
	
	HashMap<Integer , LobbyNPC> npcs = new HashMap<Integer , LobbyNPC>();
	
	public LobbyNPC getLobbyNPC(Entity e) {
		for (LobbyNPC npc : npcs.values()) {
			if (npc.e.getEntityId() == e.getEntityId()) {
				return npc;
			}
		}
		return null;
	}
	
	public void loadAll () {
		try {
			for (String s : this.getConfiguration().getConfigurationSection("npcs").getKeys(false)) {
				try {
					load(Integer.parseInt(s));
				} catch (Exception e) {

				}
			}
			Logger.log(main.console_prefix + ChatColor.YELLOW + "Loaded " + npcs.size() + " npc's!");
		} catch (Exception e) {

		}
	}
	
	public void load (Integer id) {
		try {
			Location loc = ClusterTools.getLocation(this.getConfiguration(), "npcs." +id);
			if (npcs.containsKey(id)) {
				npcs.get(id).remove();
			}
			String displayname = main.c(this.getConfiguration().getString("npcs." + id + ".displayname"));
			NPCType ncp = NPCType.valueOf(this.getConfiguration().getString("npcs." + id + ".npc").toUpperCase());
			EntityType type = EntityType.valueOf(this.getConfiguration().getString("npcs." + id + ".type").toUpperCase());
			HashMap<String , Integer> nbt_data = this.getHashMap("npcs." + id + ".nbt_data");
			boolean nametag = this.getConfiguration().getBoolean("npcs." + id + ".nametag");
			if (!nametag)
				displayname = null;
			LobbyNPC lobbynpc = new LobbyNPC(loc , main , ncp , type , nbt_data, displayname , nametag);
			npcs.put(id, lobbynpc);
		} catch (Exception e) {
			e.printStackTrace();
			Logger.log(main.console_prefix + ChatColor.RED + "Error loading the Lobby NPC '" + id + "'!");
		}
	}
	
	public int addNPC (Location loc , EntityType type , NPCType npc , String displayname , HashMap<String , Integer> nbt_data , boolean nametag) {
		int id = this.addCounting("npcs", loc);
		this.saveHashMap("npcs." + id + ".nbt_data", nbt_data);
		this.getConfiguration().set("npcs." + id + ".npc", npc.toString());
		this.getConfiguration().set("npcs." + id + ".type", type.toString());
		this.getConfiguration().set("npcs." + id + ".displayname", displayname);
		this.getConfiguration().set("npcs." + id + ".nametag", nametag);
		this.saveFile();
		load (id);
		return id;
	}
	
	public int addHologram (Location loc , String displayname) {
		int id = this.addCounting("npcs", loc);
		HashMap<String , Integer> nbt_data = new HashMap<String , Integer>();
			nbt_data.put("Invisible", 1);
			nbt_data.put("NoGravity", 1);
			nbt_data.put("Invulnerable", 1);
		this.saveHashMap("npcs." + id + ".nbt_data", nbt_data);
		this.getConfiguration().set("npcs." + id + ".npc", NPCType.DISPLAY.toString());
		this.getConfiguration().set("npcs." + id + ".type", EntityType.ARMOR_STAND.toString());
		this.getConfiguration().set("npcs." + id + ".displayname", displayname);
		this.getConfiguration().set("npcs." + id + ".nametag", true);
		this.saveFile();
		load (id);
		return id;
	}
	
	public void removeNPC (int id) {
		if (npcs.containsKey(id)) {
			npcs.get(id).remove();
		}
		this.getConfiguration().set("npcs." + id, null);
		this.saveFile();
	}
	
	public void saveHashMap (String path , HashMap<String,Integer> put) {
		for (String s : put.keySet()) {
			this.getConfiguration().set(path + "." + s, put.get(s));
		}
	}
	
	public HashMap<String,Integer> getHashMap (String path) {
		HashMap<String,Integer> r = new HashMap<String,Integer>();
		for (String s : this.getConfiguration().getConfigurationSection(path).getKeys(false)) {
			r.put(s, this.getConfiguration().getInt(path + "." + s));
		}
		return r;
	}
	
	public int addCounting (String counterpath , Location loc) {
		try {
			ConfigurationSection section = getConfiguration().getConfigurationSection(counterpath);
			Set<String> keys = section.getKeys(false);
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
			int new_loc = highest + 1;
			ClusterTools.saveLocation(this.getConfiguration(), counterpath+"." + new_loc, loc);
			return new_loc;
		} catch (Exception e) {
			ClusterTools.saveLocation(this.getConfiguration(), counterpath+"." + 1, loc);
			return 1; 
			
		}
	}
	
	public void setLocationCounting (String path , int id , Location loc) {
		ClusterTools.saveLocation(this.getConfiguration(), path+".id", loc);
	}
	
	public void removeLocationCounting (String counterpath , int id) {
		this.getConfiguration().set(counterpath+"."+id, null);
		this.saveFile();
	}
	
	public ArrayList<Location> getLocationsCounting (String counterpath) {
		ConfigurationSection section = getConfiguration().getConfigurationSection(counterpath);
		ArrayList<Location> locs = new ArrayList<Location>();
		try {
			Set<String> keys = section.getKeys(false);
			for (String s : keys) {
				locs.add(ClusterTools.getLocation(this.getConfiguration(), counterpath+"."+s));
			}
			return locs;
		} catch (Exception e) {
			return locs;
		}
	}
	
	//NPC File Managment
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "npcs.yml";
	
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
	
	public FileConfiguration getConfiguration() {
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

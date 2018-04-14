/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.map_manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.inventory.ItemStack;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.log.Logger;
import de.melays.swunlimited.map_manager.meta.ClusterMeta;
import de.melays.swunlimited.teams.Team;
import de.melays.swunlimited.teams.error.UnknownTeamException;
import net.md_5.bungee.api.ChatColor;

public class Cluster {
	
	public Location max;
	//Min Location is ALWAYS the relativ Location
	public Location min;
	
	public String name;
	ClusterState state = ClusterState.PROCESSING;
	
	ClusterGeneratorType generator = ClusterGeneratorType.COMPLETE;
	
	LinkedHashMap<RelativeLocation , AdvancedMaterial> cluster_list = new LinkedHashMap<RelativeLocation , AdvancedMaterial>();
	public LinkedHashMap<RelativeLocation , AdvancedMaterial> getClusterMap(){
		return cluster_list;
	}
	
	public Main main;
	
	public int x_size;
	public int y_size;
	public int z_size;
	
	ClusterMeta clusterMeta;
	
	String displayname;
	ItemStack displayitem;
	
	public String getDisplayName() {
		return displayname;
	}
	
	public ItemStack getDisplayItem() {
		return displayitem;
	}
	
	Cluster(Main main , String name){
		this.main = main;
		this.name = name;
		reloadCluster();
	}
	
	public ClusterMeta getClusterMeta() {
		return clusterMeta;
	}
	
	@SuppressWarnings("deprecation")
	public void reloadCluster() {
		
		//Load displayname
		
		displayname = main.getClusterManager().getConfiguration().getString(name+".display");
		if (displayname == null) displayname = name;
		
		
		//Load display item
		
		String material = main.getClusterManager().getConfiguration().getString(name+".display_item");
		if (material == null) material = Material.PAPER.toString();
		byte data = 0;
		if (material.contains(":")) {
			String byte_raw = material.split(":")[1];
			material = material.split(":")[0];
			try {
				data = Byte.parseByte(byte_raw);
			}catch (Exception ex) {
				
			}
		}
		
		Material m = Material.getMaterial(material);
		if (m == null) {
			try {
				m = Material.getMaterial(Integer.parseInt(material));
			} catch (NumberFormatException e) {
				m = Material.PAPER;
			}
			if (m == null) {
				m = Material.PAPER;
			}
		}
		
		this.displayitem = new ItemStack(m, 1 , data);
		
		//Load cluster
		
		cluster_list = new LinkedHashMap<RelativeLocation , AdvancedMaterial>();
		
		if (main.getClusterManager().getConfiguration().getString(name+".generator") != null) {
			try {
				ClusterGeneratorType tempgenerator = ClusterGeneratorType.valueOf(main.getClusterManager().getConfiguration().getString(name+".generator").toUpperCase());
				if (tempgenerator != null) {
					generator = tempgenerator;	
				}
				else {
					Logger.log(main.console_prefix + "The generator of '" + name + "' is invalid");
				}
			} catch (Exception e) {
				Logger.log(main.console_prefix + "The generator of '" + name + "' is invalid");
			}
		}

		max = ClusterTools.getLiteLocation(main.getClusterManager().getConfiguration() , name+".border.max");
		min = ClusterTools.getLiteLocation(main.getClusterManager().getConfiguration() , name+".border.min");
		
		//Recalculate min and max just to be sure
		Location loc [] = ClusterTools.generateMaxMinPositions(max ,  min);
		min = loc[0];
		max = loc[1];
		
		x_size = max.getBlockX() - min.getBlockX();
		y_size = max.getBlockY() - min.getBlockY();
		z_size = max.getBlockZ() - min.getBlockZ();

		
		try {
			Logger.log(main.console_prefix + "Loading cluster '" + name + "' into memory...");
			Date started = new Date();
			startItterating();
			Date ended = new Date();
			state = ClusterState.READY;
			if (!main.getClusterManager().getConfiguration().getBoolean(name+".enabled"))
				state = ClusterState.DISABLED;
			clusterMeta = new ClusterMeta(main , this);
			loadMeta();
			if (!checkReady()) {
				Logger.log(main.console_prefix + ChatColor.RED + "The cluster '" + name + "' will not be activated. See info above!");
				state = ClusterState.DISABLED;
			}
			Logger.log(main.console_prefix + "Cluster '" + name + "' succesfully loaded in " + (ended.getTime() - started.getTime()) + "ms, stored " + cluster_list.size() + " blocks");
			if (main.getClusterManager().getConfiguration().getString(name+".generator") == null) {
				String generator = "";
				if (cluster_list.size() <= 5000) {
					generator = "COMPLETE";
				}
				else {
					generator = "SPLITTED";
				}
				Logger.log(main.console_prefix + "Set the default generator of '" + name + "' to '" + generator + "'");
				main.getClusterManager().getConfiguration().set(name+".generator", generator);
				main.getClusterManager().saveFile();
			}
		} catch (Exception e) {
			e.printStackTrace();
			Logger.log(main.console_prefix + ChatColor.RED + "The cluster '" + name + "' could not be loaded");
			state = ClusterState.ERROR;
		}
		
	}
	
	@SuppressWarnings("deprecation")
	private void startItterating() throws Exception {
		if (!min.getWorld().getName().equals(max.getWorld().getName()))
			throw new Exception();
        for(int x = min.getBlockX(); x <= max.getBlockX(); x++){
            for(int y = min.getBlockY(); y <= max.getBlockY(); y++){
                for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++){
                	Location loc = new Location (min.getWorld() , x , y , z);
                	Block b = loc.getBlock();
                	if (b.getType() != Material.AIR) {
                		cluster_list.put(new RelativeLocation(min , loc) , new AdvancedMaterial(b.getType() , b.getData()));
                	}
                }
            }
        }
	}
	
	public ArrayList<Team> teams = new ArrayList<Team>();
	public HashMap<String, FineRelativeLocation> teamspawner = new HashMap<String , FineRelativeLocation>();
	
	public void loadMeta() {
		teams = this.getClusterMeta().getTeams();
		for (Team t : new ArrayList<Team>(teams)) {
			try {
				teamspawner.put(t.name, this.getClusterMeta().getTeamSpawn(t.name));
			} catch (UnknownTeamException e) {
				teams.remove(t);
				Logger.log(main.console_prefix + ChatColor.RED + "Corrupted team '"+t.name+"' detected in cluster '"+this.name+"'!");
			}
		}
	}
	
	public boolean checkReady() {
		boolean isReady = true;
		if (teams.size() <= 1) {
			Logger.log(main.console_prefix + ChatColor.RED + "The cluster does not have enought teams ("+teams.size()+")");
			isReady = false;
		}
		for (Team t : teams) {
			if (!teamspawner.containsKey(t.name)) {
				Logger.log(main.console_prefix + ChatColor.RED + "The team '"+t.name+"' does not have a teamspawn");
				isReady = false;
			}
			else if (teamspawner.get(t.name) == null) {
				Logger.log(main.console_prefix + ChatColor.RED + "The team '"+t.name+"' does not have a teamspawn");
				isReady = false;
			}
		}
		return isReady;
	}
}

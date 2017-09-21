package de.melays.bwunlimited.map_manager;

import java.util.Date;
import java.util.LinkedHashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.log.Logger;

public class Cluster {
	
	Location max;
	//Min Location is ALWAYS the relativ Location
	Location min;
	
	String name;
	ClusterState state = ClusterState.PROCESSING;
	
	ClusterGeneratorType generator = ClusterGeneratorType.COMPLETE;
	
	LinkedHashMap<RelativeLocation , AdvancedMaterial> cluster_list = new LinkedHashMap<RelativeLocation , AdvancedMaterial>();
	public LinkedHashMap<RelativeLocation , AdvancedMaterial> getClusterMap(){
		return cluster_list;
	}
	
	Main main;
	
	public int x_size;
	public int y_size;
	public int z_size;
	
	Cluster(Main main , String name){
		this.main = main;
		this.name = name;
		reloadCluster();
	}
	
	public void reloadCluster() {
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
			Logger.log(main.console_prefix + "The cluster '" + name + "' could not be loaded");
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
}

package de.melays.bwunlimited.map_manager;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.error.InvalidNameException;
import de.melays.bwunlimited.log.Logger;
import de.melays.bwunlimited.map_manager.error.ClusterAlreadyExistsException;
import de.melays.bwunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;
import de.melays.bwunlimited.map_manager.error.WrongWorldException;

public class ClusterManager {
	
	HashMap<String , Cluster> cluster_map = new HashMap<String , Cluster>();
	
	Main main;
	
	public ClusterManager(Main main) {
		this.main = main;
		reloadFile();
		saveFile();
	}
	
	public void loadClusters() {
		Set<String> keys = getConfiguration().getKeys(false);
		if (keys == null || keys.size() == 0) {
			Logger.log(main.console_prefix + "No clusters have been created yet! Loading none.");
		}
		else {
			for (String s : keys) {
				cluster_map.put(s, new Cluster(main , s));
			}
		}
	}
	
	public ClusterHandler getNewHandler (String Cluster) throws ClusterAvailabilityException , UnknownClusterException{
		if (!cluster_map.containsKey(Cluster))
			throw new UnknownClusterException();
		if (cluster_map.get(Cluster).state != ClusterState.READY)
			throw new ClusterAvailabilityException();
		return new ClusterHandler(main , cluster_map.get(Cluster));
	}
	
	public Cluster getCluster (String name) throws UnknownClusterException{
		if (!cluster_map.containsKey(name))
			throw new UnknownClusterException();
		return cluster_map.get(name);
	}
	
	public void createCluster(String name , Location loc , Location loc2) throws ClusterAlreadyExistsException, InvalidNameException, WrongWorldException {
		createCluster(name , loc , loc2 , true);
	}
	
	public void createCluster(String name , Location loc , Location loc2 , boolean load) throws ClusterAlreadyExistsException, InvalidNameException, WrongWorldException {
		Set<String> keys = getConfiguration().getKeys(false);
		if (keys != null) {
			if (keys.contains(name)) {
				throw new ClusterAlreadyExistsException();
			}
		}
		if (!StringUtils.isAlphanumeric(name)) {
			throw new InvalidNameException();
		}
		if (!loc.getWorld().getName().equals(main.presetworld) || !loc2.getWorld().getName().equals(main.presetworld)) {
			throw new WrongWorldException();
		}
		Location locs[] = ClusterTools.generateMaxMinPositions(loc, loc2);
		Location min = locs[0];
		Location max = locs[1];
		
		getConfiguration().set(name+".display", name);
		getConfiguration().set(name+".enabled", false);
		ClusterTools.saveLiteLocation(getConfiguration(), name+".border.max", max);
		ClusterTools.saveLiteLocation(getConfiguration(), name+".border.min", min);
		saveFile();
		if (load)
			loadCluster(name);
	}
	
	private void loadCluster(String name) {
		cluster_map.put(name, new Cluster(main , name));
	}
	
	//Cluster File Managment
	
	FileConfiguration configuration = null;
	File configurationFile = null;
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(main.getDataFolder(), "clusters.yml");
	    }
	    configuration = YamlConfiguration.loadConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = main.getResource("clusters.yml");
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

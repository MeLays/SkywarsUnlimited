package de.melays.bwunlimited.map_manager.meta;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Set;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.map_manager.ClusterTools;
import de.melays.bwunlimited.map_manager.FineRelativeLocation;

public class ClusterMetaTools {

	Main main;
	
	public ClusterMetaTools(Main main) {
		this.main = main;
	}
	
	public int addCounting (String counterpath , FineRelativeLocation loc) {
		try {
			ConfigurationSection section = getClusterFile().getConfigurationSection(counterpath);
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
			ClusterTools.saveFineRelativeLocation(this.getClusterFile(), counterpath+"." + new_loc, loc);
			return new_loc;
		} catch (Exception e) {
			ClusterTools.saveFineRelativeLocation(this.getClusterFile(), counterpath+"." + 1, loc);
			return 1; 
			
		}
	}
	
	public void setFineRelativeLocationCounting (String path , int id , FineRelativeLocation loc) {
		ClusterTools.saveFineRelativeLocation(this.getClusterFile(), path+".id", loc);
	}
	
	public void removeFineRelativeLocationCounting (String counterpath , int id) {
		this.getClusterFile().set(counterpath+"."+id, null);
		this.saveFile();
	}
	
	public ArrayList<FineRelativeLocation> getFineRelativeLocationsCounting (String counterpath) {
		ConfigurationSection section = getClusterFile().getConfigurationSection(counterpath);
		ArrayList<FineRelativeLocation> locs = new ArrayList<FineRelativeLocation>();
		try {
			Set<String> keys = section.getKeys(false);
			for (String s : keys) {
				locs.add(ClusterTools.getFineRelativeLocation(this.getClusterFile(), counterpath+"."+s));
			}
			return locs;
		} catch (Exception e) {
			return locs;
		}
	}
	
	public LinkedHashMap<Integer , FineRelativeLocation> getFineRelativeLocationsCountingMap (String counterpath) {
		ConfigurationSection section = getClusterFile().getConfigurationSection(counterpath);
		LinkedHashMap<Integer , FineRelativeLocation> locs = new LinkedHashMap<Integer , FineRelativeLocation>();
		try {
			Set<String> keys = section.getKeys(false);
			for (String s : keys) {
				try {
					locs.put(Integer.parseInt(s), ClusterTools.getFineRelativeLocation(this.getClusterFile(), counterpath+"."+s));
				} catch (Exception e) {

				}
			}
			return locs;
		} catch (Exception e) {
			return locs;
		}
	}
	
	public int addCounting (String counterpath , Location loc) {
		try {
			ConfigurationSection section = getClusterFile().getConfigurationSection(counterpath);
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
			ClusterTools.saveLocation(this.getClusterFile(), counterpath+"." + new_loc, loc);
			return new_loc;
		} catch (Exception e) {
			ClusterTools.saveLocation(this.getClusterFile(), counterpath+"." + 1, loc);
			return 1; 
			
		}
	}
	
	public void setLocationCounting (String path , int id , Location loc) {
		ClusterTools.saveLocation(this.getClusterFile(), path+".id", loc);
	}
	
	public void removeLocationCounting (String counterpath , int id) {
		this.getClusterFile().set(counterpath+"."+id, null);
		this.saveFile();
	}
	
	public ArrayList<Location> getLocationsCounting (String counterpath) {
		ConfigurationSection section = getClusterFile().getConfigurationSection(counterpath);
		ArrayList<Location> locs = new ArrayList<Location>();
		try {
			Set<String> keys = section.getKeys(false);
			for (String s : keys) {
				locs.add(ClusterTools.getLocation(this.getClusterFile(), counterpath+"."+s));
			}
			return locs;
		} catch (Exception e) {
			return locs;
		}
	}
	
	//Team File Managment

	public void reloadFile() {
		main.getClusterManager().reloadFile();
	}
	
	public FileConfiguration getClusterFile() {
		return main.getClusterManager().getConfiguration();
	}
	
	public void saveFile() {
		main.getClusterManager().saveFile();
	}
	
}

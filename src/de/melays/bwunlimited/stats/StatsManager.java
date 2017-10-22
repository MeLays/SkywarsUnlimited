package de.melays.bwunlimited.stats;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.Utf8YamlConfiguration;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.internal_statsapi.InternalChannel;
import de.melays.bwunlimited.internal_statsapi.InternalStatsAPI;
import de.melays.bwunlimited.log.Logger;
import de.melays.statsAPI.Channel;
import de.melays.statsAPI.StatsAPI;
import net.md_5.bungee.api.ChatColor;

public class StatsManager {
	
	StatsAPI statsapi;
	Channel channel;
	
	InternalStatsAPI istatsapi;
	InternalChannel ichannel;
	
	StatsMode mode = StatsMode.YAML;
	
	Main main;
	
	public StatsManager(Main main) {
		this.main = main;
		if (Bukkit.getPluginManager().isPluginEnabled("StatsAPI")) {
			Logger.log(main.console_prefix + "StatsAPI was found on this server! It will be used to store the statistics of your players!");
			statsapi = StatsAPI.getSpigotInstance();
			mode = StatsMode.STATSAPI;
			if (statsapi.isDummy()) {
				Logger.log(main.console_prefix + ChatColor.RED + "StatsAPI is not connected to a database server! Using the .yml file instead.");
				mode = StatsMode.YAML;
			}
			else {
				this.channel = statsapi.hookChannel(main, "bedwarsunlimited");
			}
		}
		else {
			StatsMode mode = StatsMode.valueOf(main.getConfig().getString("stats").toUpperCase());
			if (mode == null) {
				Logger.log(main.console_prefix + ChatColor.RED + "Unknown statsmode '" + main.getConfig().getString("stats") + "'. Using YAML ...");
				this.mode = mode = StatsMode.YAML;
				return;
			}
			if (mode == StatsMode.MYSQL) {
				Logger.log(main.console_prefix + "Trying to connect to the mysql-database");
				istatsapi = new InternalStatsAPI(main);
				if (istatsapi.isDummy()) {
					Logger.log(main.console_prefix + ChatColor.RED + "Could not connect to the MySQL-Server! Using the .yml file instead.");
					mode = StatsMode.YAML;
					return;
				}
				else {
					this.ichannel = istatsapi.hookChannel(main, "bedwarsunlimited");
					Logger.log(main.console_prefix + "MySQL connected successfully!");
					return;
				}
			}
		}
		if (mode == StatsMode.YAML) {
			Logger.log(main.console_prefix + ChatColor.GOLD + "YAML files are not recommenced to store your statistics on larger servers!");
			saveFile();
		}
	}
	
	public static boolean suitable(Player p , Arena a) {
		if (!a.settings.stats) return false;
		if (p.hasPermission("bwunlimited.nostats")) return false;
		return true;
	}
	
	public void setKey(UUID uuid , String key , int i) {
		try {
			if (mode == StatsMode.MYSQL) {
				ichannel.setKey(uuid, key, i);
			}
			else if (mode == StatsMode.STATSAPI) {
				channel.setKey(uuid, key, i);
			}
			else if (mode == StatsMode.YAML) {
				getFile().set(uuid.toString() + "." + key, i);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void addToKey (UUID uuid , String key , int add) {
		try {
			if (mode == StatsMode.STATSAPI) {
				channel.addToKey(uuid, key, add);
				return;
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		setKey(uuid , key , getKey(uuid , key) + add);
	}
	
	public void setStringKey(UUID uuid , String key , String str) {
		try {
			if (mode == StatsMode.MYSQL) {
				ichannel.setStringKey(uuid, key, str);
			}
			else if (mode == StatsMode.STATSAPI) {
				channel.setStringKey(uuid, key, str);
			}
			else if (mode == StatsMode.YAML) {
				getFile().set(uuid.toString() + "." + key, str);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public int getKey(UUID uuid , String key) {
		try {
			if (mode == StatsMode.MYSQL) {
				return ichannel.getKey(uuid, key);
			}
			else if (mode == StatsMode.STATSAPI) {
				return channel.getKey(uuid, key);
			}
			else if (mode == StatsMode.YAML) {
				if (getFile().contains(uuid.toString() + "." + key))
					return getFile().getInt(uuid.toString() + "." + key);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	public String getStringKey(UUID uuid , String key) {
		try {
			if (mode == StatsMode.MYSQL) {
				return ichannel.getStringKey(uuid, key);
			}
			else if (mode == StatsMode.STATSAPI) {
				return channel.getStringKey(uuid, key);
			}
			else if (mode == StatsMode.YAML) {
				if (getFile().contains(uuid.toString() + "." + key))
					return getFile().getString(uuid.toString() + "." + key);
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	public void addKill (Arena a , Player p) {
		this.addToKey(p.getUniqueId(), "kills", 1);
	}
	
	public void addDeath (Arena a , Player p) {
		this.addToKey(p.getUniqueId(), "deaths", 1);
	}
	
	public void addBed (Arena a , Player p) {
		this.addToKey(p.getUniqueId(), "beds", 1);
	}
	
	public void addGame (Arena a , Player p) {
		this.addToKey(p.getUniqueId(), "games", 1);
	}
	
	public void addWon (Arena a , Player p) {
		this.addToKey(p.getUniqueId(), "won", 1);
	}
	
	public void addLost (Arena a , Player p) {
		this.addToKey(p.getUniqueId(), "lost", 1);
	}
	
	public void addPoints (Arena a , Player p , int amount) {
		if (StatsManager.suitable(p, a)) {
			this.addToKey(p.getUniqueId(), "points", amount);
		}
	}
	
	//Team File Managment
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "stats.yml";
	
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

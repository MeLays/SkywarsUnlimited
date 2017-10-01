package de.melays.bwunlimited.game.lobby;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.colortab.ColorTabAPI;
import de.melays.bwunlimited.game.PlayerTools;
import de.melays.bwunlimited.map_manager.ClusterTools;

public class LobbyManager {
	
	Main main;
	
	public LobbyManager(Main main) {
		this.main = main;
		saveFile();
	}
	
	public void setLobbyLocation(Location loc) {
		ClusterTools.saveLocation(getLobbyFile(), "lobby", loc);
		this.saveFile();
	}
	
	public Location getLobbyLocation() {
		if (getLobbyFile().getString("lobby.x") != null) {
			return ClusterTools.getLocation(getLobbyFile(), "lobby");
		}
		return null;
	}

	public void setGameLobbyLocation(Location loc) {
		ClusterTools.saveLocation(getLobbyFile(), "gamelobby", loc);
		this.saveFile();
	}
	
	public Location getGameLobbyLocation() {
		if (getLobbyFile().getString("gamelobby.x") != null) {
			return ClusterTools.getLocation(getLobbyFile(), "gamelobby");
		}
		return null;
	}
	
	public void toLobby (Player p) {
		p.teleport(getLobbyLocation());
		PlayerTools.resetPlayer(p);
		p.setGameMode(GameMode.SURVIVAL);
		ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
		updateVisibility();
		
		if (main.getConfig().getBoolean("lobby.challenger.enabled"))
			p.getInventory().setItem(main.getConfig().getInt("lobby.challenger.slot"), main.getItemManager().getItem("lobby.challenger"));
		
		if (main.getConfig().getBoolean("lobby.gamelist.enabled"))
			p.getInventory().setItem(main.getConfig().getInt("lobby.gamelist.slot"), main.getItemManager().getItem("lobby.gamelist"));
	}
	
	public void updateVisibility() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!main.getArenaManager().isInGame(p))
				for (Player player : Bukkit.getOnlinePlayers()) {
					if (main.getArenaManager().isInGame(player)) {
						p.hidePlayer(player);
						player.hidePlayer(p);
					}
					else {
						p.showPlayer(player);
						player.showPlayer(p);
					}
				}
		}
	}
	
	//Team File Managment
	
	FileConfiguration configuration = null;
	File configurationFile = null;
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(main.getDataFolder(), "lobby.yml");
	    }
	    configuration = YamlConfiguration.loadConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = main.getResource("lobby.yml");
	    if (defConfigStream != null) {
		    Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        configuration.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getLobbyFile() {
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

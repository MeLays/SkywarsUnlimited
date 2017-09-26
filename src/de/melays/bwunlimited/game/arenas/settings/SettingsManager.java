package de.melays.bwunlimited.game.arenas.settings;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.melays.bwunlimited.Main;

public class SettingsManager {
	
	Main main;
	
	public SettingsManager(Main main) {
		this.main = main;
		getFile().options().copyDefaults(true);
		saveFile();
	}
	
	//Settings File Managment
	
	FileConfiguration configuration = null;
	File configurationFile = null;
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(main.getDataFolder(), "settings.yml");
	    }
	    configuration = YamlConfiguration.loadConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = main.getResource("settings.yml");
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

package de.melays.bwunlimited.teams;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.error.InvalidNameException;
import de.melays.bwunlimited.teams.error.TeamAlreadyExistsException;

public class TeamManager {
	
	Main main;
	
	public TeamManager (Main main){
		this.main = main;
		this.getTeamFile().options().copyDefaults(true);
		this.saveFile();
	}
	
	public void createTeam (String name , String displayname , Color color , int max) throws TeamAlreadyExistsException, InvalidNameException{
		
		if (exists (name)) {
			throw new TeamAlreadyExistsException();
		}
		if (!StringUtils.isAlphanumeric(name) || !StringUtils.isAlphanumeric(displayname) ) {
			throw new InvalidNameException();
		}
		getTeamFile().set(name+".display", displayname);
		getTeamFile().set(name+".color", color.color);
		getTeamFile().set(name+".max", max);
		saveFile();
	}
	
	public void removeTeam (String name){
		getTeamFile().set(name , null);
		saveFile();
	}
	
	public boolean exists (String s){
		if (getTeamFile().getKeys(false).contains(s)){
			return true;
		}
		return false;
	}
	
	//Team File Managment
	
	FileConfiguration configuration = null;
	File configurationFile = null;
	
	public void reloadFile() {
	    if (configurationFile == null) {
	    	configurationFile = new File(main.getDataFolder(), "teams.yml");
	    }
	    configuration = YamlConfiguration.loadConfiguration(configurationFile);

	    java.io.InputStream defConfigStream = main.getResource("teams.yml");
	    if (defConfigStream != null) {
		    Reader reader = new InputStreamReader(defConfigStream);
	        YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
	        configuration.setDefaults(defConfig);
	    }
	}
	
	public FileConfiguration getTeamFile() {
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

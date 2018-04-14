/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.teams;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;

import org.apache.commons.lang.StringUtils;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.Utf8YamlConfiguration;
import de.melays.swunlimited.error.InvalidNameException;
import de.melays.swunlimited.log.Logger;
import de.melays.swunlimited.teams.error.TeamAlreadyExistsException;

public class TeamManager {
	
	Main main;
	
	HashMap<String , Team> teams = new HashMap<String , Team>();
	
	public TeamManager (Main main){
		this.main = main;
		this.getTeamFile().options().copyDefaults(true);
		this.saveFile();
		reload();
	}
	
	public void createTeam (String name , String displayname , Color color , int max) throws TeamAlreadyExistsException, InvalidNameException{
		
		if (exists (name)) {
			throw new TeamAlreadyExistsException();
		}
		if (!StringUtils.isAlphanumeric(name) && !StringUtils.isAlphanumeric(displayname)) {
			throw new InvalidNameException();
		}
		getTeamFile().set(name+".display", displayname);
		getTeamFile().set(name+".color", color.color);
		getTeamFile().set(name+".max", max);
		saveFile();
		load(name);
	}
	
	public void reload() {
		teams = new HashMap<String , Team>();
		for (String s : getTeamFile().getKeys(false)) {
			try {
				load(s);
			} catch (Exception e) {

			}
		}
	}
	
	public HashMap<String , Team> getTeams(){
		return teams;
	}
	
	public void load (String s) {
		teams.put(s, new Team(main, s, this.getTeamFile().getString(s+".display"), new Color(this.getTeamFile().getString(s+".color")), this.getTeamFile().getInt(s+".max")));
		Logger.log(main.console_prefix + "Loaded team '" + s + "'");
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
	
	YamlConfiguration configuration = null;
	File configurationFile = null;
	
	String filenname = "teams.yml";
	
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

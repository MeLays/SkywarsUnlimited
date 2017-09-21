package de.melays.bwunlimited;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import de.melays.bwunlimited.commands.MainCommand;
import de.melays.bwunlimited.listeners.CreatureSpawnEventListener;
import de.melays.bwunlimited.log.Logger;
import de.melays.bwunlimited.map_manager.ClusterManager;
import de.melays.bwunlimited.messages.MessageFetcher;
import de.melays.bwunlimited.multiworld.EmptyRoomGenerator;
import de.melays.bwunlimited.teams.TeamManager;
import de.melays.bwunlimited.tools.MarkerTool;

public class Main extends JavaPlugin{
	
	public String console_prefix =  ChatColor.BLUE + "BW" + ChatColor.DARK_BLUE + "UNLMTD" + ChatColor.BLUE + " LOG" + ChatColor.YELLOW + " > ";
	public String prefix = c("&9BW&1\u221E&8 - &e");
	public String gameworld;
	public String presetworld;
	
	public String c (String msg) { return ChatColor.translateAlternateColorCodes('&', msg); }
	
	//Management Objects
	ClusterManager clusterManager;
	
	public ClusterManager getClusterManager() {
		return clusterManager;
	}
	
	MessageFetcher messageFetcher;
	
	public MessageFetcher getMessageFetcher() {
		return messageFetcher;
	}
	
	TeamManager teamManager;
	
	public TeamManager getTeamManager() {
		return teamManager;
	}
	
	//Tools
	MarkerTool markerTool; 
	public MarkerTool getMarkerTool() {
		return markerTool;
	}
	
	public void onEnable() {
		
		try {
			if (Class.forName("org.spigotmc.SpigotConfig") == null) {
				Logger.log(console_prefix + "It seems like you are not using spigot. To run this plugin you need spigot!");
				Bukkit.getPluginManager().disablePlugin(this);
			}
		} catch (ClassNotFoundException e) {
			Logger.log(console_prefix + "It seems like you are not using spigot. To run this plugin you need spigot!");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		//Prepare config.yml
		getConfig().options().copyDefaults(true);
		saveConfig();
		gameworld = getConfig().getString("gameworld");
		presetworld = getConfig().getString("presetworld");
		
		//Creating the Gameworld
		try {
			Logger.log(console_prefix + "Creating the gameworld '" + gameworld + "' ...");
			Bukkit.createWorld(new WorldCreator(gameworld).generator(new EmptyRoomGenerator(false)));
			Logger.log(console_prefix + "Successfully created the gameworld '" + gameworld + "'.");
		} catch (Exception e) {
			Logger.log(console_prefix + "Error creating the gameworld ...");
			Logger.log(console_prefix + "Without this world this plugin wont work. Disabling ...");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		//Creating the Preset world
		try {
			Logger.log(console_prefix + "Creating/loading the presetworld '" + presetworld + "' ...");
			Bukkit.createWorld(new WorldCreator(presetworld).generator(new EmptyRoomGenerator(true)));
			Logger.log(console_prefix + "Successfully created the presetworld '" + presetworld + "'.");
		} catch (Exception e) {
			Logger.log(console_prefix + "Error creating the presetworld ...");
			Logger.log(console_prefix + "Without this world this plugin wont work. Disabling ...");
			Bukkit.getPluginManager().disablePlugin(this);
		}
		
		//Initialize Management Objects
		this.clusterManager = new ClusterManager(this);
		this.clusterManager.loadClusters();
		this.teamManager = new TeamManager(this);
		this.messageFetcher = new MessageFetcher(this);
		prefix = this.getMessageFetcher().getMessage("prefix", false);
		
		//Set Command Executers
		getCommand("bw").setExecutor(new MainCommand(this));
		
		//Initialize Tools
		this.markerTool = new MarkerTool(this);
		
		//Register Listeners
		Bukkit.getPluginManager().registerEvents(new CreatureSpawnEventListener(this), this);
	}
	
	public void onDisable() {
		//Delete the gameworld to reset it
		try {
			Logger.log(console_prefix + "Deleting the gameworld '" + gameworld + "' to reset it.");
			World delete = Bukkit.getWorld(gameworld);
			Bukkit.unloadWorld(delete, false);
			File deleteFolder = delete.getWorldFolder();
			deleteWorld(deleteFolder);
			Logger.log(console_prefix + "Successfully deleted the gameworld '" + gameworld + "'.");
		} catch (Exception e) {
			Logger.log(console_prefix + "Failed to delete the gameworld '" + gameworld + "'.");
		}
	}
	
	public void reloadAll() {
		Logger.log(console_prefix + "Reloading config.yml");
		this.reloadConfig();
		Logger.log(console_prefix + "Reloading messages.yml");
		this.getMessageFetcher().reloadMessageFile();
		prefix = this.getMessageFetcher().getMessage("prefix", false);
	}
	
	
	//Register the World-Generator:
	public ChunkGenerator getDefaultWorldGenerator(String worldName, String id)
	{
		return new EmptyRoomGenerator(true);
	}
	
	public static boolean deleteWorld(File path) {
	      if(path.exists()) {
	          File files[] = path.listFiles();
	          for(int i=0; i<files.length; i++) {
	              if(files[i].isDirectory()) {
	                  deleteWorld(files[i]);
	              } else {
	                  files[i].delete();
	              }
	          }
	      }
	      return(path.delete());
	}
}

package de.melays.bwunlimited;

import java.io.File;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.plugin.java.JavaPlugin;

import de.melays.bwunlimited.challenges.GroupManager;
import de.melays.bwunlimited.commands.MainCommand;
import de.melays.bwunlimited.commands.groups.GroupCommand;
import de.melays.bwunlimited.game.ItemManager;
import de.melays.bwunlimited.game.arenas.ArenaManager;
import de.melays.bwunlimited.game.arenas.settings.SettingsManager;
import de.melays.bwunlimited.game.lobby.ArenaSelector;
import de.melays.bwunlimited.game.lobby.LobbyManager;
import de.melays.bwunlimited.game.lobby.TemplateSignManager;
import de.melays.bwunlimited.listeners.BlockBreakEventListener;
import de.melays.bwunlimited.listeners.BlockPlaceEventListener;
import de.melays.bwunlimited.listeners.CraftItemEventListener;
import de.melays.bwunlimited.listeners.CreatureSpawnEventListener;
import de.melays.bwunlimited.listeners.EntityDamageByEntityEventListener;
import de.melays.bwunlimited.listeners.EntityDamageEventListener;
import de.melays.bwunlimited.listeners.FoodLevelChangeEventListener;
import de.melays.bwunlimited.listeners.InventoryClickEventListener;
import de.melays.bwunlimited.listeners.PlayerDropItemEventListener;
import de.melays.bwunlimited.listeners.PlayerInteractEventListener;
import de.melays.bwunlimited.listeners.PlayerJoinEventListener;
import de.melays.bwunlimited.listeners.PlayerMoveEventListener;
import de.melays.bwunlimited.listeners.PlayerPickupItemEventListener;
import de.melays.bwunlimited.listeners.PlayerQuitEventListener;
import de.melays.bwunlimited.listeners.SignChangeEventListener;
import de.melays.bwunlimited.log.Logger;
import de.melays.bwunlimited.map_manager.ClusterManager;
import de.melays.bwunlimited.messages.MessageFetcher;
import de.melays.bwunlimited.multiworld.EmptyRoomGenerator;
import de.melays.bwunlimited.shop_old.BWShop;
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
	
	LobbyManager lobbyManager;
	
	public LobbyManager getLobbyManager() {
		return lobbyManager;
	}
	
	ItemManager itemManager;
	
	public ItemManager getItemManager() {
		return itemManager;
	}
	
	ArenaManager arenaManager;
	
	public ArenaManager getArenaManager() {
		return arenaManager;
	}
	
	SettingsManager settingsManager;
	
	public SettingsManager getSettingsManager() {
		return settingsManager;
	}
	
	TemplateSignManager templateSignManager;
	
	public TemplateSignManager getTemplateSignManager() {
		return templateSignManager;
	}
	
	GroupManager groupManager;
	
	public GroupManager getGroupManager() {
		return groupManager;
	}
	
	ArenaSelector arenaSelector;
	
	public ArenaSelector getArenaSelector() {
		return arenaSelector;
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
		this.settingsManager = new SettingsManager(this);
		this.teamManager = new TeamManager(this);
		this.itemManager = new ItemManager(this);
		this.clusterManager = new ClusterManager(this);
		this.clusterManager.loadClusters();
		this.lobbyManager = new LobbyManager(this);
		this.arenaManager = new ArenaManager(this);
		this.templateSignManager = new TemplateSignManager(this);
		this.groupManager = new GroupManager(this);
		this.arenaSelector = new ArenaSelector(this);
		this.messageFetcher = new MessageFetcher(this);
		prefix = this.getMessageFetcher().getMessage("prefix", false) + " ";
		
		//Set Command Executers
		getCommand("bw").setExecutor(new MainCommand(this));
		if (getConfig().getBoolean("group_command"))
			getCommand("group").setExecutor(new GroupCommand(this));
		
		//Initialize Tools
		this.markerTool = new MarkerTool(this);
		
		//Register Listeners
		Bukkit.getPluginManager().registerEvents(new CreatureSpawnEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerJoinEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockBreakEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new BlockPlaceEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new InventoryClickEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerDropItemEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerPickupItemEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerInteractEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new EntityDamageByEntityEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new EntityDamageEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerQuitEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new PlayerMoveEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new SignChangeEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new FoodLevelChangeEventListener(this), this);
		Bukkit.getPluginManager().registerEvents(new CraftItemEventListener(this), this);

		for (Player p : Bukkit.getOnlinePlayers()) {
			this.getLobbyManager().toLobby(p);
		}
		
		//OLD BW SHOP ONLY FOR TESTS!
		new BWShop(this);
	}
	
	public void onDisable() {
		this.getArenaManager().cancleAll();
		//Fix Visibility
		for (Player p : Bukkit.getOnlinePlayers()) {
			p.setAllowFlight(false);
			p.setFlying(false);
			for (Player player : Bukkit.getOnlinePlayers()) {
				p.showPlayer(player);
			}
		}
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
		this.getMessageFetcher().reloadFile();
		Logger.log(console_prefix + "Reloading settings.yml");
		this.getSettingsManager().reloadFile();
		prefix = this.getMessageFetcher().getMessage("prefix", false) + " ";
	}
	
	//Get Instance
	public static Main getInstance() {
		return (Main) Bukkit.getPluginManager().getPlugin("BedwarsUnlimited");
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
	
	public boolean canOperateInLobby (Player p) {
		if (p.hasPermission("bwunlimited.lobby.edit") && p.getGameMode().equals(GameMode.CREATIVE)) {
			return true;
		}
		return false;
	}
}

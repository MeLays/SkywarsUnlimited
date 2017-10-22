package de.melays.bwunlimited.game.lobby;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.scheduler.BukkitRunnable;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.challenges.Group;
import de.melays.bwunlimited.colortab.ColorTabAPI;
import de.melays.bwunlimited.game.PlayerTools;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.ClusterTools;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;
import de.melays.bwunlimited.teams.Team;

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
		main.getArenaSelector().setupPlayer(p);
		if (main.getConfig().getBoolean("lobby.challenger.enabled")) {
			ItemStack stack = main.getItemManager().getItem("lobby.challenger");
			ItemMeta meta = stack.getItemMeta();
			meta.spigot().setUnbreakable(true);
			meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);
			stack.setItemMeta(meta);
			p.getInventory().setItem(main.getConfig().getInt("lobby.challenger.slot"), stack);	
		}		
		if (main.getConfig().getBoolean("lobby.gamelist.enabled"))
			p.getInventory().setItem(main.getConfig().getInt("lobby.gamelist.slot"), main.getItemManager().getItem("lobby.gamelist"));
		if (main.getConfig().getBoolean("lobby.leave.enabled"))
			p.getInventory().setItem(main.getConfig().getInt("lobby.leave.slot"), main.getItemManager().getItem("lobby.leave"));
		if (main.getConfig().getBoolean("lobby.leave.enabled")) {
			updateGroupItem(p);
		}
		updateLobbyTab();
	}
	
	@SuppressWarnings("deprecation")
	public void updateGroupItem(Player p) {
		ItemStack stack = main.getItemManager().getItem("lobby.group");
		ItemMeta dmeta = stack.getItemMeta();
		dmeta.setDisplayName(dmeta.getDisplayName().replaceAll("%player%", main.getGroupManager().getGroup(p).getLeader().getName()));
		stack.setItemMeta(dmeta);
		if (stack.getType() == Material.SKULL_ITEM && stack.getData().getData() == (byte) 3) {
			SkullMeta meta = (SkullMeta) stack.getItemMeta();
			meta.setOwner(main.getGroupManager().getGroup(p).getLeader().getName());
			stack.setItemMeta(meta);
		}
		p.getInventory().setItem(main.getConfig().getInt("lobby.group.slot"), stack);	
	}
	
	public void updateLobbyTab() {
		String prefix = main.getSettingsManager().getFile().getString("lobby.tab.prefix");
		String suffix = main.getSettingsManager().getFile().getString("lobby.tab.suffix");
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (!main.getArenaManager().isInGame(p)) {
				ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
				String t_prefix = prefix.replaceAll("%player_prefix%", main.getChatHook().getPrefix(p));
				t_prefix = t_prefix.replaceAll("%player_suffix%", main.getChatHook().getSuffix(p));
				String t_suffix = suffix.replaceAll("%player_prefix%", main.getChatHook().getPrefix(p));
				t_suffix = t_suffix.replaceAll("%player_suffix%", main.getChatHook().getSuffix(p));
				ColorTabAPI.setTabStyle(p, main.c(t_prefix), main.c(t_suffix), 1, Bukkit.getOnlinePlayers());
			}
		}
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
	
	public ArrayList<Cluster> getChallengerClusters() {
		ArrayList<Cluster> r = new ArrayList<Cluster>();
		ArrayList<String> temp = (ArrayList<String>) getLobbyFile().getStringList("challenger.clusters");
		for (String s : temp) {
			try {
				Cluster cluster = main.getClusterManager().getCluster(s);
				if (cluster.getClusterMeta().getTeams().size() == 2) {
					r.add(cluster);	
				}
			} catch (UnknownClusterException e) {

			}
		}
		return r;
	}
	
	public LinkedHashMap<Integer , ArrayList<Cluster>> getSuitableArenas(Group group) {
		LinkedHashMap<Integer , ArrayList<Cluster>> r = new LinkedHashMap<Integer , ArrayList<Cluster>>();
		for (Cluster cluster : getChallengerClusters()) {
			int max = 0;
			for (Team t : cluster.getClusterMeta().getTeams()) {
				max += t.max;
			}
			if (max >= group.getPlayers().size()*2) {
				if (!r.containsKey(max))
					r.put(max, new ArrayList<Cluster>());
				ArrayList<Cluster> temp = r.get(max);
				temp.add(cluster);
				r.put(max, temp);
			}
		}
		return r;
	}
	
	public boolean isSuitable (Group group , Cluster cluster) {
		if (cluster.getClusterMeta().getTeams().size() != 2) return false;
		int max = 0;
		for (Team t : cluster.getClusterMeta().getTeams()) {
			max += t.max;
		}
		if (max < group.getPlayers().size()*2) {
			return false;
		}
		return true;
	}
	
	//Cooldown
	public ArrayList<Player> challenge_cooldown = new ArrayList<Player>();
	public void setChallengeCooldown (Player p , int seconds) {
		challenge_cooldown.add(p);
		new BukkitRunnable() {

			@Override
			public void run() {
				challenge_cooldown.remove(p);
			}
			
		}.runTaskLater(main, 20*seconds);
	}
	public boolean isCooldowned(Player p) {
		return challenge_cooldown.contains(p);
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

package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.colortab.ColorTabAPI;
import de.melays.bwunlimited.entitys.Merchant;
import de.melays.bwunlimited.game.PlayerTools;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.game.arenas.settings.TeamPackage;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.ClusterHandler;
import de.melays.bwunlimited.map_manager.FineRelativeLocation;
import de.melays.bwunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;
import de.melays.bwunlimited.map_manager.meta.ItemSpawner;
import de.melays.bwunlimited.teams.error.UnknownTeamException;

public class Arena {
	
	public Cluster cluster;
	public Location relative;
	
	public Settings settings;
	
	public ArenaState state = ArenaState.LOBBY;
	
	Main main;
	
	public ArenaLobby arenaLobby;
	public ArenaTeamManager teamManager;
	public ClusterHandler clusterHandler;
	public DeathManager deathManager;
	public ArenaScoreboard scoreBoard;
	public BlockManager blockManager;
	
	//PlayerLists
	public ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Player> specs = new ArrayList<Player>();
	
	//ItemSpawner
	public ArrayList<ItemSpawner> itemSpawners = new ArrayList<ItemSpawner>();
	
	public Arena (Main main , Cluster cluster , Location relative , Settings settings) throws ClusterAvailabilityException, UnknownClusterException {
		this.main = main;
		this.cluster = cluster;
		this.relative = relative;
		this.settings = settings;
		this.settings.setArena(this);
		
		//Setting up instances
		this.arenaLobby = new ArenaLobby(this);
		this.teamManager = new ArenaTeamManager(this);
		this.deathManager = new DeathManager(this);
		this.scoreBoard = new ArenaScoreboard(this);
		this.blockManager = new BlockManager(this);
		
		//Starting the generation of the Cluster
		clusterHandler = main.getClusterManager().getNewHandler(cluster.name);
		clusterHandler.generate(relative);
		
		itemSpawners = cluster.getClusterMeta().getItemSpawners();
	}
	
	//Playermanagement
	
	public ArrayList<Player> getAllPlayers() {
		ArrayList<Player> r = new ArrayList<Player>();
		r.addAll(players);
		for (ArenaTeam team: this.teamManager.getTeams()) {
			for (Player p : team.players) {
				if (!r.contains(p))
					r.add(p);	
			}
		}
		return r;
	}
	
	public ArrayList<Player> getAll() {
		ArrayList<Player> r = new ArrayList<Player>();
		r.addAll(getAllPlayers());
		r.addAll(specs);
		return r;
	}
	
	public boolean addTeamPackage(TeamPackage teamPackage) {
		if (this.getAllPlayers().size() + teamPackage.size() <= settings.max_players) {
			for (String s : teamPackage.getTeams()) {
				for (Player p : teamPackage.getPlayers(s)) {
					join(p , s);
				}
			}
			return true;
		}
		return false;
	}
	
	public boolean addPlayers(ArrayList<Player> players) {
		if (this.getAllPlayers().size() + players.size() <= settings.max_players) {
			for (Player p : players) {
				join (p);
			}
			return true;
		}
		return false;
	}
	
	public boolean addPlayer(Player p) {
		if (settings.allow_join && this.getAllPlayers().size() < settings.max_players && !this.getAllPlayers().contains(p)) {
			join (p);
			return true;
		}
		else {
			return false;
		}
	}
	
	private void join (Player p) {
		if (this.state == ArenaState.LOBBY) {
			players.add(p);
			arenaLobby.updatePlayer(p);
		}
		updateTab();
	}
	
	private void join (Player p , String team) {
		if (this.state == ArenaState.LOBBY) {
			join (p);
			this.teamManager.getTeam(team).addPlayer(p);
			this.sendMessage(main.getMessageFetcher().getMessage("game.join", true).replaceAll("%player%", p.getName()));
		}
	}
	
	public void leave (Player p , boolean silent) {
		if (state == ArenaState.LOBBY) {
			this.sendMessage(main.getMessageFetcher().getMessage("game.leave", true).replaceAll("%player%", p.getName()));
		}
		else if(state == ArenaState.INGAME){
			this.sendMessage(main.getMessageFetcher().getMessage("game.leave_ingame", true).replaceAll("%player%", p.getName()));
		}
		if (teamManager.findPlayer(p) != null) {
			teamManager.findPlayer(p).removePlayer(p);
		}
		if (players.contains(p)) {
			players.remove(p);
		}
		if (specs.contains(p)) {
			specs.remove(p);
		}
		PlayerTools.resetPlayer(p);
		p.teleport(main.getLobbyManager().getLobbyLocation());
		updateTab();
	}
	
	public void leave(Player p) {
		leave(p , false);
	}
	
	public void removeAll() {
		for (Player p : getAll()) {
			if (teamManager.findPlayer(p) != null) {
				teamManager.findPlayer(p).removePlayer(p);
			}
			if (players.contains(p)) {
				players.remove(p);
			}
			if (specs.contains(p)) {
				specs.remove(p);
			}
			PlayerTools.resetPlayer(p);
			p.teleport(main.getLobbyManager().getLobbyLocation());
			resetComplete(p);
		}
	}
	
	public void resetComplete (Player p) {
		p.setScoreboard(Bukkit.getScoreboardManager().getNewScoreboard());
		ColorTabAPI.clearTabStyle(p, Bukkit.getOnlinePlayers());
	}
	
	//Message management
	
	public void sendMessage (String message) {
		for (Player p : this.getAll()) {
			p.sendMessage(message);
		}
	}
	
	public void sendColoredMessage (String message) {
		for (Player p : this.getAll()) {
			p.sendMessage(main.c(message));
		}
	}
	
	//Visibility management
	
	public void updateTab() {
		updateColors();
		updateVisibility();
	}
	
	public void updateColors() {
		for (Player p : this.getAllPlayers()) {
			ArenaTeam team = this.teamManager.findPlayer(p);
			if (team != null) {
				String prefix = main.c(main.getSettingsManager().getFile().getString("game.tab.format_prefix")).replaceAll("%color%", team.team.Color.toChatColor().toString()).replaceAll("%display%", team.team.display); 
				String suffix = main.c(main.getSettingsManager().getFile().getString("game.tab.format_suffix")).replaceAll("%color%", team.team.Color.toChatColor().toString()).replaceAll("%display%", team.team.display);
				ColorTabAPI.setTabStyle(p, prefix, suffix, 100+this.teamManager.getTeams().indexOf(team), this.getAll());
			}
			else {
				String prefix = main.c(main.getSettingsManager().getFile().getString("game.tab.unknown_format_prefix"));
				String suffix = main.c(main.getSettingsManager().getFile().getString("game.tab.unknown_format_suffix"));
				ColorTabAPI.setTabStyle(p, prefix, suffix, 999 , this.getAll());
			}
		}
	}
	
	public void updateVisibility() {
		for (Player p : Bukkit.getOnlinePlayers()) {
			for (Player player : getAll()) {
				p.hidePlayer(player);
			}
		}
		for (Player p : getAll()) {
			for (Player player : getAll()) {
				p.showPlayer(player);
			}
		}
	}
	
	//Gamestart methods
	
	public void callLobbyEnd() {
		state = ArenaState.INGAME;
		for (ItemSpawner spawner : itemSpawners) {
			spawner.startGenerating(relative);
		}
		//Auto Team add
		for (Player p : new ArrayList<Player>(players)) {
			PlayerTools.resetPlayer(p);
			if (this.teamManager.findPlayer(p) == null) {
				autoTeam(p);
			}
			respawnPlayer(p);
			players.remove(p);
		}
		this.updateTab();
		scoreBoard.create();
		//Start loop
		loop();
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {

			@Override
			public void run() {
				//Create Villagers
				try {
					for (FineRelativeLocation loc : cluster.getClusterMeta().getShops()) {
						Merchant.spawn(loc.toLocation(relative), main.c(main.getSettingsManager().getFile().getString("shop.display")) , EntityType.valueOf(main.getSettingsManager().getFile().getString("shop.type").toUpperCase()));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			
		}, 10);
	}
	
	public void autoTeam (Player p) {
		for (ArenaTeam t : this.teamManager.getTeams()) {
			if (t.addPlayer(p)) {
				return;
			}
		}
	}
	
	//Game methods
	
	public void endGame() {
		endGame(null);
	}
	
	public void endGame(ArenaTeam winner) {
		state = ArenaState.ENDING;
		for (Player p : getAll()) {
			PlayerTools.resetPlayer(p);
			p.teleport(main.getLobbyManager().getGameLobbyLocation());
		}
		if (winner != null) {
			
		}
	}
	
	public void respawnPlayer(Player p) {
		ArenaTeam team = this.teamManager.findPlayer(p);
		if (team != null) {
			try {
				Location loc = cluster.getClusterMeta().getTeamSpawn(team.team.name).toLocation(this.relative);
				p.teleport(loc);
				p.setGameMode(GameMode.SURVIVAL);
				PlayerTools.resetPlayer(p);
			} catch (UnknownTeamException e) {

			}
		}
	}
	
	public void BedDestroyed(ArenaTeam team , Player p) {
		team.bed = false;
		String msg = main.getMessageFetcher().getMessage("game.bed.destroy_chat", true);
		ArenaTeam pteam = teamManager.findPlayer(p);
		msg = msg.replaceAll("%color%", pteam.team.Color.toChatColor().toString());
		msg = msg.replaceAll("%display%", pteam.team.display);
		msg = msg.replaceAll("%player%", p.getName());
		msg = msg.replaceAll("%destroyed_color%", team.team.Color.toChatColor().toString());
		msg = msg.replaceAll("%destroyed_display%", team.team.display);
		sendMessage(msg);
	}
	
	int scheduler;
	
	public void loop() {
		scheduler = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {

			@Override
			public void run() {
				
				scoreBoard.update();
				
			}
			
		}, 20, 20);
	}
	
	
}

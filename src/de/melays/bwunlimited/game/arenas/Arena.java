package de.melays.bwunlimited.game.arenas;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.PlayerTools;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.game.arenas.settings.TeamPackage;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.ClusterHandler;
import de.melays.bwunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;

public class Arena {
	
	public Cluster cluster;
	public Location relative;
	
	public Settings settings;
	
	public ArenaState state = ArenaState.LOBBY;
	
	Main main;
	
	public ArenaLobby arenaLobby;
	public ArenaTeamManager teamManager;
	public ClusterHandler clusterHandler;
	
	//PlayerLists
	public ArrayList<Player> players = new ArrayList<Player>();
	public ArrayList<Player> specs = new ArrayList<Player>();
	
	public Arena (Main main , Cluster cluster , Location relative , Settings settings) throws ClusterAvailabilityException, UnknownClusterException {
		this.main = main;
		this.cluster = cluster;
		this.relative = relative;
		this.settings = settings;
		this.settings.setArena(this);
		
		//Setting up instances
		this.arenaLobby = new ArenaLobby(this);
		this.teamManager = new ArenaTeamManager(this);
		
		//Starting the generation of the Cluster
		clusterHandler = main.getClusterManager().getNewHandler(cluster.name);
		clusterHandler.generate(relative);
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
		}
	}
	
	public void leave (Player p , boolean silent) {
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
	
	//Message management
	
	public void sendMessage (String message) {
		for (Player p : this.getAllPlayers()) {
			p.sendMessage(message);
		}
	}
	
	public void sendColoredMessage (String message) {
		for (Player p : this.getAllPlayers()) {
			p.sendMessage(main.c(message));
		}
	}
	
	//Visibility management
	
	public void updateTab() {
		updateVisibility();
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
		//Auto Team add
		for (Player p : new ArrayList<Player>(players)) {
			if (this.teamManager.findPlayer(p) == null) {
				for (ArenaTeam t : this.teamManager.getTeams()) {
					if (t.addPlayer(p))
						continue;
				}
			}
			players.remove(p);
		}
	}
	
	
}

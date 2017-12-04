package de.melays.bwunlimited.queue;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.challenges.Group;
import de.melays.bwunlimited.game.arenas.settings.LeaveType;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.game.arenas.settings.TeamPackage;
import de.melays.bwunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;

public class QueueManager {
	
	Main main;
	
	public QueueManager(Main main) {
		this.main = main;
	}
	
	ArrayList<Player> queue_nothing = new ArrayList<Player>();
	ArrayList<Player> queue_default = new ArrayList<Player>();
	ArrayList<Player> queue_own = new ArrayList<Player>();
	
	ArrayList<Group> groups = new ArrayList<Group>();
	
	HashMap<Player , QueuePlayerType> types = new HashMap<Player , QueuePlayerType>();
	
	public boolean isInQueue(Player p) {
		if (this.queue_default.contains(p)) return true;
		if (this.queue_nothing.contains(p)) return true;
		if (this.queue_own.contains(p)) return true;
		return false;
	}
	
	public void addToQueue(Player p , QueuePlayerType type) {
		if (this.isInQueue(p)) return;
		if (type == QueuePlayerType.DEFAULT) {
			this.queue_default.add(p);
		}
		else if (type == QueuePlayerType.OWN) {
			this.queue_own.add(p);
		}
		else if (type == QueuePlayerType.NOTHING) {
			this.queue_nothing.add(p);
		}
		types.put(p, type);
		checkQueue();
	}
	
	public void addGroup(Group g) {
		if (!groups.contains(g)) {
			groups.add(g);
		}
	}
	
	public void checkQueue() {
		for (Player p : new ArrayList<Player>(this.queue_default)) {
			if (main.getArenaManager().searchPlayer(p) != null || !p.isOnline())
				this.queue_default.remove(p);
		}
		for (Player p : new ArrayList<Player>(this.queue_nothing)) {
			if (main.getArenaManager().searchPlayer(p) != null || !p.isOnline())
				this.queue_nothing.remove(p);
		}
		for (Player p : new ArrayList<Player>(this.queue_own)) {
			if (main.getArenaManager().searchPlayer(p) != null || !p.isOnline())
				this.queue_own.remove(p);
		}
		
		
		for (Player p : new ArrayList<Player>(this.queue_default)) {
			if (this.queue_default.contains(p))
				findSuitable(p);
		}
		for (Player p : new ArrayList<Player>(this.queue_nothing)) {
			if (this.queue_default.contains(p))
				findSuitable(p);
		}
		for (Player p : new ArrayList<Player>(this.queue_own)) {
			if (this.queue_default.contains(p))
				findSuitable(p);
		}
	}
	
	public void findSuitable (Group g) {
		ArrayList<Group> temp = new ArrayList<Group>();
		temp.remove(g);
		
	}
	
	public void findSuitable(Player p) {
		QueuePlayerType type = this.types.get(p);
		if (type == QueuePlayerType.DEFAULT) {
			ArrayList<Player> temp = new ArrayList<Player>(this.queue_default);
			temp.addAll(this.queue_nothing);
			temp.remove(p);
			if (temp.size() != 0) {
				startGame(p , temp.get(0));
			}
		}
		if (type == QueuePlayerType.NOTHING) {
			ArrayList<Player> temp = new ArrayList<Player>(this.queue_nothing);
			temp.addAll(this.queue_default);
			temp.addAll(this.queue_own);
			temp.remove(p);
			if (temp.size() != 0) {
				startGame(temp.get(0) , p);
			}
		}
		if (type == QueuePlayerType.OWN) {
			ArrayList<Player> temp = new ArrayList<Player>(this.queue_nothing);
			temp.remove(p);
			if (temp.size() != 0) {
				startGame(p , temp.get(0));
			}
		}
	}
	
	public void removePlayer (Player p) {
		this.queue_default.remove(p);
		this.queue_nothing.remove(p);
		this.queue_own.remove(p);
	}
	
	public void startGame (Player main, Player second) {
		removePlayer(main);
		removePlayer(second);
		QueuePlayerType type = this.types.get(main);
		Settings play = new Settings(this.main);
		if (type == QueuePlayerType.OWN) {
			play = this.main.getLobbyManager().settings.get(main).settings;
		}
		this.main.getArenaSelector().setupPlayer(main);
		HashMap<String , ArrayList<Player>> teams = new HashMap<String , ArrayList<Player>>();
		ArrayList<Player> player1 = new ArrayList<Player>(); player1.add(main);
		ArrayList<Player> player2 = new ArrayList<Player>(); player2.add(second);
		teams.put(this.main.getArenaSelector().selected.get(main).getClusterMeta().getTeams().get(0).name, player1);
		teams.put(this.main.getArenaSelector().selected.get(main).getClusterMeta().getTeams().get(1).name, player2);
		TeamPackage teampackage = new TeamPackage(teams);
		
		play.lobby_leave = LeaveType.ABORT;
		play.allow_join = false;
		play.fixed_teams = true;
		play.min_lobby_countdown = this.main.getConfig().getInt("challenger.lobby_time");
		
		try {
			this.main.getArenaManager().startGame(this.main.getArenaSelector().selected.get(main), play, "challenge", teampackage);
		} catch (ClusterAvailabilityException e) {

		} catch (UnknownClusterException e) {

		}
	}
	
	public void removeFromQueue(Group g) {
		groups.remove(g);
	}
	
	public void startGame (Group g1 , Group g2) {
		groups.remove(g1);
		groups.remove(g2);
		Settings play = new Settings(this.main);
		this.main.getArenaSelector().selected.remove(g1.getLeader());
		this.main.getArenaSelector().setupPlayer(g1.getLeader());
		HashMap<String , ArrayList<Player>> teams = new HashMap<String , ArrayList<Player>>();
		teams.put(this.main.getArenaSelector().selected.get(g1.getLeader()).getClusterMeta().getTeams().get(0).name, g1.getPlayers());
		teams.put(this.main.getArenaSelector().selected.get(g1.getLeader()).getClusterMeta().getTeams().get(1).name, g2.getPlayers());
		TeamPackage teampackage = new TeamPackage(teams);
		
		play.lobby_leave = LeaveType.ABORT;
		play.allow_join = false;
		play.fixed_teams = true;
		play.min_lobby_countdown = this.main.getConfig().getInt("challenger.lobby_time");
		
		try {
			this.main.getArenaManager().startGame(this.main.getArenaSelector().selected.get(g1.getLeader()), play, "challenge", teampackage);
		} catch (ClusterAvailabilityException e) {

		} catch (UnknownClusterException e) {

		}
	}
}

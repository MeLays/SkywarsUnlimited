package de.melays.bwunlimited.challenges;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.game.arenas.settings.LeaveType;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.game.arenas.settings.TeamPackage;
import de.melays.bwunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;

public class Group {
	
	Player leader;
	ArrayList<Player> players = new ArrayList<Player>();
	
	GroupManager groupManager;
	
	public Group (GroupManager groupManager, Player leader) {
		this.groupManager = groupManager;
		this.leader = leader;
	}
	
	public boolean hasPlayer (Player p) {
		return players.contains(p);
	}
	
	public ArrayList<Player> getPlayers() {
		ArrayList<Player> pls = new ArrayList<Player>(players);
		pls.add(leader);
		return pls;
	}
	
	@SuppressWarnings("unchecked")
	public ArrayList<Player> getMembers() {
		return (ArrayList<Player>) players.clone();
	}
	
	public Player getLeader () {
		return leader;
	}
	
	ArrayList<Player> invited = new ArrayList<Player>();
	
	public void sendMessage(String msg) {
		for (Player p : getPlayers()) {
			p.sendMessage(msg);
		}
	}
	
	//Challenges
	
	ArrayList<Challenge> challenges = new ArrayList<Challenge>();
	
	public void challenge(Player hitter , Player hit , Challenge c) {
		if (this.groupManager.getGroup(hitter).getLeader() != hitter) {
			hitter.sendMessage(this.groupManager.main.getMessageFetcher().getMessage("group.not_leader", true).replaceAll("%player%", hit.getName()));
			return;
		}
		Group inviter = this.groupManager.getGroup(hitter);
		if (this.getPlayers().size() != inviter.getPlayers().size()) {
			hitter.sendMessage(this.groupManager.main.getMessageFetcher().getMessage("group.not_same_size", true).replaceAll("%player%", hit.getName()));
			return;
		}
		if (hit != leader) {
			hitter.sendMessage(this.groupManager.main.getMessageFetcher().getMessage("group.this_not_leader", true).replaceAll("%player%", hit.getName()));
			return;
		}
		this.groupManager.main.getArenaSelector().setupPlayer(inviter.getLeader());
		removeChallenge(inviter);
		challenges.add(c);
		for (String s : this.groupManager.main.getMessageFetcher().getMessageFetcher().getStringList("group.challenge.challenged")) {
			s = this.groupManager.main.c(s);
			s = s.replaceAll("%prefix%", this.groupManager.main.getMessageFetcher().getMessage("prefix", false));
			s = s.replaceAll("%challenger%", inviter.getLeader().getName());
			s = s.replaceAll("%cluster%", c.cluster.getDisplayName());
			this.sendMessage(s);
		}
		hitter.sendMessage(this.groupManager.main.getMessageFetcher().getMessage("group.challenge.sent", true).replaceAll("%player%", hit.getName()));
		SoundDebugger.playSound(hitter, "LEVEL_UP", "ENTITY_PLAYER_LEVELUP");
		SoundDebugger.playSound(this.leader, "LEVEL_UP", "ENTITY_PLAYER_LEVELUP");
	}
	
	public void removeChallenge(Group g) {
		for (Challenge c : this.challenges) {
			if (c.from == g) {
				this.challenges.remove(c);
				return;
			}
		}
	}
	
	public void removeAllChallenges() {
		challenges = new ArrayList<Challenge>();
	}
	
	public void acceptChallenge (Challenge c) {
		Group inviter = c.from;
		if (this.getPlayers().size() != inviter.getPlayers().size()) {
			leader.sendMessage(this.groupManager.main.getMessageFetcher().getMessage("group.not_same_size", true).replaceAll("%player%", inviter.getLeader().getName()));
			removeChallenge(inviter);
			return;
		}
		Settings settings = new Settings(this.groupManager.main);
		settings.lobby_leave = LeaveType.ABORT;
		settings.allow_join = false;
		settings.fixed_teams = true;
		settings.min_lobby_countdown = this.groupManager.main.getConfig().getInt("challenger.lobby_time");
		HashMap<String , ArrayList<Player>> teams = new HashMap<String , ArrayList<Player>>();
		teams.put(c.cluster.getClusterMeta().getTeams().get(0).name, c.from.getPlayers());
		teams.put(c.cluster.getClusterMeta().getTeams().get(1).name, getPlayers());
		TeamPackage teampackage = new TeamPackage(teams);
		try {
			this.groupManager.main.getArenaManager().startGame(c.cluster, settings, "challenge", teampackage);
			removeAllChallenges();
			inviter.removeAllChallenges();
		} catch (ClusterAvailabilityException e) {

		} catch (UnknownClusterException e) {

		}
	}
	
	public boolean hasChallenge (Group g) {
		for (Challenge c : this.challenges) {
			if (c.from == g) {
				return true;
			}
		}
		return false;
	}
	
	public Challenge getChallenge(Group g) {
		for (Challenge c : this.challenges) {
			if (c.from == g) {
				return c;
			}
		}
		return null;
	}
	
	//Command methods
	public boolean invite (Player p) {
		if (this.getPlayers().size() >= groupManager.getMaxPlayers(this.getLeader())) {
			return false;
		}
		if (!invited.contains(p)) {
			invited.add(p);
			p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.invite", true).replaceAll("%player%", leader.getName()));
			TextComponent message = new TextComponent(TextComponent.fromLegacyText(groupManager.main.getMessageFetcher().getMessage("prefix", false)));
			message.addExtra(" ");
			
			TextComponent accept = new TextComponent(TextComponent.fromLegacyText(groupManager.main.getMessageFetcher().getMessage("group.buttons.accept", false)));
			accept.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/group accept "+leader.getName() ));
			message.addExtra(accept);
			
			message.addExtra(" ");
			
			TextComponent deny = new TextComponent(TextComponent.fromLegacyText(groupManager.main.getMessageFetcher().getMessage("group.buttons.deny", false)));
			deny.setClickEvent(new ClickEvent( ClickEvent.Action.RUN_COMMAND, "/group deny "+leader.getName() ));
			message.addExtra(deny);
			p.spigot().sendMessage(message);
			return true;
		}
		return false;
	}
	
	public boolean accept(Player p) {
		if (invited.contains(p)) {
			if (this.getPlayers().size() >= groupManager.getMaxPlayers(this.getLeader())) {
				p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.full_join", true).replaceAll("%player%", p.getName()));
				return false;
			}
			invited.remove(p);
			if (groupManager.getGroup(p).getPlayers().size() != 1) {
				groupManager.getGroup(p).leave(p);
			}
			groupManager.getGroup(p).players.remove(p);
			groupManager.players.remove(p);
			players.add(p);
			removeAllChallenges();
			sendMessage(groupManager.main.getMessageFetcher().getMessage("group.join", true).replaceAll("%player%", p.getName()));
			if (!groupManager.main.getArenaManager().isInGame(p)) {
				this.groupManager.main.getLobbyManager().updateGroupItem(p);
			}
			Bukkit.getPluginManager().callEvent(new GroupUpdateEvent (this));
			return true;
		}
		p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.join_failed", true).replaceAll("%player%", p.getName()));
		return false;
	}
	
	public boolean deny(Player p) {
		if (invited.contains(p)) {
			invited.remove(p);
			sendMessage(groupManager.main.getMessageFetcher().getMessage("group.deny", true).replaceAll("%player%", p.getName()));
			p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.deny_player", true).replaceAll("%player%", leader.getName()));
			return true;
		}
		p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.not_invited", true).replaceAll("%player%", p.getName()));
		return false;
	}
	
	public void leave (Player p) {
		if (p == leader) {
			if (players.size() != 0) {
				setLeader(players.get(0));
			}
			else {
				p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.cant_leave", true));
				return;
			}
		}
		sendMessage(groupManager.main.getMessageFetcher().getMessage("group.leave", true).replaceAll("%player%", p.getName()));
		players.remove(p);
		removeAllChallenges();
		if (!groupManager.main.getArenaManager().isInGame(p)) {
			this.groupManager.main.getLobbyManager().updateGroupItem(p);
		}
		Bukkit.getPluginManager().callEvent(new GroupUpdateEvent (this));
	}
	
	public void kick (Player p) {
		if (p == leader) {
			p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.self_interact", true));
			return;
		}
		if (players.contains(p)) {
			sendMessage(groupManager.main.getMessageFetcher().getMessage("group.kick", true).replaceAll("%player%", p.getName()));
			players.remove(p);
			removeAllChallenges();
			Bukkit.getPluginManager().callEvent(new GroupUpdateEvent (this));
		}
		else {
			leader.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.not_in_group", true).replaceAll("%player%", p.getName()));
		}
		if (!groupManager.main.getArenaManager().isInGame(p)) {
			this.groupManager.main.getLobbyManager().updateGroupItem(p);
		}
	}
	
	public void setLeader(Player p) {
		if (!players.contains(p)) {
			p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.not_in_group", true).replaceAll("%player%", p.getName()));
			return;
		}
		else {
			removeAllChallenges();
			players.add(leader);
			players.remove(p);
			this.groupManager.players.remove(leader);
			leader = p;
			this.groupManager.players.put(p, this);
			sendMessage(groupManager.main.getMessageFetcher().getMessage("group.new_leader", true).replaceAll("%player%", p.getName()));
		}
		for (Player pp : this.getPlayers()) {
			if (!groupManager.main.getArenaManager().isInGame(pp)) {
				this.groupManager.main.getLobbyManager().updateGroupItem(pp);
			}
		}
		Bukkit.getPluginManager().callEvent(new GroupUpdateEvent (this));
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.challenges;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.melays.swunlimited.game.SoundDebugger;
import de.melays.swunlimited.game.arenas.settings.LeaveType;
import de.melays.swunlimited.game.arenas.settings.Settings;
import de.melays.swunlimited.game.arenas.settings.TeamPackage;
import de.melays.swunlimited.map_manager.error.ClusterAvailabilityException;
import de.melays.swunlimited.map_manager.error.UnknownClusterException;
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

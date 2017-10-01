package de.melays.bwunlimited.challenges;

import java.util.ArrayList;

import org.bukkit.entity.Player;

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
	
	public boolean invite (Player p) {
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
			groupManager.getGroup(p).players.remove(p);
			groupManager.players.remove(p);
			players.add(p);
			sendMessage(groupManager.main.getMessageFetcher().getMessage("group.join", true).replaceAll("%player%", p.getName()));
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
	}
	
	public void kick (Player p) {
		if (p == leader) {
			p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.self_interact", true));
			return;
		}
		if (players.contains(p)) {
			sendMessage(groupManager.main.getMessageFetcher().getMessage("group.kick", true).replaceAll("%player%", p.getName()));
			players.remove(p);
		}
		else {
			leader.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.not_in_group", true).replaceAll("%player%", p.getName()));
		}
	}
	
	public void setLeader(Player p) {
		if (!players.contains(p)) {
			p.sendMessage(groupManager.main.getMessageFetcher().getMessage("group.not_in_group", true).replaceAll("%player%", p.getName()));
		}
		else {
			players.add(leader);
			players.remove(p);
			this.groupManager.players.remove(leader);
			leader = p;
			this.groupManager.players.put(p, this);
			sendMessage(groupManager.main.getMessageFetcher().getMessage("group.new_leader", true).replaceAll("%player%", p.getName()));
		}
	}
	
}

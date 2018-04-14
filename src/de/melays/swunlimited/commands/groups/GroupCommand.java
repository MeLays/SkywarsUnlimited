/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.commands.groups;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.challenges.Group;

public class GroupCommand implements CommandExecutor {
	
	Main main;
	
	public GroupCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(main.prefix + "You cant run this command from the console!");
			return true;
		}
		Player p = (Player) sender;
		if (args.length == 0) {
			Group group = main.getGroupManager().getGroup(p);
			p.sendMessage(main.getMessageFetcher().getMessage("group.list.header", true).replaceAll("%player%", group.getLeader().getName()));
			if (group.getMembers().size() == 0) {
				p.sendMessage(main.getMessageFetcher().getMessage("group.list.no_players", true));
			}
			else {
				for (Player member : group.getMembers()) {
					p.sendMessage(main.getMessageFetcher().getMessage("group.list.player", true).replaceAll("%player%", member.getName()));
				}
			}
			p.sendMessage(main.getMessageFetcher().getMessage("group.list.footer", true));
			return true;
		}
		else if (args[0].equalsIgnoreCase("help")) {
			for (String s : main.getMessageFetcher().getMessageFetcher().getStringList("group.help")) {
				p.sendMessage(main.c(s.replace("%prefix%", main.getMessageFetcher().getMessage("prefix", false))));
			}
		}
		else if (args[0].equalsIgnoreCase("invite")) {
			Group group = main.getGroupManager().getGroup(p);
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/group invite <player>"));
				return true;
			}
			if (p == group.getLeader()) {
				if (Bukkit.getServer().getPlayer(args[1]) == null) {
					p.sendMessage(main.getMessageFetcher().getMessage("group.not_online", true).replaceAll("%player%", args[1]));
					return true;
				}
				if (Bukkit.getServer().getPlayer(args[1]) == p) {
					p.sendMessage(main.getMessageFetcher().getMessage("group.self_interact", true));
					return true;
				}
				if (group.getPlayers().contains(Bukkit.getPlayer(args[1]))) {
					p.sendMessage(main.getMessageFetcher().getMessage("group.already_in_group", true).replaceAll("%player%", args[1]));
					return true;
				}
				if (group.invite(Bukkit.getPlayer(args[1])))
					p.sendMessage(main.getMessageFetcher().getMessage("group.invite_sender", true).replaceAll("%player%", args[1]));
				else {
					if (group.getPlayers().size() >= main.getGroupManager().getMaxPlayers(group.getLeader())) {
						p.sendMessage(main.getMessageFetcher().getMessage("group.full", true).replaceAll("%max%", main.getGroupManager().getMaxPlayers(group.getLeader()) + ""));
					}
					else {
						p.sendMessage(main.getMessageFetcher().getMessage("group.already_invited", true).replaceAll("%player%", args[1]));
					}
				}
			}
			else {
				p.sendMessage(main.getMessageFetcher().getMessage("group.not_leader", true));
			}
		}
		else if (args[0].equalsIgnoreCase("kick")) {
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/group kick <player>"));
				return true;
			}
			if (Bukkit.getServer().getPlayer(args[1]) == null) {
				p.sendMessage(main.getMessageFetcher().getMessage("group.not_online", true).replaceAll("%player%", args[1]));
				return true;
			}
			if (Bukkit.getServer().getPlayer(args[1]) == p) {
				p.sendMessage(main.getMessageFetcher().getMessage("group.self_interact", true));
				return true;
			}
			Group group = main.getGroupManager().getGroup(p);
			group.kick(Bukkit.getServer().getPlayer(args[1]));
		}
		else if (args[0].equalsIgnoreCase("setleader")) {
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/group setleader <player>"));
				return true;
			}
			if (Bukkit.getServer().getPlayer(args[1]) == null) {
				p.sendMessage(main.getMessageFetcher().getMessage("group.not_online", true).replaceAll("%player%", args[1]));
				return true;
			}
			if (Bukkit.getServer().getPlayer(args[1]) == p) {
				p.sendMessage(main.getMessageFetcher().getMessage("group.self_interact", true));
				return true;
			}
			Group group = main.getGroupManager().getGroup(p);
			group.setLeader(Bukkit.getServer().getPlayer(args[1]));
		}
		else if (args[0].equalsIgnoreCase("leave")) {
			if (args.length != 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/group leave"));
				return true;
			}
			Group group = main.getGroupManager().getGroup(p);
			group.leave(p);
		}
		else if (args[0].equalsIgnoreCase("accept")) {
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/group accept <player>"));
				return true;
			}
			if (Bukkit.getServer().getPlayer(args[1]) == null) {
				p.sendMessage(main.getMessageFetcher().getMessage("group.not_online", true).replaceAll("%player%", args[1]));
				return true;
			}
			Group group = main.getGroupManager().getGroup(Bukkit.getServer().getPlayer(args[1]));
			group.accept(p);
		}
		else if (args[0].equalsIgnoreCase("deny")) {
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/group deny <player>"));
				return true;
			}
			if (Bukkit.getServer().getPlayer(args[1]) == null) {
				p.sendMessage(main.getMessageFetcher().getMessage("group.not_online", true).replaceAll("%player%", args[1]));
				return true;
			}
			Group group = main.getGroupManager().getGroup(Bukkit.getServer().getPlayer(args[1]));
			group.deny(p);
		}
		else {
			Group group = main.getGroupManager().getGroup(p);
			if (args.length != 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/group <player>"));
				return true;
			}
			if (p == group.getLeader()) {
				if (Bukkit.getServer().getPlayer(args[0]) == null) {
					p.sendMessage(main.getMessageFetcher().getMessage("group.not_online", true).replaceAll("%player%", args[0]));
					return true;
				}
				if (Bukkit.getServer().getPlayer(args[0]) == p) {
					p.sendMessage(main.getMessageFetcher().getMessage("group.self_interact", true));
					return true;
				}
				if (group.getPlayers().contains(Bukkit.getPlayer(args[0]))) {
					p.sendMessage(main.getMessageFetcher().getMessage("group.already_in_group", true).replaceAll("%player%", args[0]));
					return true;
				}
				if (group.invite(Bukkit.getPlayer(args[0])))
					p.sendMessage(main.getMessageFetcher().getMessage("group.invite_sender", true).replaceAll("%player%", args[0]));
				else {
					p.sendMessage(main.getMessageFetcher().getMessage("group.already_invited", true).replaceAll("%player%", args[0]));
				}
			}
			else {
				p.sendMessage(main.getMessageFetcher().getMessage("group.not_leader", true));
			}
		}
		return true;
	}
	
}

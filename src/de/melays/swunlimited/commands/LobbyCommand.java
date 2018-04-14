/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.commands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.map_manager.Cluster;
import de.melays.swunlimited.map_manager.error.UnknownClusterException;
import de.melays.swunlimited.npc.NPCType;

public class LobbyCommand {
	
	Main main;
	
	public LobbyCommand(Main main) {
		this.main = main;
	}
	
	public void onCommand (CommandSender sender , String command , String args[]) {
		HelpSender helpSender = new HelpSender (main , command);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/sw lobby help");
		helpSender.addAlias("setlobby", "Set the lobby location", "Set the location where\nwhere players get when they join\nyour server." , "/sw lobby setlobby");
		helpSender.addAlias("setgamelobby", "Set the game-lobby location", "Set the location where\nwhere players get when they join\na arena." , "/sw lobby setgamelobby");
		helpSender.addAlias("removechallengercluster <cluster>", "Remove a cluster", "Remove a cluster from the\nchallengers map selector menu" , "/sw lobby removechallengercluster <cluster>");
		helpSender.addAlias("listchallengerclusters", "List the clusters", "Lists the maps in the mapselector" , "/sw lobby listchallengerclusters");
		helpSender.addAlias("addlobbynpc [...]", "Add a lobby npc", "Add a lobby npc for a game queue or settings" , "/sw lobby addlobbynpc [Entity] [Displayname] [NPCType] [Nametag (true/false)]");
		helpSender.addAlias("addhologram <display>", "Add a lobby hologram", "Add a hologram for informations" , "/sw lobby addhologram <display>");
		helpSender.addAlias("removeentity <id>", "Removes a entity", "Removes lobby a npc or hologram" , "/sw lobby removeentity <id>");
		
		if (args.length == 1) {
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.help"))return;
			helpSender.sendHelp(sender, 1);
		}
		
		else if (args[1].equalsIgnoreCase("help")) {
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.help"))return;
			if (args.length == 2) {
				helpSender.sendHelp(sender, 1);
			}
			else {
				try {
					int page = Integer.parseInt(args[2]);
					helpSender.sendHelp(sender, page);
				} catch (NumberFormatException e) {
					helpSender.sendHelp(sender, 1);
				}
			}
		}
		
		else if (args[1].equalsIgnoreCase("addlobbynpc")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw lobby addlobbynpc [Entity] [Displayname] [NPCType] [Nametag (true/false)]"));
				return;
			}
			Player p = (Player) sender;
			EntityType type = EntityType.VILLAGER;
			String displayname = main.c("&aNPC");
			NPCType npc = NPCType.DISPLAY;
			boolean nametag = true;
			
			if (args.length > 2) {
				try {
					type = EntityType.valueOf(args[2].toUpperCase());
					if (type == null) {
						p.sendMessage(main.prefix + "Unknown Entity Type!");
						return;
					}
				} catch (Exception e) {
					p.sendMessage(main.prefix + "Unknown Entity Type!");
					return;
				}
			}
			
			if (args.length > 3) {
				displayname = args[3];
			}
			
			if (args.length > 4) {
				try {
					npc = NPCType.valueOf(args[4].toUpperCase());
					if (npc == null) {
						p.sendMessage(main.prefix + "Unknown NCP Type! (QUEUE , DISPLAY , SETTINGS)");
						return;
					}
				} catch (Exception e) {
					p.sendMessage(main.prefix + "Unknown NCP Type! (QUEUE , DISPLAY , SETTINGS)");
					return;
				}
			}
			
			if (args.length > 5) {
				try {
					nametag = Boolean.valueOf(args[5]);
				} catch (Exception e) {
					p.sendMessage(main.prefix + "Please use true or false!");
					return;
				}
			}
			
			HashMap<String , Integer> nbt_data = new HashMap<String , Integer>();
			nbt_data.put("NoAI", 1);
			nbt_data.put("NoGravity", 1);
			nbt_data.put("Invulnerable", 1);
			nbt_data.put("Silent", 1);
			int id = main.getLobbyNPCManager().addNPC(p.getLocation(), type, npc, displayname, nbt_data, nametag);
			p.sendMessage(main.prefix + "The lobby npc has been created! (ID: "+id+")");
		}
		
		else if (args[1].equalsIgnoreCase("addhologram")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw lobby addhologram <Display>"));
				return;
			}
			Player p = (Player) sender;
			
			String msg = "";
			for (int i = 2 ; i < args.length ; i++) {
				if (i == args.length - 1) {
					msg += args[i];
				}
				else {
					msg += args[i] + " ";
				}
			}

			int id = main.getLobbyNPCManager().addHologram(p.getLocation(), msg);
			p.sendMessage(main.prefix + "The hologram has been created! (ID: "+id+")");
		}
		
		else if (args[1].equalsIgnoreCase("removeentity")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw lobby removeentity <id>"));
				return;
			}
			Player p = (Player) sender;
			int id = 0;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				p.sendMessage(main.prefix + "Please enter a valid number!");
				return;
			}
			main.getLobbyNPCManager().removeNPC(id);
			p.sendMessage(main.prefix + "If this npc existed, it has been removed!");
		}
		
		else if (args[1].equalsIgnoreCase("setlobby")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw lobby setlobby"));
				return;
			}
			Player p = (Player) sender;
			main.getLobbyManager().setLobbyLocation(p.getLocation());
			p.sendMessage(main.prefix + "The lobby location has been set!");
		}
		
		else if (args[1].equalsIgnoreCase("setgamelobby")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw lobby setgamelobby"));
				return;
			}
			Player p = (Player) sender;
			main.getLobbyManager().setGameLobbyLocation(p.getLocation());
			p.sendMessage(main.prefix + "The game-lobby location has been set!");
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/sw lobby help"));
		}
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.settings.Settings;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;

public class ArenaCommand {
	
	Main main;
	
	public ArenaCommand(Main main) {
		this.main = main;
	}
	
	public void onCommand (CommandSender sender , String command , String args[]) {
		HelpSender helpSender = new HelpSender (main , command);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/bw arenas help");
		helpSender.addAlias("list", "Lists the running games", "Shows all running games" , "/bw arenas list");
		helpSender.addAlias("start <cluster> <player>,...<player>", "Starts a custom game", "Starts a custom game with\ndefault settings" , "/bw arenas start <cluster> <player>,...<player>");
		helpSender.addAlias("stop <cluster>/<player>/<id>/<category>", "Stops an arena", "Stops the arenas:\n - using that cluster\n - with that player\n - with that id\n - in that category" , "/bw arenas stop <cluster>/<player>/<id>/<category>");
		helpSender.addAlias("stopall", "Stops all arenas", "Stops COMPLETELY all arenas." , "/bw arenas stopall");

		
		if (args.length == 1) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			helpSender.sendHelp(sender, 1);
		}
		
		else if (args[1].equalsIgnoreCase("help")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
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
		
		else if (args[1].equalsIgnoreCase("start")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw arenas start <cluster> <player>,...<player>"));
				return;
			}
			Cluster cluster = null;
			try {
				cluster = main.getClusterManager().getCluster(args[2]);
			} catch (UnknownClusterException e) {
				sender.sendMessage(main.prefix + "This cluster does not exist!");
				return;
			}
			if (cluster.getClusterMeta() == null) {
				sender.sendMessage(main.prefix +  "This cluster is not ready yet!");
				return;
			}
			String[] player_string = args[3].split(",");
			try {
				ArrayList<Player> players = new ArrayList<Player>();
				for (String s : player_string) {
					players.add(Bukkit.getPlayer(s));
				}
				main.getArenaManager().startGame(cluster, new Settings(main), "other" , players);
			} catch (Exception e) {
				sender.sendMessage(main.prefix +  "An error occured trying to start the game!");
				return;
			}
		}
		
		else if (args[1].equalsIgnoreCase("stopall")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length != 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw arenas stopall"));
				return;
			}
			int count = 0;
			for (Arena a : main.getArenaManager().getArenas()) {
				count ++;
				a.stop();
			}
			sender.sendMessage(main.prefix + count + " arenas have been stopped!");
		}
		
		else if (args[1].equalsIgnoreCase("stop")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length <= 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw arenas stop <cluster>/<player>/<id>/<category>"));
				return;
			}
			Cluster cluster = null;
			try {
				cluster = main.getClusterManager().getCluster(args[2]);
			} catch (UnknownClusterException e) {

			}
			
			if (cluster != null) {
				int count = 0;
				for (Arena a : main.getArenaManager().getArenas()) {
					if (a.cluster.name.equals(cluster.name)) {
						count ++;
						a.stop();
					}
				}
				sender.sendMessage(main.prefix + count + " arenas using the cluster '"+cluster.name+"' have been stopped!");
				return;
			}
			
			Player search = Bukkit.getPlayer(args[2]);
			if (search != null) {
				Arena a = main.getArenaManager().searchPlayer(search);
				if (a == null) {
					sender.sendMessage(main.prefix + search.getName() + " is not ingame! Searching for the category " + args[2] + " ...");
				}
				else {
					a.stop();
					sender.sendMessage(main.prefix + search.getName() + "'s arena has been stopped.");
					return;
				}
			}
			
			int id = -1;
			try {
				id = Integer.parseInt(args[2]);
			} catch (NumberFormatException e) {
				id = -1;
			}
			
			Arena a = main.getArenaManager().getArena(id);
			if (a != null) {
				a.stop();
				sender.sendMessage(main.prefix + "The arena '"+id+"' has been stopped!");
				return;
			}
			
			String category = args[2];
			int count = 0;
			for (Arena arena : main.getArenaManager().getArenas(category)) {
				count ++;
				arena.stop();
			}
			sender.sendMessage(main.prefix + count + " arenas in the categroy '"+category+"' have been stopped!");
			return;
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/bw arenas help"));
		}
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.commands;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.teams.Color;

public class MainCommand implements CommandExecutor {

	Main main;
	
	ClusterCommand clusterCommand;
	TeamCommand teamCommand;
	SetupCommand setupCommand;
	LobbyCommand lobbyCommand;
	ArenaCommand arenaCommand;
	
	public MainCommand(Main main) {
		this.main = main;
		this.clusterCommand = new ClusterCommand(main);
		this.teamCommand = new TeamCommand(main);
		this.setupCommand = new SetupCommand(main);
		this.lobbyCommand = new LobbyCommand(main);
		this.arenaCommand = new ArenaCommand(main);
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		HelpSender helpSender = new HelpSender (main , alias);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/bw help");
		helpSender.addAlias("cluster [...]", "Get to the cluster management overview", "Shows commands to manage the clusters.\nUse 'cluster <page>' to get to the next help pages" , "/bw cluster");
		helpSender.addAlias("setup [...]", "Get to the cluster-setup management overview", "Shows commands to manage the clusters locations.\nUse 'setup <page>' to get to the next help pages" , "/bw setup");
		helpSender.addAlias("arenas [...]", "Get to the arena management overview", "Shows commands to manage the arenas.\nManage the running arenas.\nUse 'arenas <page>' to get to the next help pages" , "/bw arenas");
		helpSender.addAlias("teams [...]", "Get to the team management overview", "Shows commands to manage the teams.\nUse 'teams <page>' to get to the next help pages" , "/bw teams");
		helpSender.addAlias("lobby [...]", "Get to the lobby management overview", "Shows commands to manage the lobby.\nUse 'lobby <page>' to get to the next help pages" , "/bw lobby");
		helpSender.addAlias("reload", "Reload the configuration files", "Reloades all configuration files.\nThis can cause issues in running arenas!" , "/bw reload");
		helpSender.addAlias("worldtp <game , presets , world>", "Teleport to a world", "You can teleport to those worlds:\n - 'GAME', here are the arenas generated to\n - 'PRESETS', here you can create presets\\n - 'WORLD', the specified default world" , "/bw worldtp <game , presets , world>");
		helpSender.addAlias("colorlist", "List all colors", "Lists all available Colors to create teams" , "/bw colorlist");
		helpSender.addAlias("showsettings", "Lists the settings of a player", "Lists all settings from this player" , "/bw showsettings <player>");

		
		if (args.length == 0) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return true;
			helpSender.sendHelp(sender, 1);
		}
		
		else if (args[0].equalsIgnoreCase("help")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return true;
			if (args.length == 1) {
				helpSender.sendHelp(sender, 1);
			}
			else {
				try {
					int page = Integer.parseInt(args[1]);
					helpSender.sendHelp(sender, page);
				} catch (NumberFormatException e) {
					helpSender.sendHelp(sender, 1);
				}
			}
		}
		
		else if (args[0].equalsIgnoreCase("reload")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.reload"))return true;
			main.reloadAll();
			sender.sendMessage(main.prefix + "Reloaded all configuration files.");
		}
		
		else if (args[0].equalsIgnoreCase("cluster")) {
			clusterCommand.onCommand(sender, alias + " " + args[0], args);
		}
		
		else if (args[0].equals("arenas")) {
			arenaCommand.onCommand(sender, alias + " " + args[0], args);
		}
		
		else if (args[0].equals("lobby")) {
			lobbyCommand.onCommand(sender, alias + " " + args[0], args);
		}
		
		else if (args[0].equals("setup")) {
			setupCommand.onCommand(sender, alias + " " + args[0], args);
		}
		
		else if (args[0].equals("teams")) {
			teamCommand.onCommand(sender, alias + " " + args[0], args);
		}
		
		else if (args[0].equals("colorlist")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return true;
			String c = "";
			for (String s : Color.getAll()) {
				c += new Color(s).toChatColor() + s + " ";
			}
			sender.sendMessage(main.prefix + "Available colors:");
			sender.sendMessage(c);
		}
		
		else if (args[0].equals("showsettings")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return true;
			}
			if (args.length == 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw showsettings <player>"));
				return true;
			}
			Player p = Bukkit.getPlayer(args[1]);
			if (p == null) {
				sender.sendMessage(main.getMessageFetcher().getMessage("group.not_online", true));
				return true;
			}
			if (!p.isOnline()) {
				sender.sendMessage(main.getMessageFetcher().getMessage("group.not_online", true));
				return true;
			}
			main.getLobbyManager().settings.get(p).sendAsString((Player) sender);
		}
		
		else if (args[0].equals("worldtp")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return true;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return true;
			if (args.length == 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw worldtp <game , presets , world>"));
				return true;
			}
			Player p = (Player) sender;
			if (args[1].equalsIgnoreCase("game")) {
				p.teleport(Bukkit.getWorld(main.gameworld).getSpawnLocation());
			}
			else if (args[1].equalsIgnoreCase("presets")) {
				p.teleport(Bukkit.getWorld(main.presetworld).getSpawnLocation().clone().add(0, 1, 0));
			}
			else if (args[1].equalsIgnoreCase("world")) {
				p.teleport(Bukkit.getServer().getWorlds().get(0).getSpawnLocation());
			}
			else {
				p.sendMessage(main.prefix + "Unknown world. Use 'game', 'presets' or 'world'");
			}
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/bw help"));
		}
		
		return true;
	}
	
}

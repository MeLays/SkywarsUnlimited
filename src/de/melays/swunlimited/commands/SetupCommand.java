/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.map_manager.Cluster;
import de.melays.swunlimited.map_manager.ClusterTools;
import de.melays.swunlimited.map_manager.error.UnknownClusterException;
import de.melays.swunlimited.map_manager.meta.error.TeamAlreadyAddedException;
import de.melays.swunlimited.teams.Team;
import de.melays.swunlimited.teams.error.UnknownTeamException;
import net.md_5.bungee.api.ChatColor;

public class SetupCommand {
	
	Main main;
	
	public SetupCommand(Main main) {
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
	public void onCommand (CommandSender sender , String command , String args[]) {
		HelpSender helpSender = new HelpSender (main , command);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/sw setup help");
		helpSender.addAlias("setDisplayItem <cluster> <material>", "Sets the displayitem", "Sets the item shown in menus.\nYou can add data by adding a :\nex.: Stone:2." , "/sw setup setDisplayItem <cluster> <material>");
		helpSender.addAlias("setDisplayName <cluster> <name>", "Sets the displayname", "Sets the displayname of the cluster.\nThis will be shown on signs and menus.\nYou CAN use spaces!" , "/sw setup setDisplayName <cluster> <name>");
		helpSender.addAlias("addteam <cluster> <team>", "Adds a team to a cluster", "Adds a team to a cluster.\nA cluster can have a infinite amount of teams." , "/sw setup addteam <cluster> <team>");
		helpSender.addAlias("listteams <cluster>", "List the teams of the cluster", "Lists all teams added to the cluster." , "/sw setup listteams <cluster>");
		helpSender.addAlias("removeteam <cluster> <team>", "Removes a team", "Removes a team from a cluster.\nA cluster can have a infinite amount of teams." , "/sw setup removeteam <cluster> <team>");

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
		
		else if (args[1].equalsIgnoreCase("setDisplayItem")) {
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw setup setDisplayItem <cluster> <item>"));
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
			String material = args[3];
			byte data = 0;
			if (material.contains(":")) {
				String byte_raw = material.split(":")[1];
				material = material.split(":")[0];
				try {
					data = Byte.parseByte(byte_raw);
				}catch (Exception ex) {
					
				}
			}
			Material m = Material.getMaterial(material);
			if (m == null) {
				try {
					m = Material.getMaterial(Integer.parseInt(material));
				} catch (NumberFormatException e) {
					sender.sendMessage(main.prefix + "The material you entered is not valid!");
					return;
				}
				if (m == null) {
					sender.sendMessage(main.prefix + "The material you entered is not valid!");
					return;
				}
			}
			main.getClusterManager().getConfiguration().set(cluster.name+".display_item", m.toString()+":"+data);
			main.getClusterManager().saveFile();
			sender.sendMessage(main.prefix +  "The item '"+args[3]+"' has been set as the displayitem of '"+cluster.name+"'");
			return;

		}
		
		else if (args[1].equalsIgnoreCase("setDisplayName")) {
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw setup setDisplayName <cluster> <name>"));
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
			String msg = "";
			for (int i = 3 ; i < args.length ; i++) {
				if (i == args.length - 1) {
					msg += args[i];
				}
				else {
					msg += args[i] + " ";
				}
			}
			main.getClusterManager().getConfiguration().set(cluster.name+".display", msg);
			main.getClusterManager().saveFile();
			sender.sendMessage(main.prefix +  "The displayname '"+msg+"' has been set as the displayname of '"+cluster.name+"'");
			return;

		}
		
		else if (args[1].equalsIgnoreCase("addteam")) {
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw setup addteam <cluster> <team>"));
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
			try {
				cluster.getClusterMeta().addTeam(args[3]);
				sender.sendMessage(main.prefix +  "This team '"+args[3]+"' has been added to the cluster '"+cluster.name+"'");
				return;
			} catch (UnknownTeamException e) {
				sender.sendMessage(main.prefix +  "This team does not exists!");
				return;
			} catch (TeamAlreadyAddedException e) {
				sender.sendMessage(main.prefix +  "This team is already added to this cluster!");
				return;
			}
		}
		
		else if (args[1].equalsIgnoreCase("listteams")) {
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw setup listteams <cluster>"));
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
			ListSender teamlist = new ListSender (main , "Team-List ("+cluster.name+")");
			for (Team team : cluster.getClusterMeta().getTeams()) {
				teamlist.addItem(team.Color.toChatColor() + team.name + ChatColor.YELLOW + " - " + team.display + ChatColor.RESET +" max. "+ ChatColor.ITALIC + ""  + team.max);
			}
			if (args.length == 2) {
				teamlist.sendList(sender, 1);
			}
			else {
				try {
					int page = Integer.parseInt(args[3]);
					teamlist.sendList(sender, page);
				} catch (Exception e) {
					teamlist.sendList(sender, 1);
				}
			}
		}
		
		else if (args[1].equalsIgnoreCase("removeteam")) {
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw setup removeteam <cluster> <team>"));
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
			cluster.getClusterMeta().removeTeam(args[3]);
			sender.sendMessage(main.prefix + "If this team existed it has been removed!");
		}
		
		else if (args[1].equalsIgnoreCase("setteamspawn")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "swunlimited.setup"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/sw setup setteamspawn <cluster> <team>"));
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
			if (!ClusterTools.isInCluster(p.getLocation() , cluster)) {
				p.sendMessage(main.prefix + "The location must be inside the cluster-template!");
				return;
			}
			try {
				cluster.getClusterMeta().setTeamSpawn(args[3], p.getLocation());
				sender.sendMessage(main.prefix + "Set the teamspawn of '"+args[3]+"' to your location!");
				return;
			} catch (UnknownTeamException e) {
				sender.sendMessage(main.prefix + "This team does not exist!");
				return;
			}
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/sw setup help"));
		}
		
	}
	
}

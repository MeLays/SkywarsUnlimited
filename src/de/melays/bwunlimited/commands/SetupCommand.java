package de.melays.bwunlimited.commands;

import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.map_manager.Cluster;
import de.melays.bwunlimited.map_manager.ClusterTools;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;
import de.melays.bwunlimited.map_manager.meta.ItemSpawner;
import de.melays.bwunlimited.map_manager.meta.error.TeamAlreadyAddedException;
import de.melays.bwunlimited.teams.Team;
import de.melays.bwunlimited.teams.error.UnknownTeamException;
import net.md_5.bungee.api.ChatColor;

public class SetupCommand {
	
	Main main;
	
	public SetupCommand(Main main) {
		this.main = main;
	}
	
	@SuppressWarnings("deprecation")
	public void onCommand (CommandSender sender , String command , String args[]) {
		HelpSender helpSender = new HelpSender (main , command);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/bw setup help");
		helpSender.addAlias("addteam <cluster> <team>", "Adds a team to a cluster", "Adds a team to a cluster.\nA cluster can have a infinite amount of teams." , "/bw setup addteam <cluster> <team>");
		helpSender.addAlias("listteams <cluster>", "List the teams of the cluster", "Lists all teams added to the cluster." , "/bw setup listteams <cluster>");
		helpSender.addAlias("removeteam <cluster> <team>", "Removes a team", "Removes a team from a cluster.\nA cluster can have a infinite amount of teams." , "/bw setup removeteam <cluster> <team>");
		helpSender.addAlias("setteamspawn <cluster> <team>", "Sets a teamspawn", "Sets the teamspawn to your current location\nYou must be inside the cluster!" , "/bw setup setteamspawn <cluster> <team>");
		helpSender.addAlias("addspawner <...>", "Add a spawner", "Adds an itemspawner to a cluster\nto you Location.\nYou must be inside the cluster!" , "/bw setup addspawner <cluster> <material/ID> <ticks> <displayname>");
		helpSender.addAlias("listspawners <cluster>", "List the itemspawners", "Lists all itemspawners added to the cluster." , "/bw setup listspawners <cluster>");
		helpSender.addAlias("removespawner <cluster> <id>", "Removes a itemspawner", "Removes a itemspawner from a cluster" , "/bw setup removespawner <cluster> <id>");
		helpSender.addAlias("addshop <cluster>", "Add a merchant to a cluster", "Adds an merchant to a cluster\nto you Location.\nYou must be inside the cluster!" , "/bw setup addshop <cluster>");
		helpSender.addAlias("listshops <cluster>", "List the merchants", "Lists all merchants added to the cluster." , "/bw setup listshops <cluster>");
		helpSender.addAlias("removeshop <cluster> <id>", "Removes a shop", "Removes a shop from a cluster" , "/bw setup removeshop <cluster> <id>");
		
		if (args.length == 1) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			helpSender.sendHelp(sender, 1);
		}
		
		else if (args[1].equalsIgnoreCase("help")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
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
		
		else if (args[1].equalsIgnoreCase("addteam")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup addteam <cluster> <team>"));
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
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup listteams <cluster>"));
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
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup removeteam <cluster> <team>"));
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
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup setteamspawn <cluster> <team>"));
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
		
		else if (args[1].equalsIgnoreCase("addspawner")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 5) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup addspawner <cluster> <material/ID> <ticks> <displayname>"));
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
			Material m = Material.getMaterial(args[3]);
			if (m == null) {
				try {
					m = Material.getMaterial(Integer.parseInt(args[3]));
				} catch (NumberFormatException e) {
					p.sendMessage(main.prefix + "The material you entered is not valid!");
					return;
				}
				if (m == null) {
					p.sendMessage(main.prefix + "The material you entered is not valid!");
					return;
				}
			}
			int ticks = 0;
			try {
				ticks = Integer.parseInt(args[4]);
			} catch (NumberFormatException e) {
				p.sendMessage(main.prefix + "You need to enter a valid number for the ticks!");
				return;
			}
			String msg = "";
			for (int i = 5 ; i < args.length ; i++) {
				if (i == args.length - 1) {
					msg += args[i];
				}
				else {
					msg += args[i] + " ";
				}
			}
			int id = cluster.getClusterMeta().addItemSpawner(p.getLocation(), m, ticks, msg);
			sender.sendMessage(main.prefix +  "A itemspawner with the id '"+id+"' has been added to the cluster '"+cluster.name+"'");
		}
		
		else if (args[1].equalsIgnoreCase("listspawners")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup listteams <cluster>"));
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
			ListSender teamlist = new ListSender (main , "Itemspawner-List ("+cluster.name+")");
			for (ItemSpawner spawner : cluster.getClusterMeta().getItemSpawners()) {
				teamlist.addItem(spawner.id + ChatColor.GRAY.toString() + " | " + ChatColor.WHITE + spawner.m.toString() + ChatColor.YELLOW + " - " + ChatColor.RESET + spawner.ticks + " Ticks" + ChatColor.YELLOW + " - " + ChatColor.RESET + main.c(spawner.displayname));
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
		
		else if (args[1].equalsIgnoreCase("removespawner")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup removespawner <cluster> <id>"));
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
			int id = 0;
			try {
				id = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage(main.prefix + "You need to enter a valid number for the id!");
				return;
			}
			cluster.getClusterMeta().removeItemSpawner(id);
			sender.sendMessage(main.prefix + "If this itemspawner existed it has been removed!");
		}
		
		else if (args[1].equalsIgnoreCase("addshop")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			Player p = (Player) sender;
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup addshop <cluster>"));
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
			int id = cluster.getClusterMeta().addShop(p.getLocation());
			sender.sendMessage(main.prefix +  "A shop with the id '"+id+"' has been added to the cluster '"+cluster.name+"'");
			return;
		}
		
		else if (args[1].equalsIgnoreCase("listshops")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup listshops <cluster>"));
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
			ListSender teamlist = new ListSender (main , "Merchant-List ("+cluster.name+")");
			for (int id : cluster.getClusterMeta().getShopsMap().keySet()) {
				teamlist.addItem(id + ChatColor.GRAY.toString() + " | " + ChatColor.WHITE + "Location shift: " + cluster.getClusterMeta().getShopsMap().get(id).toString());
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
		
		else if (args[1].equalsIgnoreCase("removeshop")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw setup removeshop <cluster> <id>"));
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
			int id = 0;
			try {
				id = Integer.parseInt(args[3]);
			} catch (NumberFormatException e) {
				sender.sendMessage(main.prefix + "You need to enter a valid number for the id!");
				return;
			}
			cluster.getClusterMeta().removeShop(id);
			sender.sendMessage(main.prefix + "If this shop existed it has been removed!");
		}
		
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.commands;

import org.bukkit.command.CommandSender;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.error.InvalidNameException;
import de.melays.bwunlimited.teams.Color;
import de.melays.bwunlimited.teams.Team;
import de.melays.bwunlimited.teams.error.TeamAlreadyExistsException;
import net.md_5.bungee.api.ChatColor;

public class TeamCommand {
	
	Main main;
	
	public TeamCommand(Main main) {
		this.main = main;
	}
	
	public void onCommand (CommandSender sender , String command , String args[]) {
		HelpSender helpSender = new HelpSender (main , command);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/bw teams help");
		helpSender.addAlias("list", "List created teams", "Lists all teams that have been created!" , "/bw teams list");
		helpSender.addAlias("create <...>", "Create a team", "Create a team that you can add to an cluster\nname - the identifier of the team\n   (wont be shown to users)\ndisplayname - the name shown to the users\n- color - The teamcolor (/bw colorlist)\nmax-players - The max amount of players that can join this team" , "/bw teams create <name> <displayname> <color> <max-players>");
		helpSender.addAlias("remove <name>", "Remove a team", "Remove a team from the teams.yml\nWARNING: Clusters using this team wont load anymore!" , "/bw teams remove <name>");
		
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
		
		else if (args[1].equalsIgnoreCase("create")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length <= 5) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw teams create <name> <displayname> <color> <max-players>"));
				return;
			}
			Color c = new Color(args[4]);
			if (!c.exists()) {
				sender.sendMessage(main.prefix + "This color does not exist! Use /bw colorlist");
				return;
			}
			try {
				int max = Integer.parseInt(args[5]);
				main.getTeamManager().createTeam(args[2], args[3], c , max);
				sender.sendMessage(main.prefix + "The team has been created!");
			} catch (TeamAlreadyExistsException e) {
				sender.sendMessage(main.prefix + "There is already a team with that name!");
			} catch (InvalidNameException e) {
				sender.sendMessage(main.prefix + "Please use only alphanumeric characters (A-Z , a-z , 0-9)");
			} catch (NumberFormatException e) {
				sender.sendMessage(main.prefix + "'"+args[5]+"' is not a number!");
			}
		}
		
		else if (args[1].equalsIgnoreCase("remove")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length <= 2) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw teams remove <name>"));
				return;
			}
			if (main.getTeamManager().exists(args[2])) {
				main.getTeamManager().removeTeam(args[2]);
				sender.sendMessage(main.prefix + "The team has been removed!");
				sender.sendMessage(main.prefix + "WARNING: Clusters using this team may not load anymore!");
			}
			else {
				sender.sendMessage(main.prefix + "The team doesn't exist!");
			}
		}
		
		else if (args[1].equalsIgnoreCase("list")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			ListSender teamlist = new ListSender (main , "Team-List");
			for (Team team : main.getTeamManager().getTeams().values()) {
				teamlist.addItem(team.Color.toChatColor() + team.name + ChatColor.YELLOW + " - " + team.display + ChatColor.RESET +" max. "+ ChatColor.ITALIC + ""  + team.max);
			}
			if (args.length == 2) {
				teamlist.sendList(sender, 1);
			}
			else {
				try {
					int page = Integer.parseInt(args[2]);
					teamlist.sendList(sender, page);
				} catch (NumberFormatException e) {
					teamlist.sendList(sender, 1);
				}
			}
		}
		
		else {
			sender.sendMessage(main.getMessageFetcher().getMessage("help.unknown", true).replaceAll("%help%", "/bw teams help"));
		}
		
	}
	
}

package de.melays.bwunlimited.commands;

import org.bukkit.command.CommandSender;

import de.melays.bwunlimited.Main;

public class SetupCommand {
	
	Main main;
	
	public SetupCommand(Main main) {
		this.main = main;
	}
	
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
		
	}
	
}

package de.melays.bwunlimited.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;

public class LobbyCommand {
	
	Main main;
	
	public LobbyCommand(Main main) {
		this.main = main;
	}
	
	public void onCommand (CommandSender sender , String command , String args[]) {
		HelpSender helpSender = new HelpSender (main , command);
		
		helpSender.addAlias("help [page]", "Show this overview", "Use 'help <page>' to get to the next help pages" , "/bw teams help");
		helpSender.addAlias("setlobby", "Set the lobby location", "Set the location where\nwhere players get when they join\nyour server." , "/bw lobby setlobby");
		
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
		
		else if (args[1].equalsIgnoreCase("setlobby")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage(main.prefix + "You cant run this command from the console!");
				return;
			}
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.setup"))return;
			if (args.length <= 1) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw lobby setlobby"));
				return;
			}
			Player p = (Player) sender;
			main.getLobbyManager().setLobbyLocation(p.getLocation());
			p.sendMessage(main.prefix + "The lobbylocation has been set!");
		}
	}
	
}

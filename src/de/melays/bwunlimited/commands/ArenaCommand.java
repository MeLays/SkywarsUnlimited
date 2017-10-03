package de.melays.bwunlimited.commands;

import java.util.ArrayList;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
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
		
		else if (args[1].equalsIgnoreCase("start")) {
			if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.help"))return;
			if (args.length <= 3) {
				sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/bw arenas start <cluster> <player>,...<player>\""));
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
				main.getArenaManager().startGame(cluster, new Settings(main), players);
			} catch (Exception e) {
				sender.sendMessage(main.prefix +  "An error occured trying to start the game!");
				return;
			}
		}
	}
	
}

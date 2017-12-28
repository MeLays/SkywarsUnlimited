/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.commands.spectate;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;

public class SpectateCommand implements CommandExecutor {
	
	Main main;
	
	public SpectateCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(main.prefix + "You cant run this command from the console!");
			return true;
		}
		Player p = (Player) sender;
		if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.spectatecommand")) {
			return true;
		}
		if (args.length != 1) {
			sender.sendMessage(main.getMessageFetcher().getMessage("command_usage", true).replaceAll("%command%", "/spectate <Player>/<ID>"));
			return true;
		}
		Arena arena = main.getArenaManager().searchPlayer(Bukkit.getPlayer(args[0]));
		if (arena == null) {
			try {
				arena = main.getArenaManager().getArena(Integer.parseInt(args[0]));
			} catch (NumberFormatException e) {
				arena = null;
			}
		}
		if (arena == null) {
			sender.sendMessage(main.getMessageFetcher().getMessage("spectate.not_found", true));
			return true;
		}
		if (arena.state != ArenaState.INGAME) {
			p.sendMessage(main.getMessageFetcher().getMessage("spectate.not_ingame", true));
			return true;
		}
		arena.forceSpectate(p);
		return true;
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.commands.leave;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.game.arenas.Arena;

public class LeaveCommand implements CommandExecutor {
	
	Main main;
	
	public LeaveCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(main.prefix + "You cant run this command from the console!");
			return true;
		}
		Player p = (Player) sender;
		Arena arena = main.getArenaManager().searchPlayer(p);
		if (arena == null) {
			p.sendMessage(main.getMessageFetcher().getMessage("start.not_ingame", true));
			return true;
		}
		arena.leave(p);
		return true;
	}
	
}

package de.melays.bwunlimited.commands.start;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;

public class StartCommand implements CommandExecutor {
	
	Main main;
	
	public StartCommand(Main main) {
		this.main = main;
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
		if (!(sender instanceof Player)) {
			sender.sendMessage(main.prefix + "You cant run this command from the console!");
			return true;
		}
		Player p = (Player) sender;
		if (!main.getMessageFetcher().checkPermission(sender, "bwunlimited.start")) {
			return true;
		}
		Arena arena = main.getArenaManager().searchPlayer(p);
		if (arena == null) {
			p.sendMessage(main.getMessageFetcher().getMessage("not_ingame", true));
			return true;
		}
		if (arena.state != ArenaState.LOBBY) {
			p.sendMessage(main.getMessageFetcher().getMessage("already_started", true));
			return true;
		}
		if (arena.getAll().size() < 2) {
			p.sendMessage(main.getMessageFetcher().getMessage("not_enought_players", true));
			return true;
		}
		arena.arenaLobby.stopLobby();
		p.sendMessage(main.getMessageFetcher().getMessage("started", true));
		return true;
	}
	
}

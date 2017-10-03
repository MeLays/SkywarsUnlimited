package de.melays.bwunlimited.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.ArenaTeam;
import de.melays.bwunlimited.game.arenas.state.ArenaState;

public class AsyncPlayerChatEventListener implements Listener{

	Main main;
	
	public AsyncPlayerChatEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			e.setCancelled(true);
			Arena arena = main.getArenaManager().searchPlayer(p);
			boolean global = false;
			String message = e.getMessage();
			if (message.startsWith("@all ")) {
				global = true;
				message = message.replaceFirst("@all ", "");
			}
			else if (message.startsWith("@a ")) {
				global = true;
				message = message.replaceFirst("@a ", "");
			}
			else if (message.startsWith("@")) {
				global = true;
				message = message.replaceFirst("@", "");
			}
			if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.ENDING) {
				String msg = main.getMessageFetcher().getMessage("chat.game.lobby", true);
				msg = msg.replaceAll("%player_prefix%", main.getChatHook().getPrefix(p));
				msg = msg.replaceAll("%player_suffix%", main.getChatHook().getSuffix(p));
				msg = msg.replaceAll("%player%", p.getName());
				if (arena.teamManager.findPlayer(p) != null) {
					msg = msg.replaceAll("%team_color%", arena.teamManager.findPlayer(p).team.Color.toChatColor().toString());
				}
				msg = msg.replaceAll("%team_color%", "");
				msg = main.c(msg);
				msg = msg.replaceAll("%msg%", message);
				arena.sendMessage(msg);
			}
			else if (arena.specs.contains(p)) {
				String msg = main.getMessageFetcher().getMessage("chat.game.spec", true);
				msg = msg.replaceAll("%player_prefix%", main.getChatHook().getPrefix(p));
				msg = msg.replaceAll("%player_suffix%", main.getChatHook().getSuffix(p));
				msg = msg.replaceAll("%player%", p.getName());
				msg = main.c(msg);
				msg = msg.replaceAll("%msg%", message);
				for (Player spec : arena.specs) {
					spec.sendMessage(msg);
				}
			}
			else if (global) {
				String msg = main.getMessageFetcher().getMessage("chat.game.global", true);
				msg = msg.replaceAll("%player_prefix%", main.getChatHook().getPrefix(p));
				msg = msg.replaceAll("%player_suffix%", main.getChatHook().getSuffix(p));
				msg = msg.replaceAll("%player%", p.getName());
				if (arena.teamManager.findPlayer(p) != null) {
					msg = msg.replaceAll("%team_color%", arena.teamManager.findPlayer(p).team.Color.toChatColor().toString());
					msg = msg.replaceAll("%team_display%", arena.teamManager.findPlayer(p).team.display);
				}
				msg = main.c(msg);
				msg = msg.replaceAll("%msg%", message);
				arena.sendMessage(msg);
			}
			else {
				ArenaTeam team = arena.teamManager.findPlayer(p);
				String msg = main.getMessageFetcher().getMessage("chat.game.team", true);
				msg = msg.replaceAll("%player_prefix%", main.getChatHook().getPrefix(p));
				msg = msg.replaceAll("%player_suffix%", main.getChatHook().getSuffix(p));
				msg = msg.replaceAll("%player%", p.getName());
				msg = msg.replaceAll("%team_color%", team.team.Color.toChatColor().toString());
				msg = msg.replaceAll("%team_display%", team.team.display);
				msg = main.c(msg);
				msg = msg.replaceAll("%msg%", message);
				team.sendMessage(msg);
			}
		}
		else {
			e.setCancelled(true);
			String msg = main.getMessageFetcher().getMessage("chat.lobby", true);
			msg = msg.replaceAll("%player_prefix%", main.getChatHook().getPrefix(p));
			msg = msg.replaceAll("%player_suffix%", main.getChatHook().getSuffix(p));
			msg = msg.replaceAll("%player%", p.getName());
			msg = main.c(msg);
			msg = msg.replaceAll("%msg%", e.getMessage());
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (!main.getArenaManager().isInGame(player)) {
					player.sendMessage(msg);
				}
			}
		}
	}
	
}

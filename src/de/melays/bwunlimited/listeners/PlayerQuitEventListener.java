package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;

public class PlayerQuitEventListener implements Listener{

	Main main;
	
	public PlayerQuitEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent e) {
		Player p = e.getPlayer();
		main.getGroupManager().getGroup(e.getPlayer()).leave(p);
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			arena.leave(p);
		}
		e.setQuitMessage(null);
	}
	
}

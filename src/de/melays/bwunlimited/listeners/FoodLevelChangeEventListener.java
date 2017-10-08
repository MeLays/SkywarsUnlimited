package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;

public class FoodLevelChangeEventListener implements Listener{

	Main main;
	
	public FoodLevelChangeEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockBreak(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.ENDING) {
				e.setFoodLevel(20);
				e.setCancelled(true);
			}
		}
		else if (!main.canOperateInLobby(p)) {
			e.setFoodLevel(40);
		}
	}
	
}

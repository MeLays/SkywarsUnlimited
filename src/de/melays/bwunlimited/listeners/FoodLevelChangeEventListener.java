package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.FoodLevelChangeEvent;

import de.melays.bwunlimited.Main;

public class FoodLevelChangeEventListener implements Listener{

	Main main;
	
	public FoodLevelChangeEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockBreak(FoodLevelChangeEvent e) {
		Player p = (Player) e.getEntity();
		if (main.getArenaManager().isInGame(p)) {
			
		}
		else if (!main.canOperateInLobby(p)) {
			e.setFoodLevel(40);
		}
	}
	
}

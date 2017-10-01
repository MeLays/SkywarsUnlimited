package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import de.melays.bwunlimited.Main;

public class CraftItemEventListener implements Listener{

	Main main;
	
	public CraftItemEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (main.getArenaManager().isInGame(p)) {
			//Arena arena = main.getArenaManager().searchPlayer(p);
			
			e.setCancelled(true);
			
		}
		else if (!main.canOperateInLobby(p)) {
			e.setCancelled(true);
		}
	}
	
}

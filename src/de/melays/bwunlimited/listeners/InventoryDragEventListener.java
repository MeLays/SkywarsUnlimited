package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.inventory.InventoryType;

import de.melays.bwunlimited.Main;

public class InventoryDragEventListener implements Listener{

	Main main;
	
	public InventoryDragEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryDragEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (main.getArenaManager().isInGame(p)) {
			if (e.getInventory().getType() == InventoryType.CRAFTING) {
				e.setCancelled(true);
			}
		}
	}
	
}

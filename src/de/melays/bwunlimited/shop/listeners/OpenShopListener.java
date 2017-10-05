package de.melays.bwunlimited.shop.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import de.melays.bwunlimited.shop.BedwarsShop;

/**
 * Listener for shop-opening-actions in case of
 * BedwarsShop being used as stand-alone plugin.
 * 
 * @author Coordu
 * @version 1.0 | 5. October 2017
 */
public class OpenShopListener implements Listener {
	/**
	 * Opens the shop if key to open shop is a block.
	 * @param event	PlayerInteractEvent
	 */
	@EventHandler
	public void onBlockClick(PlayerInteractEvent event) {
		
	}
	/**
	 * Opens the shop if key to open shop is an entity.
	 * @param event PlayerInteractEntityEvent
	 */
	@EventHandler
	public void onEntityClick(PlayerInteractEntityEvent event) {
		if (!(event.getRightClicked().getType() == EntityType.VILLAGER)) {
			return;
		}
		event.setCancelled(true);
		BedwarsShop.openShop(event.getPlayer());
	}
}

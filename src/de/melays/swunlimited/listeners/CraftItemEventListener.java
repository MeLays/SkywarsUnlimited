/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.CraftItemEvent;

import de.melays.swunlimited.Main;

public class CraftItemEventListener implements Listener{

	Main main;
	
	public CraftItemEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onCraftItem(CraftItemEvent e) {
		Player p = (Player) e.getWhoClicked();
		if (main.getArenaManager().isInGame(p)) {

			
		}
		else if (!main.canOperateInLobby(p)) {
			e.setCancelled(true);
		}
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;

import de.melays.bwunlimited.Main;

public class PlayerArmorStandManipulateEventListener implements Listener{

	Main main;
	
	public PlayerArmorStandManipulateEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockPlace(PlayerArmorStandManipulateEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {

			e.setCancelled(true);
			
		}
		else if (!main.canOperateInLobby(p)) {
			e.setCancelled(true);
		}
	}
	
}

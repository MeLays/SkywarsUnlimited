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
import org.bukkit.event.player.PlayerDropItemEvent;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.game.arenas.Arena;
import de.melays.swunlimited.game.arenas.state.ArenaState;

public class PlayerDropItemEventListener implements Listener{

	Main main;
	
	public PlayerDropItemEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerDropItem(PlayerDropItemEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			//Arena relevant Event stuff
			if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.ENDING) {
				e.setCancelled(true);
			}
			
		}
		else if (!main.canOperateInLobby(p)) {
			e.setCancelled(true);
		}
	}
	
}

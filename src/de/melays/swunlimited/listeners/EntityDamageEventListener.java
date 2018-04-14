/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.game.arenas.Arena;
import de.melays.swunlimited.game.arenas.state.ArenaState;

public class EntityDamageEventListener implements Listener{

	Main main;
	
	public EntityDamageEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onEntityDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player p = (Player) e.getEntity();
			if (main.getArenaManager().isInGame(p)) {
				Arena arena = main.getArenaManager().searchPlayer(p);
				
				//Arena relevant Event stuff
				if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.ENDING) {
					e.setCancelled(true);
				}
				else if (arena.state == ArenaState.INGAME) {
					if (arena.specs.contains(p)) {
						e.setCancelled(true);
					}
					else if (p.getHealth() - e.getDamage() <= 0) {
						e.setDamage(0);
						arena.deathManager.playerDeath(p);
					}
				}
			}
			else if (!main.canOperateInLobby(p)) {
				e.setCancelled(true);
			}
		}
		else if(e.getEntity().getType().equals(EntityType.ENDER_CRYSTAL)){
			e.setCancelled(true);
		}
		else if(main.getLobbyNPCManager().getLobbyNPC(e.getEntity()) != null){
			e.setCancelled(true);
		}
		else {
			
		}
	}
	
}

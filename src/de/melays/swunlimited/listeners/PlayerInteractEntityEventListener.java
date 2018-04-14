/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.challenges.Group;
import de.melays.swunlimited.npc.LobbyNPC;
import de.melays.swunlimited.npc.NPCType;

public class PlayerInteractEntityEventListener implements Listener{

	Main main;
	
	public PlayerInteractEntityEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (!main.canOperateInLobby(p)) {
			if (e.getRightClicked() instanceof Player) {
				Player clicked = (Player) e.getRightClicked();
				if (p.getInventory().getHeldItemSlot() == main.getConfig().getInt("lobby.group.slot")) {
					Group group = main.getGroupManager().getGroup(clicked);
					group.accept(p);
				}
			}
			e.setCancelled(true);
		}
	}
	
}

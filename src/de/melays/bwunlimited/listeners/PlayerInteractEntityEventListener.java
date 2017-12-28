/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.challenges.Group;
import de.melays.bwunlimited.npc.LobbyNPC;
import de.melays.bwunlimited.npc.NPCType;

public class PlayerInteractEntityEventListener implements Listener{

	Main main;
	
	public PlayerInteractEntityEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			if (!(e.getRightClicked().getType() == EntityType.VILLAGER)) {
				return;
			}
			main.getBedwarsShop().openShop(e.getPlayer());
			e.setCancelled(true);
		}
		else if (!main.canOperateInLobby(p)) {
			if (e.getRightClicked() instanceof Player) {
				Player clicked = (Player) e.getRightClicked();
				if (p.getInventory().getHeldItemSlot() == main.getConfig().getInt("lobby.group.slot")) {
					Group group = main.getGroupManager().getGroup(clicked);
					group.accept(p);
				}
			}
			else {
				Entity entity = e.getRightClicked();
				LobbyNPC npc = main.getLobbyNPCManager().getLobbyNPC(entity);
				if (npc != null) {
					if (npc.npc == NPCType.SETTINGS) {
						main.getLobbyManager().settings.get(p).openGUI();
					}
				}
			}
			e.setCancelled(true);
		}
	}
	
}

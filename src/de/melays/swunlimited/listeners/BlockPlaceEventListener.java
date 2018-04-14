/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.TNTPrimed;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.game.arenas.Arena;
import de.melays.swunlimited.game.arenas.ArenaTeam;
import de.melays.swunlimited.game.arenas.state.ArenaState;

public class BlockPlaceEventListener implements Listener{

	Main main;
	
	public BlockPlaceEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			//Arena relevant Event stuff
			if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.ENDING) {
				e.setCancelled(true);
			}
			else if (arena.state == ArenaState.INGAME) {
				if (!arena.blockManager.placeBlock(e.getBlock().getLocation() , e.getItemInHand())) {
					e.setCancelled(true);
				}
				if (e.getItemInHand().getType() == Material.CHEST) {
					if (!arena.lootManager.looted_before.contains(e.getBlock().getLocation()))
						arena.lootManager.looted_before.add(e.getBlock().getLocation());
				}
				Material tnt = Material.getMaterial(arena.main.getConfig().getString("game.special_items.TNT.item"));
				if (tnt != null) {
					if (e.getItemInHand().getType() == tnt) {
						if (arena.settings.tnt_auto_ignite) {
							e.getPlayer().getWorld().spawn(e.getBlock().getLocation(), TNTPrimed.class);
							if (p.getInventory().getItem(p.getInventory().getHeldItemSlot()).getAmount() > 1) {
								ItemStack set = p.getInventory().getItem(p.getInventory().getHeldItemSlot()).clone();
								set.setAmount(set.getAmount() - 1);
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), set);
							}
							else {
								ItemStack set = new ItemStack(Material.AIR);
								p.getInventory().setItem(p.getInventory().getHeldItemSlot(), set);
							}
							e.setCancelled(true);
						}
					}
				}
				
			}
			
		}
		else if (!main.canOperateInLobby(p)) {
			e.setCancelled(true);
		}
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.listeners;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.inventory.ItemStack;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.game.arenas.Arena;
import de.melays.swunlimited.game.arenas.state.ArenaState;

public class BlockBreakEventListener implements Listener{

	Main main;
	
	public BlockBreakEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			//Arena relevant Event stuff
			if (arena.state == ArenaState.LOBBY || arena.state == ArenaState.ENDING) {
				e.setCancelled(true);
			}
			else if (arena.state == ArenaState.INGAME) {
				if (!arena.blockManager.removeBlock(e.getBlock().getLocation(), p)) {
					e.setCancelled(true);
				}
				else {
					if (main.getConfig().getStringList("game.no_drop").contains(e.getBlock().getType().toString())) {
						e.getBlock().setType(Material.AIR);
					}
				}
			}
			
		}
		else if (!main.canOperateInLobby(p)) {
			e.setCancelled(true);
		}
		else {
			if (main.getTemplateSignManager().removeSign(e.getBlock().getLocation())) {
				p.sendMessage(main.prefix + "A sign has been removed from config.");
			}
		}
	}
	
}

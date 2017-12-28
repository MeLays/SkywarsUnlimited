/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.bwunlimited.listeners;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityExplodeEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.map_manager.ClusterTools;

public class EntityExplodeEventListener implements Listener {
	
	Main main;
	
	public EntityExplodeEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onEntityExlopdes(EntityExplodeEvent e) {
		
		Arena arena = null;
		
		//Search Arena
		for (Arena a : main.getArenaManager().getArenas()) {
			Location max = a.relative.clone().add(a.cluster.x_size , a.cluster.y_size , a.cluster.z_size);
			if (ClusterTools.isInAreaIgnoreHeight(e.getLocation(), a.relative, max)) {
				arena = a;
				break;
			}
		}
		
		e.setCancelled(true);
		
		if (arena != null) {
			e.setCancelled(false);
			for (Block b : new ArrayList<Block>(e.blockList())) {
				
				if (!arena.blockManager.placed_blocks.contains(b.getLocation()) && !arena.settings.tnt_detroy_map) {
					e.blockList().remove(b);
				}
				
			}
			
		}
	}

}

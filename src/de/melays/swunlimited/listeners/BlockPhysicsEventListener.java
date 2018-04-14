/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.listeners;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPhysicsEvent;

import de.melays.swunlimited.Main;

public class BlockPhysicsEventListener implements Listener{

	Main main;
	
	public BlockPhysicsEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onBlockPhysics(BlockPhysicsEvent e) {
		Block block = e.getBlock();
		if (block.getWorld().getName().equals(main.gameworld)) {
			if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
				if (!main.getConfig().getBoolean("physics.game.water-update")) {
					e.setCancelled(true);
				}
			}
			else {
				if (!main.getConfig().getBoolean("physics.game.other")) {
					e.setCancelled(true);
				}
			}
		}
		if (block.getWorld().getName().equals(main.presetworld)) {
			if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
				if (!main.getConfig().getBoolean("physics.presets.water-update")) {
					e.setCancelled(true);
				}
			}
			else {
				if (!main.getConfig().getBoolean("physics.presets.other")) {
					e.setCancelled(true);
				}
			}
		}
		if (block.getWorld().getName().equals(Bukkit.getWorlds().get(0).getName())) {
			if (block.getType() == Material.WATER || block.getType() == Material.STATIONARY_WATER) {
				if (!main.getConfig().getBoolean("physics.world.water-update")) {
					e.setCancelled(true);
				}
			}
			else {
				if (!main.getConfig().getBoolean("physics.world.other")) {
					e.setCancelled(true);
				}
			}
		}
	}
	
}

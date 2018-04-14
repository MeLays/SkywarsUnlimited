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
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import de.melays.swunlimited.Main;
import de.melays.swunlimited.game.SoundDebugger;
import de.melays.swunlimited.game.arenas.Arena;
import de.melays.swunlimited.game.arenas.state.ArenaState;
import de.melays.swunlimited.game.lobby.ArenaList;

public class InventoryClickEventListener implements Listener{

	Main main;
	
	public InventoryClickEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		
		Player p = (Player) e.getWhoClicked();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			if (e.getClickedInventory().getType() == InventoryType.CRAFTING) {
				e.setCancelled(true);
			}
			
			//Arena relevant Event stuff
			if (arena.state == ArenaState.LOBBY) {
				e.setCancelled(true);
				
				if (e.getClickedInventory().getName().equals(main.c(main.getSettingsManager().getFile().getString("gamelobby.inventory.teamselector.name")))) {
					if(arena.teamManager.getTeams().size() == 4) {
						if (e.getSlot() == 1) {
							arena.teamManager.setTeamSound(p, arena.teamManager.getTeams().get(0).team.name);
						}
						if (e.getSlot() == 3) {
							arena.teamManager.setTeamSound(p, arena.teamManager.getTeams().get(1).team.name);
						}
						if (e.getSlot() == 5) {
							arena.teamManager.setTeamSound(p, arena.teamManager.getTeams().get(2).team.name);
						}
						if (e.getSlot() == 7) {
							arena.teamManager.setTeamSound(p, arena.teamManager.getTeams().get(3).team.name);
						}
					}
					else if(arena.teamManager.getTeams().size() == 2) {
						if (e.getSlot() == 2) {
							arena.teamManager.setTeamSound(p, arena.teamManager.getTeams().get(0).team.name);
						}
						if (e.getSlot() == 6) {
							arena.teamManager.setTeamSound(p, arena.teamManager.getTeams().get(1).team.name);
						}
					}
					else {
						arena.teamManager.setTeamSound(p, arena.teamManager.getTeams().get(e.getSlot()).team.name);
					}
					arena.arenaLobby.openTeamMenu(p);
				}
				
			}
			else if (arena.state == ArenaState.ENDING) {
				e.setCancelled(true);				
			}
		}
		else if (!main.canOperateInLobby(p)) {
			e.setCancelled(true);
			if (!main.getRunningGames().player_list.containsKey(p)) main.getRunningGames().player_list.put(p, new ArenaList(main , p));
			else if (e.getClickedInventory().getName().equals(main.c(main.getSettingsManager().getFile().getString("lobby.inventory.running_games.title")))) {
				if (main.getRunningGames().slots.containsKey(e.getSlot())) {
					String category = main.getRunningGames().slots.get(e.getSlot());
					main.getRunningGames().player_list.get(p).openArenaList(category, 1);
					SoundDebugger.playSound(p, "CLICK", "BLOCK_DISPENSER_DISPENSE");
				}
			}
			else if (e.getClickedInventory().getName().equals(main.getRunningGames().player_list.get(p).title)) {
				if (main.getRunningGames().player_list.get(p).slots.containsKey(e.getSlot())) {
					int arena_id = main.getRunningGames().player_list.get(p).slots.get(e.getSlot());
					SoundDebugger.playSound(p, "CLICK", "BLOCK_DISPENSER_DISPENSE");
					if (main.getArenaManager().getArena(arena_id) != null) {
						main.getArenaManager().getArena(arena_id).addPlayer(p);
					}
					else {
						main.getRunningGames().openOverview(p);
					}
				}
			}
		}
	}
	
}

package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.SoundDebugger;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;
import de.melays.bwunlimited.game.lobby.ArenaList;

public class InventoryClickEventListener implements Listener{

	Main main;
	
	public InventoryClickEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e) {
		
		if (e.getClickedInventory().getName().equals(main.c(main.getBedwarsShop().getItemFile().getString("title"))) && e.getClickedInventory().getSize() == main.getBedwarsShop().getItemFile().getInt("size")) {
			main.getBedwarsShop().shopClick((Player)e.getWhoClicked(), e.getSlot() , e.isShiftClick() , (e.getClick() == ClickType.NUMBER_KEY) , e.getHotbarButton());
			e.setCancelled(true);
		}
		
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
			if (e.getClickedInventory().getName().equals(main.getArenaSelector().inv.get(p))) {
				if (e.getSlot() == 8 && main.getItemManager().isItem("lobby.inventory.next" , e.getCurrentItem())) {
					main.getArenaSelector().openArenaSelector(p, main.getArenaSelector().page.get(p)+1);
					SoundDebugger.playSound(p, "CLICK", "BLOCK_DISPENSER_DISPENSE");
				}
				else if (e.getSlot() == 0 && main.getItemManager().isItem("lobby.inventory.back" , e.getCurrentItem())) {
					main.getArenaSelector().openArenaSelector(p, main.getArenaSelector().page.get(p)-1);
					SoundDebugger.playSound(p, "CLICK", "BLOCK_DISPENSER_DISPENSE");
				}
				else if (e.getSlot() > 8) {
					int size = main.getArenaSelector().size.get(p);
					SoundDebugger.playSound(p, "CLICK", "BLOCK_DISPENSER_DISPENSE");
					main.getArenaSelector().selected.put(p, main.getLobbyManager().getSuitableArenas(main.getGroupManager().getGroup(p)).get(size).get(e.getSlot()-9));
					main.getArenaSelector().openArenaSelector(p, main.getArenaSelector().page.get(p));
				}
			}
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

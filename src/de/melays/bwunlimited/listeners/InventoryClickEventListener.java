package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;

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
			
			
		}
	}
	
}

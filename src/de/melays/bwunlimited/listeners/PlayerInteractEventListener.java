package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.game.arenas.Arena;
import de.melays.bwunlimited.game.arenas.state.ArenaState;

public class PlayerInteractEventListener implements Listener{

	Main main;
	
	public PlayerInteractEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			Arena arena = main.getArenaManager().searchPlayer(p);
			
			//Arena relevant Event stuff
			if (arena.state == ArenaState.LOBBY) {
				e.setCancelled(true);
				//Item Interact Check
				if (e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK 
						|| e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
					if (main.getItemManager().isItem("gamelobby.teamselector", e.getItem())) {
						//Teamchooser
						arena.arenaLobby.openTeamMenu(p);
					}
				}
			}
			
			
		}
	}
	
}

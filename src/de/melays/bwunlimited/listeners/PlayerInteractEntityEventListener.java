package de.melays.bwunlimited.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.melays.bwunlimited.Main;

public class PlayerInteractEntityEventListener implements Listener{

	Main main;
	
	public PlayerInteractEntityEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerInteractEntity(PlayerInteractEntityEvent e) {
		Player p = e.getPlayer();
		if (main.getArenaManager().isInGame(p)) {
			
		}
		else if (!main.canOperateInLobby(p)) {
			e.setCancelled(true);
			if (e.getRightClicked() instanceof Player) {
				//Player at = (Player) e.getRightClicked();
				if (p.getItemInHand() == null) {
					return;
				}
				if (!main.getItemManager().isItem("lobby.challenger", p.getItemInHand())) {
					return;
				}
			}
		}
	}
	
}

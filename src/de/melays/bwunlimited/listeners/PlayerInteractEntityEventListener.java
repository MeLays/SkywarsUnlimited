package de.melays.bwunlimited.listeners;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEntityEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.challenges.Group;
import de.melays.bwunlimited.shop.BedwarsShop;

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
			e.setCancelled(true);
			BedwarsShop.openShop(p);
		}
		else if (!main.canOperateInLobby(p)) {
			if (!(e.getRightClicked() instanceof Player)) return;
			Player clicked = (Player) e.getRightClicked();
			if (p.getInventory().getHeldItemSlot() == main.getConfig().getInt("lobby.group.slot")) {
				Group group = main.getGroupManager().getGroup(clicked);
				group.accept(p);
			}
			e.setCancelled(true);
		}
	}
	
}

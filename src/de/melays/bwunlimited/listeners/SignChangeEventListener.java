package de.melays.bwunlimited.listeners;

import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

import de.melays.bwunlimited.Main;
import de.melays.bwunlimited.map_manager.error.UnknownClusterException;

public class SignChangeEventListener implements Listener{

	Main main;
	
	public SignChangeEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onSignChange(SignChangeEvent e) {
		if (!main.canOperateInLobby(e.getPlayer())) {
			e.setCancelled(true);
		}
		else {
			Player p = e.getPlayer();
			if (e.getBlock().getState() instanceof Sign) {
				if (e.getLine(0).equalsIgnoreCase("[TEMPLATE]") && p.hasPermission("bwunlimited.templatesign.create")) {
					try {
						int id = main.getTemplateSignManager().saveSign(e);
						p.sendMessage(main.prefix + "The template-sign '"+id+"' has been created successfully!");
					} catch (UnknownClusterException e1) {
						p.sendMessage(main.prefix + "This cluster doesn't exist!");
					}
				}
			}
		}
	}
	
}

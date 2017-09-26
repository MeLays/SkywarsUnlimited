package de.melays.bwunlimited.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import de.melays.bwunlimited.Main;

public class PlayerDropItemEventListener implements Listener{

	Main main;
	
	public PlayerDropItemEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		
	}
	
}

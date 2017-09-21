package de.melays.bwunlimited.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;

import de.melays.bwunlimited.Main;

public class CreatureSpawnEventListener implements Listener{

	Main main;
	
	public CreatureSpawnEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onCreatureSpawn(CreatureSpawnEvent e) {
		if (!main.getConfig().getBoolean("creature_spawn") && (e.getSpawnReason() == SpawnReason.CHUNK_GEN || e.getSpawnReason() == SpawnReason.NATURAL || e.getSpawnReason() == SpawnReason.DEFAULT)) {
			e.setCancelled(true);
		}
	}
	
}

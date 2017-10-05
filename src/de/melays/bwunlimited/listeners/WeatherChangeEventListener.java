package de.melays.bwunlimited.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import de.melays.bwunlimited.Main;

public class WeatherChangeEventListener implements Listener {
	
	Main main;
	
	public WeatherChangeEventListener (Main main) {
		this.main = main;
	}
	
	@EventHandler
	public void onWeatherChangeEvent(WeatherChangeEvent e) {
		if (!main.getConfig().getBoolean("worlds.allow_rain")) {
			e.getWorld().setThundering(false);
			e.setCancelled(true);
		}
	}
	
}

/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.weather.WeatherChangeEvent;

import de.melays.swunlimited.Main;

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

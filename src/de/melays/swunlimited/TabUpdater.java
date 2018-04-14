/*******************************************************************************
 * Copyright (C) Philipp Seelos - All Rights Reserved
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential
 * Written by Philipp Seelos <seelos@outlook.com>, December 2017
 ******************************************************************************/
package de.melays.swunlimited;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.melays.swunlimited.colortab.ColorTabAPI;

public class TabUpdater {
	
	Main main;
	
	public TabUpdater(Main main) {
		this.main = main;
		if (main.getConfig().getBoolean("tab_header_and_footer"))
			Bukkit.getScheduler().scheduleSyncRepeatingTask(main, new Runnable() {
				@Override
				public void run() {
					for (Player p : Bukkit.getOnlinePlayers())
						update(p);
					main.getLobbyManager().updateLobbyTab();
				}
			},0, 20);
	}
	
	public void update(Player p) {
		
		//TODO Stats variables
		List<String> header = main.getSettingsManager().getFile().getStringList("tab.header");
		for (int i = 0 ; i < header.size() ; i++) {
			if (header.get(i).contains("%server%")) {
				header.set(i, header.get(i).replaceAll("%server%", main.getBungeeServerFetcher().servername));
			}
		}
		List<String> footer = main.getSettingsManager().getFile().getStringList("tab.footer");
		for (int i = 0 ; i < footer.size() ; i++) {
			if (footer.get(i).contains("%server%")) {
				footer.set(i, header.get(i).replaceAll("%server%", main.getBungeeServerFetcher().servername));
			}
		}
		ArrayList<Player> ps = new ArrayList<Player>();
		ps.add(p);
		ColorTabAPI.setHeaderAndFooter(header, footer, ps);
		
	}
	
}

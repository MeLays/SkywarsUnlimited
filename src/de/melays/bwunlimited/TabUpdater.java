package de.melays.bwunlimited;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import de.melays.bwunlimited.colortab.ColorTabAPI;

public class TabUpdater {
	
	Main main;
	
	public TabUpdater(Main main) {
		this.main = main;
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
		List<String> footer = main.getSettingsManager().getFile().getStringList("tab.footer");
		ArrayList<Player> ps = new ArrayList<Player>();
		ps.add(p);
		ColorTabAPI.setHeaderAndFooter(header, footer, ps);
		
	}
	
}
